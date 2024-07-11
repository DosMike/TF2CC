package com.github.dosmike.a2s;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

class QueryHandler {

    private static final Map<InetSocketAddress, Object> lockTable = new HashMap<>();

    private final InetSocketAddress address;
    private final A2SMessage.MessageType requestType;
    private final boolean expectSize;

    static A2S.SocketFactory socketFactory = A2S.DEFAULT_SOCKET_FACTORY;

    QueryHandler(InetSocketAddress address, A2SMessage.MessageType requestType) {
        long expectedApp = A2S.expectedGame.getOrDefault(address, 0L);
        this.address = address;
        this.requestType = requestType;
        this.expectSize = !(
                // https://developer.valvesoftware.com/wiki/Server_queries
                // under Protocol -> Multi-packet Response Format -> Source Servers
                ServerInfo.SteamAppId.SourceSDK2006.id == expectedApp ||
                ServerInfo.SteamAppId.EternalSilence.id == expectedApp ||
                ServerInfo.SteamAppId.InsurgencyMod.id == expectedApp
                // ServerInfo.SteamAppId.CounterStrikeSource.id == expectedApp if protocol = 7, idk
        );
    }

    <T> T request(Class<T> tClass, byte[] payload) throws IOException, SocketTimeoutException {
        Object mutex;
        if (lockTable.containsKey(address)) {
            mutex = lockTable.get(address);
        } else {
            lockTable.put(address, mutex = new Object());
        }

        synchronized (mutex) {
            try (DatagramSocket socket = socketFactory.create()) {
                socket.connect(address);
                A2SMessage msg;

                msg = new A2SMessage(requestType, false);
                if (payload.length != 0) msg.append(payload);
                msg.putInt(-1);
                socket.send(msg.toDatagram());

                ByteBuffer reply = A2SMessage.receive(socket, expectSize);
                A2SMessage.MessageType type = A2SMessage.MessageType.fromHeader((char)reply.get());
                if (type == A2SMessage.MessageType.S2C_CHALLENGE) {
                    int challenge = reply.getInt();
                    msg = new A2SMessage(requestType, false);
                    if (payload.length != 0) msg.append(payload);
                    msg.putInt(challenge);
                    socket.send(msg.toDatagram());

                    reply = A2SMessage.receive(socket, expectSize);
                    type = A2SMessage.MessageType.fromHeader((char)reply.get());
                }

                if (type != requestType.responseType())
                    throw new IOException("Expected "+ requestType.responseType().name()+", got "+type.name());

                long expectedGame = A2S.expectedGame.getOrDefault(address, 0L);
                try {
                    Constructor<T> constructor = tClass.getDeclaredConstructor(ByteBuffer.class, Long.TYPE);
                    constructor.setAccessible(true);
                    return constructor.newInstance(reply, expectedGame);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
