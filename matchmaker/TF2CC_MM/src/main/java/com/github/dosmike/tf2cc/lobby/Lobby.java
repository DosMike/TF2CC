package com.github.dosmike.tf2cc.lobby;

import com.github.dosmike.msq.MasterServerQuery;
import com.github.dosmike.tf2cc.GameData;
import com.github.dosmike.tf2cc.gameserver.QueueManager;
import com.github.dosmike.tf2cc.gameserver.Server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Lobby {

    private ArrayList<Client> clients = new ArrayList<>(GameData.MaxLobbySize);
    private int ready = 0;
    private String code;
    private LinkedList<GameData.Maps> searchPool = new LinkedList<>();
    private LinkedList<MasterServerQuery.Region> searchRegions = new LinkedList<>();
    private String gameAddress = null;

    Lobby() {
        code = LobbyManager.codeGen.generate();
    }

    public void send(String text) {
        for (Client client : clients) {
            client.send(text);
        }
    }
    public void sendToAllBut(Client notMe, String text) {
        for (Client client : clients) {
            if (client.getAccountid() == notMe.getAccountid()) continue;
            client.send(text);
        }
    }

    public boolean remove(Client client) {
        boolean reown = isOwner(client);
        if (!clients.remove(client))
            return false;
        send("LEAVE "+client.getAccountid());
        if (clients.isEmpty()) {
            LobbyManager.logger.info("Dropping lobby {}", code);
            LobbyManager.lobbies.remove(this);
            QueueManager.remove(this);
        } else {
            if (reown)
                sendList();
            for (Client member : clients)
                member.setReady(false);
            ready = 0;
            checkQueue();
        }
        return true;
    }

    public boolean add(Client client) {
        if (clients.contains(client)) {
            int at = clients.indexOf(client); //find by .equals
            clients.set(at, client); //update instance
            return false;
        } else {
            if (clients.size() >= GameData.MaxLobbySize)
                return false;
            if (!clients.add(client))
                return false;
        }
        sendToAllBut(client, "JOIN "+client.getAccountid());
        sendListTo(client);
        checkQueue();
        return true;
    }

    public boolean contains(Client client) {
        return clients.contains(client);
    }

    public int size() {
        return clients.size();
    }

    // the connection has been closed for now, but they might come back
    public void markAfk(Client client) {
        if (client.isReady()) {
            client.setReady(false);
            ready-=1;
        }
        send("AFK "+client.getAccountid());
        if (clients.stream().allMatch(Client::isAFK)) close();
    }

    public void close() {
        send("CLOSE");
        clients.clear();
        LobbyManager.logger.info("Dropping lobby {}", code);
        LobbyManager.lobbies.remove(this);
        QueueManager.remove(this);
    }

    public void sendList() {
        send("LIST "+code+" "+clients.stream().map(c->String.valueOf(c.getAccountid())).collect(Collectors.joining(" ")));
        for (Client member : clients) {
            if (member.isReady()) send("READY "+member.getAccountid()+" 1");
        }
    }

    public void sendListTo(Client client) {
        client.send("LIST "+code+" "+clients.stream().map(c->String.valueOf(c.getAccountid())).collect(Collectors.joining(" ")));
        for (Client member : clients) {
            if (member.isReady()) client.send("READY "+member.getAccountid()+" 1");
        }
    }

    public boolean isOwner(Client client) {
        return clients.getFirst().getAccountid() == client.getAccountid();
    }

    public void setReady(Client client, boolean ready) {
        if (client.isReady() == ready) return;
        client.setReady(ready);
        if (ready) this.ready += 1;
        else this.ready -= 1;
        send("READY "+ client.getAccountid()+" "+(ready?1:0));
        checkQueue();
    }

    private void checkQueue() {
        if (ready == clients.size()) {
            QueueManager.add(this);
        } else {
            QueueManager.remove(this);
        }
    }

    public boolean searchServer() {
        return ready == clients.size() && !clients.isEmpty() && gameAddress == null;
    }

    public String getCode() {
        return code;
    }

    public LinkedList<GameData.Maps> getMaps() {
        return searchPool;
    }

    public LinkedList<MasterServerQuery.Region> getRegions() {
        return searchRegions;
    }

    public void join(Server server) {
        for (Client member : clients)
            member.setReady(false);
        ready = 0;
        send("CONNECT "+server.getAddress().getAddress().getHostAddress()+":"+server.getAddress().getPort());
    }

}
