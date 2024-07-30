package com.github.dosmike.tf2cc.gameserver;

import com.github.dosmike.tf2cc.Main;
import com.github.dosmike.tf2cc.lobby.Lobby;
import com.github.dosmike.tf2cc.sql.DatabaseInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueManager {

    static Logger logger = LoggerFactory.getLogger("Queue");

    private static final LinkedList<Lobby> queue = new LinkedList<>();

    public static synchronized void add(Lobby lobby) {
        queue.add(lobby);
    }
    public static synchronized void remove(Lobby lobby) {
        queue.remove(lobby);
    }
    public static synchronized boolean isQueued(Lobby lobby) {
        return queue.contains(lobby);
    }
    private static synchronized Lobby peek() {
        return queue.peek();
    }

    public static void run() {
        while (true) {
            makeMatch();
            Main.waitMs(1000);
        }
    }


    /// check how well a server fits to a lobby
    private static double serverMatchWeight(Server server, Lobby lobby) {
        // map is not picked
        if (!lobby.getMaps().contains(server.getMap())) return 0.0;
        // region is not picked
        if (!lobby.getRegions().contains(server.getRegion())) return 0.0;

        // try to fill an existing server first, otherwise an empty server is fine
        if (server.getPlayers() == 0) return 1e-3;

        int cap = server.getMaxplayers() - server.getPlayers() - 1; //subtract one to get in a bit more reliably

        // not enough space for this lobby
        if (cap < lobby.size()) {
            logger.info("Cap low for {}: ({}-{})/{}", server.getName(), server.getMaxplayers(), server.getPlayers()+1, lobby.size());
            return 0.0;
        }

        double weight = (server.getPlayers()-server.getBots()) * 1.0 / server.getMaxplayers();
        logger.info("Weight for {}: ({}-{})/{} -> {}%", server.getName(), server.getPlayers(), server.getBots(), server.getMaxplayers(), weight);
        // the more human players the better
        return weight;
    }

    private static Server findServerForLobby(Lobby lobby) {
        // mass-update
        if (ServerMonitor.globalLock.getAndSet(true))
            return null; //was already true -> master update is running

        Collection<Server> candidates = ServerMonitor.getServers();
        candidates.removeIf(s->!lobby.getMaps().contains(s.getMap()) || s.getRules() == null);

        int poolsize = 15;
        ExecutorService threadpool = Executors.newFixedThreadPool(poolsize);
        for (Server server : candidates) {
            threadpool.submit(() -> {
                if (server.getInfoAge() > 60000) try {
                    server.loadInfo();
                    DatabaseInterface.pushServer(server);
                    logger.info("Updated and pushed {} while queueing", server.getAddress());
                } catch (Exception e) {
                    logger.error("Could not update server info for " + server.getAddress(), e);
                    candidates.remove(server);
                }
            });
        }
        threadpool.close();
        ServerMonitor.globalLock.set(false);

        // now we can more quickly search through the maps, without blocking for updates
        double bestValue = 0.0;
        List<Server> instances = new LinkedList<>();
        for (Server s : candidates) {
            if (!lobby.getMaps().contains(s.getMap())) continue;
            double value = serverMatchWeight(s, lobby);
            if (!isQueued(lobby))
                return null;
            if (value > bestValue) {
                instances.clear();
                bestValue = value;
                instances.add(s);
            } else if (value == bestValue) {
                instances.add(s);
            }
        }
        return instances.get((int)Math.floor(Math.random() * instances.size()));
    }

    private static boolean makeMatch() {
        // pop lobby to match-make
        Lobby lobby = peek();
        if (lobby == null)
            return false;
        logger.info("Searching server for {}", lobby.getCode());

        // pick server
        Server server = findServerForLobby(lobby);
        if (server == null || !isQueued(lobby)) {
            // push server back, we try again later
            synchronized (QueueManager.class) {
                if (queue.remove(lobby))
                    queue.add(lobby);
            }
            logger.info("Could not find server for lobby {} in time", lobby.getCode());
            return false;
        } else {
            // joining into a running server
            queue.remove(lobby);
            server.addLobby(lobby);
            logger.info("Added lobby "+lobby.getCode()+" to "+server.getAddress());
            return true;
        }
    }
}
