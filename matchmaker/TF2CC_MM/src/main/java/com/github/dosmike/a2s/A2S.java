package com.github.dosmike.a2s;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class A2S {

    public static interface SocketFactory {
        DatagramSocket create() throws IOException;
    }
    public static final SocketFactory DEFAULT_SOCKET_FACTORY = ()->{
        DatagramSocket socket = new DatagramSocket();
        socket.setReuseAddress(true);
        socket.setSoTimeout(5000);
        socket.setReceiveBufferSize(15000);
        return socket;
    };

    static Map<InetSocketAddress, Long> expectedGame = new HashMap<>();

    /**
     * Some games implement weird extras. In order to help parsing those, you can
     * optionally provide an expected game steamAppId.
     * I think steamAppId should be the client, not the dedi.
     * @param ip address of the server
     * @param port port of the server
     * @param steamAppId the numerical steamAppId, 0 to clear
     */
    public static void setExpectedGame(String ip, int port, long steamAppId) throws UnknownHostException {
        setExpectedGame(new InetSocketAddress(InetAddress.getByName(ip), port), steamAppId);
    }
    /**
     * Some games implement weird extras. In order to help parsing those, you can
     * optionally provide an expected game steamAppId.
     * I think steamAppId should be the client, not the dedi.
     * @param address server ip:port
     * @param steamAppId the numerical steamAppId, 0 to clear
     */
    public static void setExpectedGame(InetSocketAddress address, long steamAppId) {
        if (steamAppId == 0)
            expectedGame.remove(address);
        else
            expectedGame.put(address, steamAppId);
    }

    /**
     * You can override socket creation to specify your own timeouts and stuff. But do not connect the socket!
     * For retrieving rules or other large messages, the socket factory should have a receive-buffer large enough to
     * hold all fragments, so they can be processed without being dropped.
     */
    public static void installDatagramSocketFactory(SocketFactory factory) {
        QueryHandler.socketFactory = factory;
    }

    public static ServerInfo info(String ip, int port) throws IOException, SocketTimeoutException {
        return info(new InetSocketAddress(InetAddress.getByName(ip), port));
    }
    public static ServerInfo info(InetSocketAddress address) throws IOException, SocketTimeoutException {
        byte[] query = "Source Engine Query\0".getBytes(StandardCharsets.UTF_8);
        return new QueryHandler(address, A2SMessage.MessageType.A2S_INFO).request(ServerInfo.class, query);
    }

    public static PlayerList players(String ip, int port) throws IOException, SocketTimeoutException {
        return players(new InetSocketAddress(InetAddress.getByName(ip), port));
    }
    public static PlayerList players(InetSocketAddress address) throws IOException, SocketTimeoutException {
        return new QueryHandler(address, A2SMessage.MessageType.A2S_PLAYER).request(PlayerList.class, new byte[0]);
    }

    public static Rules rules(String ip, int port) throws IOException, SocketTimeoutException {
        return rules(new InetSocketAddress(InetAddress.getByName(ip), port));
    }
    public static Rules rules(InetSocketAddress address) throws IOException, SocketTimeoutException {
        return new QueryHandler(address, A2SMessage.MessageType.A2S_RULES).request(Rules.class, new byte[0]);
    }

}
