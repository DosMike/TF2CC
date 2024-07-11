package com.github.dosmike.a2s;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class A2SMessage {

    interface A2SDeserializable {}

    protected enum MessageType {
        A2S_INFO('T', 'T', 'I'),
        A2S_PLAYER('U', 'U', 'D'),
        A2S_RULES('V', 'V', 'E'),
        S2C_CHALLENGE('A', 'A', 'A'),
        S2C_INFO('I', 'T', 'I'),
        S2C_PLAYER('D', 'U', 'D'),
        S2C_RULES('E', 'V', 'E'),
        ;
        private final char headerByte;
        private final char requestType;
        private final char responseType;
        MessageType(char c, char r, char t) { headerByte = c; requestType = r; responseType = t; }
        public MessageType requestType() { return fromHeader(requestType); }
        public MessageType responseType() { return fromHeader(responseType); }
        static MessageType fromHeader(char b) {
            return switch (b) {
                case 'T' -> A2S_INFO;
                case 'U' -> A2S_PLAYER;
                case 'V' -> A2S_RULES;
                case 'A' -> S2C_CHALLENGE;
                case 'I' -> S2C_INFO;
                case 'D' -> S2C_PLAYER;
                case 'E' -> S2C_RULES;
                default -> null;
            };
        }
    }

    protected static final int MESSAGE_SINGLE = -1;
    protected static final int MESSAGE_MULTIPLE = -2;

    static class FramgentList extends ArrayList<Fragment> {

        boolean[] received;
        FramgentList(Fragment first) throws IOException {
            super(first.total);
            received = new boolean[first.total];
            for (int i=0;i<first.total;i++) this.add(new Fragment());
            this.set(first.number, first);
        }

        @Override
        public Fragment set(int index, Fragment element) {
            received[index] = true;
            return super.set(index, element);
        }

        public void insert(Fragment fragment) {
            set(fragment.number, fragment);
        }

        public boolean isComplete() {
            for (boolean b : received) {
                if (!b) return false;
            }
            return true;
        }

        ByteBuffer complete() throws IOException {
            for (boolean b : received) {
                if (!b) throw new IOException("Message did not complete");
            }
            int number = 0;
            for (Fragment fragment : this) number += fragment.payload.length;
            ByteBuffer bb = ByteBuffer.allocate(number);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            byte[] buffer = bb.array();
            number = 0;
            for (Fragment fragment : this) {
                System.arraycopy(fragment.payload, 0, buffer, number, fragment.payload.length);
                number += fragment.payload.length;
            }
            if (this.get(0).compressed) {
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                BZip2CompressorInputStream inflateStream = new BZip2CompressorInputStream(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //IOUtils.copy(inflateStream, baos, 2048);
                inflateStream.transferTo(baos);
                //don't know the crc32 algo used to verify
                if (baos.size() != this.get(0).inflatedSize)
                    throw new IOException("Message Decompression error (Expected "+this.get(0).inflatedSize+" bytes, got "+baos.size()+")");
                bb = ByteBuffer.wrap(baos.toByteArray());
                bb.order(ByteOrder.LITTLE_ENDIAN);
            }
            //fix broken double wrapped single part header send in multipart message
            if (bb.remaining() > 4 && bb.getInt() != -1) {
                bb.position(bb.position()-4);
            }
            return bb;
        }

    }

    static class Fragment {
        int flag, id, total, number, size=1248, inflatedSize, crc32;
        boolean compressed;
        byte[] payload;

        /** default constructor for dummy fragment to space out the fragment list */
        Fragment() {
            flag = 0; id = 0; total = 0; number = 0; size = 0; inflatedSize = 0; crc32 = 0;
            compressed = false; payload = new byte[0];
        }
        Fragment(ByteBuffer buffer, boolean expectSize) throws IOException {
            flag = buffer.getInt();
            if (flag == MESSAGE_MULTIPLE) {
                id = buffer.getInt();
                compressed = (id & 0x80000000)!=0;
                total = buffer.get() & 0x0ff;
                number = buffer.get() & 0x0ff;
                if (expectSize) size = buffer.getShort();
                if (number==0 && compressed) {
                    inflatedSize = buffer.getInt();
                    crc32 = buffer.getInt();
                }
            } else if (flag != MESSAGE_SINGLE) {
                throw new IOException("Invalid header int");
            }
            payload = new byte[buffer.remaining()];
            buffer.get(buffer.position(), payload, 0, buffer.remaining());
        }

        @Override
        public String toString() {
            return "Fragment{" +
                    "flag=" + flag +
                    ", id=" + id +
                    ", compressed=" + compressed +
                    ", total=" + total +
                    ", number=" + number +
                    ", size=" + size +
                    ", inflatedSize=" + inflatedSize +
                    ", crc32=" + crc32 +
                    ", payload[" + payload.length + ']' +
                    '}';
        }

        /** gets payload only */
        ByteBuffer toBuffer() {
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            return buffer;
        }
    }

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    A2SMessage() {}

    A2SMessage(MessageType type, boolean fragment) {
        putInt(fragment ? MESSAGE_MULTIPLE : MESSAGE_SINGLE);
        putByte(type.headerByte);
    }


    private static ByteBuffer recvfrom(DatagramSocket socket) throws IOException, SocketTimeoutException {
        byte[] rxbuf = new byte[150000];
        DatagramPacket packet = new DatagramPacket(rxbuf, 0, rxbuf.length);
        socket.receive(packet);
        ByteBuffer buffer = ByteBuffer.wrap(rxbuf, 0, packet.getLength());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }
    /**
     * receive a single or multipart A2S message payload.
     * the fragment size field is not always available, set expectSize = false if not.
     */
    public static ByteBuffer receive(DatagramSocket socket, boolean expectSize) throws IOException, SocketTimeoutException {
        A2SMessage.Fragment fragment = new A2SMessage.Fragment(recvfrom(socket), expectSize);
        if (fragment.flag == A2SMessage.MESSAGE_SINGLE) {
            return fragment.toBuffer();
        }
        A2SMessage.FramgentList fragments = new A2SMessage.FramgentList(fragment);
        while (!fragments.isComplete()) {
            fragments.insert(new A2SMessage.Fragment(recvfrom(socket), expectSize));
        }
        return fragments.complete();
    }

    void append(byte[] data) {
        baos.writeBytes(data);
    }
    void putByte(char value) { baos.write(value); }
    void putInt(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        baos.writeBytes(buffer.array());
    }

    DatagramPacket toDatagram() {
        return new DatagramPacket(baos.toByteArray(), 0, baos.size());
    }

    public static String getString(ByteBuffer buffer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
        int next;
        while (buffer.hasRemaining() && (next = buffer.get()) != 0) {
            baos.write(next);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

}
