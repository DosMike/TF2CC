package com.github.dosmike.tf2cc.lobby;

import com.github.dosmike.tf2cc.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;

public class LobbyManager {

    static final Logger logger = LoggerFactory.getLogger("LobbyMgr");

    static ArrayList<Lobby> lobbies = new ArrayList<>(500);
    static final CodeGenerator codeGen = new CodeGenerator();

    public static Optional<Lobby> getFor(Client client) {
        Optional<Lobby> lobby = lobbies.stream().filter(l->l.contains(client)).findAny();
        if (lobby.isPresent())
            logger.info("Found lobby for client");
        lobby.ifPresent(l->l.add(client)); //update client object instance
        return lobby;
    }

    public static Lobby makeNew(Client client) {
        Optional<Lobby> lobby = getFor(client);
        lobby.ifPresent(l->l.remove(client));
        Lobby newLobby = new Lobby();
        newLobby.add(client);
        LobbyManager.logger.info("Created lobby {}", newLobby.getCode());
        lobbies.add(newLobby);
        return newLobby;
    }

    public static Optional<Lobby> getByCode(String code) {
        return lobbies.stream().filter(l->l.getCode().equals(code)).findAny();
    }

}
