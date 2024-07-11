package com.github.dosmike.tf2cc;

import com.github.dosmike.tf2cc.gameserver.QueueManager;
import com.github.dosmike.tf2cc.gameserver.ServerMonitor;
import com.github.dosmike.tf2cc.lobby.ClientManager;
import com.github.dosmike.tf2cc.webserver.LobbySocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static int SocketPort = 80;

    static Thread serverThread, lobbyThread, clientThread, matchThread, inputThread;

    static Logger logger = LoggerFactory.getLogger("Watchdog");

    private static void makeServerThread() {
        serverThread = new Thread(new ServerMonitor());
        serverThread.setName("ServerMonitor");
        serverThread.start();
    }

    private static void makeLobbyThread() {
        lobbyThread = new Thread(new LobbySocket());
        lobbyThread.setName("LobbyWebSocket");
        lobbyThread.start();
    }

    private static void makeClientThread() {
        clientThread = new Thread(ClientManager::deadConnectionLooper);
        clientThread.setName("DeadConMonitor");
        clientThread.start();
    }

    private static void makeMatchmakerThread() {
        matchThread = new Thread(QueueManager::run);
        matchThread.setName("Matchmaker");
        matchThread.start();
    }

    private static void inputHandler() {
        while (true) {
            waitMs(1000);

            String line;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                line = in.readLine();
                if (line == null) continue;
            } catch (IOException ignore) {
                continue;
            }

            if (line.toLowerCase().startsWith("msg ")) {
                ClientManager.sendToAll(line.substring(4));
            } else if (line.equalsIgnoreCase("exit")) {
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
    private static void makeInputThread() {
        inputThread = new Thread(Main::inputHandler);
        inputThread.setName("Input");
        inputThread.start();
    }

    public static void main(String[] args) {

        //arg parsing :puke:
        for (String s : args) {
            if (s.startsWith("-p")) {
                Matcher m = Pattern.compile("-p=?([0-8]+)").matcher(s);
                if (!m.matches()) {
                    System.out.println("-p expects port number");
                    return;
                }
                int v = Integer.parseInt(m.group(1));
                if (v>65535) {
                    System.out.println("Port for -p too high!");
                    return;
                }
                SocketPort = v;
            } else {
                System.out.println("Unknown argument: "+s);
                return;
            }
        }

        makeServerThread();
        makeLobbyThread();
        makeClientThread();
        makeMatchmakerThread();
        makeInputThread();

        while (true) {
            if (!serverThread.isAlive()) {
                logger.warn("ServerMonitor thread has died");
                makeServerThread();
            }
            if (!lobbyThread.isAlive()) {
                logger.warn("Lobby thread has died");
                makeLobbyThread();
            }
            if (!clientThread.isAlive()) {
                logger.warn("ClientConnection thread has died");
                makeClientThread();
            }
            if (!matchThread.isAlive()) {
                logger.warn("MatchMaker thread has died");
                makeMatchmakerThread();
            }
            if (!inputThread.isAlive()) {
                logger.warn("InputHandler thread has died");
                makeInputThread();
            }

            Main.waitMs(1000);
        }

    }

    public static void waitMs(long ms) {
        long end = System.currentTimeMillis() + ms;
        do {
            try { Thread.sleep(end-System.currentTimeMillis()); }
            catch (InterruptedException ignore) {}
        } while (System.currentTimeMillis() < end);
    }

}
