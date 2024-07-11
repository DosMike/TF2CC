package com.github.dosmike.tf2cc.lobby;

import com.github.dosmike.tf2cc.sql.DatabaseInterface;
import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Client {

    private String session;
    private long accountid;
    private long lastmsg; //for connection timeout
    private boolean ready;

    @Nullable
    WebSocket socket; //can be null if disconnected

    public Client(WebSocket socket, String session) {
        this.session = session;
        this.accountid = DatabaseInterface.getAccountFromSession(session).orElseThrow();
        this.lastmsg = System.currentTimeMillis();
        this.socket = socket;
    }

    public void updateSocket(@Nullable WebSocket socket) {
        this.socket = socket;
        if (this.socket == null) {
            LobbyManager.getFor(this).ifPresent(l->l.markAfk(this));
            ClientManager.uncache(socket);
        }
    }

    WebSocket getSocket() {
        return socket;
    }

    public void send(String message) {
        if (socket != null)
            socket.send(message);
    }
    public void keepAlive() {
        lastmsg = System.currentTimeMillis();
    }

    public boolean isTimedOut() {
        return socket == null && System.currentTimeMillis() - lastmsg > 60000;
    }

    public boolean isAFK() {
        return socket == null;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getAccountid() {
        return accountid;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return accountid == client.accountid;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountid);
    }
}
