package com.github.dosmike.tf2cc.gameserver;

import com.github.dosmike.a2s.A2S;
import com.github.dosmike.a2s.ServerInfo;
import com.github.dosmike.msq.MasterServerQuery;
import com.github.dosmike.tf2cc.GameData;
import com.github.dosmike.tf2cc.Main;
import com.github.dosmike.tf2cc.sql.DatabaseInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ServerMonitor implements Runnable {

    static final Logger logger = LoggerFactory.getLogger("ServMon");
    private static final ArrayList<Server> serverList = new ArrayList<>(1000);

    public static AtomicBoolean globalLock = new AtomicBoolean(false);

    void updateServers(boolean database) {
        long t0;
        t0 = System.currentTimeMillis();
        MasterServerQuery.Builder msqBuilder = MasterServerQuery.builder()
                .filterAppid(440)
                .filterDedicated()
                .filterGamedir("tf")
                .filterNoPassword()
                .filterSecure()
                .region(MasterServerQuery.Region.ANY);
        List<Server> masterListServers = new LinkedList<>();
        boolean first=true;
        for (MasterServerQuery.Region region : MasterServerQuery.Region.values()) {
            if (first) first = false;
            else Main.waitMs(1000);
            if (region == MasterServerQuery.Region.ANY) continue;

            MasterServerQuery query = msqBuilder.region(region).build();
            query.update();
            Collection<Server> forRegion = query.getServers().stream().map(sock-> {
                Server s = new Server(sock);
                s.region = region;
                return s;
            }).toList();
            masterListServers.addAll(forRegion);
        }
        logger.info("MSQ Query Time: {}ms for {} instances", System.currentTimeMillis()-t0, masterListServers.size());

        Collections.shuffle(masterListServers);

        synchronized (ServerMonitor.class) {
            serverList.retainAll(masterListServers);
            List<Server> newServer = new LinkedList<>(masterListServers);
            newServer.removeAll(serverList);
            serverList.addAll(newServer);
        }

        A2S.installDatagramSocketFactory(()->{
            DatagramSocket socket = new DatagramSocket();
            socket.setReuseAddress(true);
            socket.setSoTimeout(2000);
            socket.setReceiveBufferSize(150000);
            return socket;
        });
        t0 = System.currentTimeMillis();
        // .parallel() is not controllable, so split up ourselves
        int poolsize = 15;
        AtomicInteger failed = new AtomicInteger(0);
        ExecutorService threadpool = Executors.newFixedThreadPool(poolsize);
        serverList.forEach(s->{
            threadpool.submit(() -> {
                try {
                    if (s.getInfoAge() > 60000)
                        s.loadInfo();
                    if (s.getRulesAge() > 60000)
                        s.loadRules();
                } catch (SocketTimeoutException e) {
                    logger.error("Failed to query server {}: Connection timed out", s.address);
                    failed.addAndGet(1);
                } catch (IOException e) {
                    logger.error("Failed to query server "+s.address, e);
                    failed.addAndGet(1);
                }
            });
        });
        threadpool.close();
        logger.info("A2S Query Time: {}ms for {} instance, {} failed", System.currentTimeMillis()-t0, serverList.size(), failed.get());

        long minGameVersion = GameData.getGameVersion();
        t0 = System.currentTimeMillis();
        // should not start with a whitespace (or weird stuff), only letters (of any language)
        // and space characters. ascii "special chars" are ok as well. but any hostname should be set.
        Pattern p = Pattern.compile("^(?![\\h\\v\\x01-\\x20]).+$");
        Predicate<String> serverNameOK = (s)-> s != null && !s.equals("Team Fortress") && p.matcher(s).matches();
        final Set<String> cvarBlockList = new HashSet<>();
        // cvarBlockList.add("dr_version");
        cvarBlockList.add("ff2_version");
        cvarBlockList.add("sf2_version");
        cvarBlockList.add("sf2modified_version");
        cvarBlockList.add("shavit_version");
        // cvarBlockList.add("sm_dr_version");
        cvarBlockList.add("sm_force_html_motd_version");
        cvarBlockList.add("sm_free_noise_makers__version");
//        cvarBlockList.add("sm_giveitem_version");
        cvarBlockList.add("sm_prophunt_version");
        cvarBlockList.add("sm_rtd_version");
        cvarBlockList.add("sm_szf_version");
        cvarBlockList.add("sm_taunt_version");
//        cvarBlockList.add("sm_tf2ii_version");
//        cvarBlockList.add("sm_tf2idb_version");
        cvarBlockList.add("sm_zf_version");
//        cvarBlockList.add("tf2items_giveweapon_version");
        cvarBlockList.add("tf2items_rnd_version"); //rando?
        cvarBlockList.add("tf2ware_version");
        cvarBlockList.add("tf2x10_version");
        cvarBlockList.add("tf_tauntem_version");
        cvarBlockList.add("ze_version");
        Predicate<Server> configValid = (s)->{
            Set<String> rules = new HashSet<>(s.rules.keySet());
            rules.retainAll(cvarBlockList);
            return rules.isEmpty();
        };

        synchronized (ServerMonitor.class) {
            serverList.removeIf(s ->
            {
                try {
                    if (s.getName() == null || s.getRules() == null) return true;
                } catch (NullPointerException ignore) {
                    return true;
                }
                if (!s.isVacEnabled()) {
                    logger.info("{} Dropped: VAC disabled", s.address);
                    return true;
                }
                if (s.hasPassword()) {
                    logger.info("{} Dropped: has password", s.address);
                    return true;
                }
                if (s.getServertype() != ServerInfo.ServerType.Dedicated) {
                    logger.info("{} Dropped: not dedicated", s.address);
                    return true;
                }
                if (!"tf".equals(s.getFolder())) {
                    logger.info("{} Dropped: game folder not tf", s.address);
                    return true;
                }
                if (!"Team Fortress".equals(s.getGame())) {
                    logger.info("{} Dropped: game name not Team Fortress", s.address);
                    return true;
                }
                if (s.getVersion() < minGameVersion) {
                    logger.info("{} Dropped: server is outdated (below minGameVersion {})", s.address, minGameVersion);
                    return true;
                }
                if (s.getServerSteamId() == 0L) {
                    logger.info("{} Dropped: no gslt steam account", s.address);
                    return true;
                }
                if (s.getMaxplayers() < 24) {
                    logger.info("{} Dropped: max players below 24", s.address);
                    return true;
                }
                if (s.getMaxplayers() > 32) {
                    logger.info("{} Dropped: max players above 32", s.address);
                    return true;
                }
                if (!serverNameOK.test(s.getName())) {
                    logger.info("{} Dropped: server name not OK", s.address);
                    return true;
                }
                if (s.hasAnyTag("friendlyfire", "highlander", "trade", "noquickplay", "hidden", "rtd", "minecraft", "deathmatch", "mvm", "ff2", "jump", "taunt", "items", "tdm")) {
                    logger.info("{} Dropped: has restricted tag", s.address);
                    return true;
                }
                if (!s.validateTags()) {
                    logger.info("{} Dropped: tag validation failed", s.address);
                    return true;
                }
                if (!configValid.test(s)) {
                    logger.info("{} Dropped: prohibited plugins detected", s.address);
                    return true;
                }
                return false;
            });
        }

        // update database if we can queue in
        if (database) {
            serverList.forEach(s->{
                DatabaseInterface.pushServer(s);
                DatabaseInterface.checkServer(s);
                logger.info("Checked in server {}", s.address);
            });
        }
        logger.info("Filtering Time: {}ms down to {} instances", System.currentTimeMillis()-t0, serverList.size());

    }

    public void run() {
        while (true) {
            boolean lockTaken = false;
            long runtime = 0;
            try {
                lockTaken = !globalLock.getAndSet(true);
                if (lockTaken) {
                    long t0 = System.currentTimeMillis();
                    updateServers(true);
                    runtime = System.currentTimeMillis() - t0;
                    logger.info("Total Cycle Time: {}ms", runtime);
                }
            } finally {
                if (lockTaken)
                    globalLock.set(false);
            }
            //schedule in a 15-minute rhythm
            long nextRun = 900000 - (runtime % 900000);
            Main.waitMs(nextRun);
        }
    }

    public static synchronized Collection<Server> getServers() {
        return new ArrayList<>(serverList);
    }

    public static synchronized void visit(Consumer<Server> visitor) {
        for (Server s : serverList) visitor.accept(s);
    }

}
