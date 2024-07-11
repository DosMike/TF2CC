package com.github.dosmike.msq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MasterServerQuery {

    static Logger logger = LoggerFactory.getLogger("MSQ");

    public enum Region {
        USE(0),
        USW(1),
        SA(2),
        EU(3),
        AS(4),
        AU(5),
        ME(6),
        AF(7),
        ANY(-1)
        ;
        final byte bval;
        Region(int bval) {this.bval = (byte)bval;}
        public byte getByteValue() {return bval;}
        public static Region ofByteValue(int value) { for (Region r : values()) if (r.bval == value) return r; return Region.ANY; }
    }

    private String filter;
    private String lastIP = "0.0.0.0:0";
    private Region region = Region.ANY;

    private final List<InetSocketAddress> serverList = new LinkedList<>();

    private static final InetSocketAddress masterServer = new InetSocketAddress("hl2master.steampowered.com", 27011);

    private MasterServerQuery() {
    }

    public int update() {
        List<InetSocketAddress> result = new LinkedList<>();
        DatagramSocket socket = null;
        logger.info("Querying for region {} (Filter: {})", region.name(), filter);
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
            lastIP = "0.0.0.0:0";

            do {
                request(socket);
                result.addAll(response(socket));
            } while (!lastIP.equals("0.0.0.0:0"));

        } catch (IOException e) {
            logger.error("Failed to query master server", e);
            return -1;
        } finally {
            if (socket != null) try { socket.close(); } catch (Exception ignore) {}
        }

        synchronized (serverList) {
            serverList.clear();
            serverList.addAll(result);
        }
        return result.size();
    }

    public Collection<InetSocketAddress> getServers() {
        synchronized (serverList) {
            return serverList;
        }
    }

    private void request(DatagramSocket socket) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0x31);
        baos.write(region.getByteValue());
        baos.write(lastIP.getBytes(StandardCharsets.US_ASCII));
        baos.write(0);
        baos.write(filter.getBytes(StandardCharsets.US_ASCII));
        baos.write(0);
        DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.size(), masterServer.getAddress(), masterServer.getPort());
        socket.send(packet);
    }
    private Collection<InetSocketAddress> response(DatagramSocket socket) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
        socket.receive(packet);
        ByteBuffer bb = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
        bb.order(ByteOrder.BIG_ENDIAN);
        List<InetSocketAddress> entries = new LinkedList<>();
        String address = (bb.get() & 0xff) + "." + (bb.get() & 0xff) + "." + (bb.get() & 0xff) + "." + (bb.get() & 0xff);
        int port = (bb.getShort() & 0xFFFF);
        if (!address.equals("255.255.255.255") || port != 0x660A) {
            throw new IOException("Invalid response header: "+address+":"+port);
        }
        while (bb.remaining() >= 6) {
            address = (bb.get() & 0xff) + "." + (bb.get() & 0xff) + "." + (bb.get() & 0xff) + "." + (bb.get() & 0xff);
            port = (bb.getShort() & 0xFFFF);
            if (!"0.0.0.0".equals(address) && port != 0)
                entries.add(new InetSocketAddress(address, port));
            //logger.info("Added Server {}:{}", address, port);
        }
        lastIP = address+":"+port;
        logger.info("Added {} servers, last {}", entries.size(), lastIP);
        return entries;
    }

    public static class Builder {
        private Region region = Region.ANY;
        private StringBuilder filter = new StringBuilder();

        public Builder region(Region region) {
            this.region = region;
            return this;
        }
        public Builder filterNor(int n) {
            this.filter.append("\\nor\\").append(n);
            return this;
        }
        public Builder filterNand(int n) {
            this.filter.append("\\nand\\").append(n);
            return this;
        }
        public Builder filterDedicated() {
            this.filter.append("\\dedicated\\1");
            return this;
        }
        public Builder filterSecure() {
            this.filter.append("\\secure\\1");
            return this;
        }
        public Builder filterGamedir(String gamedir) {
            this.filter.append("\\gamedir\\").append(gamedir);
            return this;
        }
        public Builder filterMap(String map) {
            this.filter.append("\\map\\").append(map);
            return this;
        }
        public Builder filterLinux() {
            this.filter.append("\\linux\\1");
            return this;
        }
        public Builder filterNoPassword() {
            this.filter.append("\\password\\0");
            return this;
        }
        public Builder filterNotEmpty() {
            this.filter.append("\\empty\\1");
            return this;
        }
        public Builder filterNotFull() {
            this.filter.append("\\full\\1");
            return this;
        }
        public Builder filterSpecProxy() {
            this.filter.append("\\proxy\\1");
            return this;
        }
        public Builder filterAppid(int appid) {
            this.filter.append("\\appid\\").append(appid);
            return this;
        }
        public Builder filterNotAppid(int appid) {
            this.filter.append("\\napp\\").append(appid);
            return this;
        }
        public Builder filterEmpty() {
            this.filter.append("\\noplayers\\1");
            return this;
        }
        public Builder filterWhitelisted() {
            this.filter.append("\\white\\1");
            return this;
        }
        public Builder filterTags(Collection<String> tags) {
            this.filter.append("\\gametype\\").append(String.join(",", tags));
            return this;
        }
        public Builder filterTagsHidden(Collection<String> tags) {
            this.filter.append("\\gamedata\\").append(String.join(",", tags));
            return this;
        }
        public Builder filterTagsHiddenAny(Collection<String> tags) {
            this.filter.append("\\gamedataor\\").append(String.join(",", tags));
            return this;
        }
        public Builder filterNameMatch(String name) {
            this.filter.append("\\name_match\\").append(name);
            return this;
        }
        public Builder filterVersionMatch(String version) {
            this.filter.append("\\version_match\\").append(version);
            return this;
        }
        public Builder filterCollapseAddress() {
            this.filter.append("\\collapse_addr_hash\\1");
            return this;
        }
        public Builder filterAddress(String address) {
            this.filter.append("\\gameaddr\\").append(address);
            return this;
        }

        public MasterServerQuery build() {
            MasterServerQuery query = new MasterServerQuery();
            query.region = region;
            query.filter = filter.toString();
            return query;
        }
    }
    public static Builder builder() {
        return new Builder();
    }

}
