package com.github.dosmike.tf2cc.lobby;

import com.github.dosmike.tf2cc.Main;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ClientManager {

    static final Logger logger = LoggerFactory.getLogger("ClientMgr");

    private static Set<Client> clients = new HashSet<>(1000);
    private static Map<WebSocket, Client> connectionCache = new HashMap<>();

    public static Client getBySession(String session, WebSocket socket) {
        Client res = clients.stream().filter(c->c.getSession().equals(session)).findAny().orElse(null);
        if (res == null) {
            logger.info("Client not found by session, creating new instance");
            try {
                Client newClient = new Client(socket, session);
                Optional<Client> existing = getByAccount(newClient.getAccountid());
                existing.ifPresent(client -> {
                    logger.info("New session for tracked account id - updating socket");
                    if (client.socket != null && client.socket.isOpen()) {
                        connectionCache.remove(client.socket);
                        client.socket.close(4000, "Session has moved to different login");
                    }
                    client.setSession(session);
                    client.updateSocket(socket);
                });
                if (existing.isEmpty()) {
                    clients.add(newClient);
                }
                res = existing.orElse(newClient);
            } catch (Exception e) {
            }
        } else {
            res.updateSocket(socket);
        }
        if (res == null) {
            throw new RuntimeException("Failed to connect to session");
        }
        connectionCache.put(socket, res);
        return res;
    }

    public static void uncache(WebSocket socket) {
        connectionCache.remove(socket);
    }

    public static Optional<Client> getByAccount(long accountId) {
        return clients.stream().filter(c->c.getAccountid() == accountId).findAny();
    }

    public static Optional<Client> getBySocket(WebSocket socket) {
        return Optional.ofNullable(connectionCache.get(socket));
    }

    public static Collection<Client> getClients() {
        return clients;
    }

    public static void deadConnectionLooper() {
        while (true) {
            Main.waitMs(5000);
            dropDead();
        }
    }
    public static void dropDead() {
        List<Client> ded = clients.stream().filter(Client::isTimedOut).toList();
        ded.forEach(c-> {
            logger.info("Dropped dead connection for {}", c.getAccountid());
            LobbyManager.getFor(c).ifPresent(l->l.remove(c));
            clients.remove(c);
            WebSocket sock = c.getSocket();
            if (sock != null) connectionCache.remove(sock);
        });
    }

    public static void sendToAll(String message) {
        Set<WebSocket> sockets = new HashSet<>(connectionCache.keySet());
        for (WebSocket s : sockets) {
            if (s.isOpen() && !s.isClosing())
                s.send("SYSNOTIF "+message);
        }
    }

}
