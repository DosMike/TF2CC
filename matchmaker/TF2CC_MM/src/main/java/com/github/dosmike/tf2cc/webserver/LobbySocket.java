package com.github.dosmike.tf2cc.webserver;

import com.github.dosmike.msq.MasterServerQuery;
import com.github.dosmike.tf2cc.GameData;
import com.github.dosmike.tf2cc.Main;
import com.github.dosmike.tf2cc.lobby.Client;
import com.github.dosmike.tf2cc.lobby.ClientManager;
import com.github.dosmike.tf2cc.lobby.Lobby;
import com.github.dosmike.tf2cc.lobby.LobbyManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LobbySocket extends WebSocketServer {

    Logger logger = LoggerFactory.getLogger("LobbySock");

    public LobbySocket() {
        super(new InetSocketAddress(Main.SocketPort));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.debug("New connection: "+webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.debug("Closed connection: "+webSocket.getRemoteSocketAddress()+" ("+i+") "+s);
        ClientManager.getBySocket(webSocket).ifPresent(c->{
            c.keepAlive(); //don't drop right away
            c.updateSocket(null);
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        Client client;
        Lobby lobby;
        if (s.startsWith("HELO ")) {
            String session = s.substring(5);
            client = ClientManager.getBySession(session, webSocket);
            lobby = LobbyManager.getFor(client).orElseGet(()->LobbyManager.makeNew(client));
            lobby.sendToAllBut(client, "JOIN "+client.getAccountid());
            lobby.sendListTo(client);
            logger.info("Created Lobby {} for {} @ {} {}", lobby.getCode(), client.getAccountid(), client.getSession(), webSocket.getRemoteSocketAddress());
            return;
        } else {
            client = ClientManager.getBySocket(webSocket).orElse(null);
            if (client == null) {
                webSocket.close(4000, "Protocol error");
                return;
            }
            lobby = LobbyManager.getFor(client).orElse(null);
            if (lobby == null) {
                webSocket.close(4000, "Internal error");
                return;
            }
        }
        logger.info("{}/{} > {}", lobby.getCode(), client.getAccountid(), s);
        client.keepAlive();

        String[] cmd = s.split(" ");
        if (cmd[0].equals("JOIN")) {
            if (cmd.length != 2) {
                client.send("ERROR Invalid lobby code"); //assume trim error
                return;
            }
            Lobby target = LobbyManager.getByCode(cmd[1]).orElse(null);
            if (target == null) {
                client.send("ERROR Invalid lobby code");
                return;
            }
            if (!lobby.getCode().equals(target.getCode())) {
                lobby.remove(client);
                target.add(client);
            }
        } else if (cmd[0].equals("LEAVE")) {
            if (cmd.length != 1) {
                webSocket.close(4000, "Protocol error");
                return;
            }

            lobby.remove(client);
            lobby = LobbyManager.makeNew(client);
        } else if (cmd[0].equals("KICK")) {
            if (cmd.length != 2) {
                webSocket.close(4000, "Protocol error");
                return;
            }
            if (!lobby.isOwner(client))
                return;

            Client target;
            try {
                target = ClientManager.getByAccount(Long.parseLong(cmd[1])).orElseThrow(()->new RuntimeException("Client not online"));
            } catch (NumberFormatException e) {
                webSocket.close(4000, "Protocol error");
                return;
            } catch (RuntimeException e) {
                webSocket.send("ERROR Client no longer online");
                return;
            }

            lobby.remove(target);
            lobby = LobbyManager.makeNew(target);
            lobby.sendListTo(target);
        } else if (cmd[0].equals("READY")) {
            if (cmd.length != 2) {
                webSocket.close(4000, "Protocol error");
                return;
            }

            lobby.setReady(client, !cmd[1].equals("0"));
        } else if (cmd[0].equals("CONFIG")) {
            if (cmd.length < 2) {
                webSocket.close(4000, "Protocol error");
                return;
            } else if (cmd[1].equals("SYNC")) {
                client.send("CONFIG = "+lobby.getRegions().stream().map(Enum::name).collect(Collectors.joining(" "))
                        +" "+lobby.getMaps().stream().map(GameData.Maps::getMapName).collect(Collectors.joining(" ")));
                return;
            }
            if (!lobby.isOwner(client))
                return;

            boolean add=true;
            if (cmd[1].equals("-"))
                add = false;
            else if (!cmd[1].equals("+")) {
                webSocket.close(4000, "Protocol error");
                return;
            }

            List<GameData.Maps> maps = new LinkedList<>();
            List<MasterServerQuery.Region> regions = new LinkedList<>();
            for (int i = 2; i < cmd.length; i++) {
                for (MasterServerQuery.Region r : MasterServerQuery.Region.values())
                    if (r != MasterServerQuery.Region.ANY && r.name().equalsIgnoreCase(cmd[i]))
                        regions.add(r);
                for (GameData.Maps m : GameData.Maps.values())
                    if (m.getMapName().equals(cmd[i]))
                        maps.add(m);
            }
            if (add) {
                maps.removeAll(lobby.getMaps());
                lobby.getMaps().addAll(maps);
                regions.removeAll(lobby.getRegions());
                lobby.getRegions().addAll(regions);
                if (!maps.isEmpty() || !regions.isEmpty())
                    lobby.send("CONFIG + "+regions.stream().map(Enum::name).collect(Collectors.joining(" "))
                            +" "+maps.stream().map(GameData.Maps::getMapName).collect(Collectors.joining(" ")));
            } else {
                maps.retainAll(lobby.getMaps());
                lobby.getMaps().removeAll(maps);
                regions.retainAll(lobby.getRegions());
                lobby.getRegions().removeAll(regions);
                if (!maps.isEmpty() || !regions.isEmpty())
                    lobby.send("CONFIG - "+regions.stream().map(Enum::name).collect(Collectors.joining(" "))
                            +" "+maps.stream().map(GameData.Maps::getMapName).collect(Collectors.joining(" ")));
            }

        } else {
            webSocket.close(4000, "Protocol error");
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error("Socket: "+webSocket, e);
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(10);
    }
}
