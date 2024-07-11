package com.github.dosmike.tf2cc.gameserver;

import com.github.dosmike.a2s.A2S;
import com.github.dosmike.a2s.PlayerList;
import com.github.dosmike.a2s.Rules;
import com.github.dosmike.a2s.ServerInfo;
import com.github.dosmike.msq.MasterServerQuery;
import com.github.dosmike.tf2cc.GameData;
import com.github.dosmike.tf2cc.lobby.Lobby;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    final InetSocketAddress address;
    MasterServerQuery.Region region;

    int dbEnabled = 1;

    ServerInfo info = null;
    long infoAge = 0L;
    Rules rules = null;
    long rulesAge = 0L;
    PlayerList players = new PlayerList();
    long playerListAge = 0L;

    public Server(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Server { Address: ").append(address);
        if (region != null) sb.append(" (").append(region.name()).append(")");
        if (info == null) {
            sb.append(", NO INFO LOADED");
        } else {
            sb.append(", Name: ").append(getName())
                .append(", Map: ").append(getMap())
                .append(", Game: ").append(getGame()).append(" (").append(getFolder()).append(", ").append(getAppid())
                .append("), Players: ").append(getPlayers()).append("/").append(getMaxplayers()).append(" +").append(getBots())
                .append("Bots, Meta: ").append(getServertype().name()).append(" ").append(getServerenv().name());
            if (hasPassword()) sb.append(" pw");
            if (isVacEnabled()) sb.append(" vac");
            sb.append(", Version: ").append(getVersion())
                .append(", Server Login: ").append(getServerSteamId())
                .append(", Keywords: ").append(String.join(",", getKeywords()));
        }
        sb.append(", Rules: { ");
        if (rules != null)
            for (Map.Entry<String, String> e : rules.entrySet())
                sb.append(e.getKey()).append(": ").append(e.getValue()).append(", ");
        sb.append("}, Players: { ");
        if (players != null)
            for (PlayerList.Player p : players)
                sb.append(p.name).append(" - ").append(p.score).append(" T").append(p.duration).append(", ");
        sb.append("} }");
        return sb.toString();
    }

    public boolean hasTag(String s) {
        return Arrays.stream(getKeywords()).anyMatch(s::equalsIgnoreCase);
    }
    public boolean hasAnyTag(String ... tags) {
        Set<String> set_kw = Arrays.stream(getKeywords()).collect(Collectors.toSet());
        Set<String> set_tags = Arrays.stream(tags).collect(Collectors.toSet());
        set_kw.retainAll(set_tags);
        return !set_kw.isEmpty();
    }

    /** requires rules to be read */
    public boolean validateTags() {
        return hasTag("friendlyfire") == !rules.getOrDefault("mp_friendlyfire", "").equals("0") &&
                hasTag("respawntimes") == !rules.getOrDefault("mp_respawnwavetime", "").equals("10.0") &&
                hasTag("norespawntime") == !rules.getOrDefault("mp_disable_respawn_times", "").equals("0") &&
                hasTag("increased_maxplayers") == (getMaxplayers() > 24) &&
                //hasTag("nocrits") == rules.getOrDefault("tf_weapon_criticals", "").equals("0") &&
                //hasTag("nodmgspread") == !rules.getOrDefault("tf_damage_disablespread", "").equals("0") &&
                //hasTag("noquickplay") == !rules.getOrDefault("tf_server_identity_disable_quickplay", "").equals("0") &&
                hasTag("highlander") == !rules.getOrDefault("mp_highlander", "").equals("0") &&
                hasTag("gravity") == !rules.getOrDefault("sv_gravity", "").equals("800") &&
                !hasTag("_registered");
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public MasterServerQuery.Region getRegion() {
        return region;
    }

    public GameData.Maps getMap() {
        try {
            return GameData.Maps.fromMapName(info.map);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getName() {
        return info.name;
    }

    public String getFolder() {
        return info.folder;
    }

    public String getGame() {
        return info.game;
    }

    public long getAppid() {
        return info.steamAppId;
    }

    public int getPlayers() {
        return info.players;
    }

    public int getMaxplayers() {
        return info.maxPlayers;
    }

    public int getBots() {
        return info.bots;
    }

    public ServerInfo.ServerType getServertype() {
        return info.type;
    }

    public ServerInfo.Environment getServerenv() {
        return info.environment;
    }

    public boolean hasPassword() {
        return info.visibility == ServerInfo.Visibility.Private;
    }

    public boolean isVacEnabled() {
        return info.vac == ServerInfo.VACSecurity.Secured;
    }

    public long getVersion() {
        if (info.version == null || info.version.isEmpty())
            return 0L;
        try {
            return Long.parseLong(info.version);
        } catch (NumberFormatException nfe) {
            return 0L;
        }
    }

    public long getServerSteamId() {
        return info.serverSteamId;
    }

    public String[] getKeywords() {
        return info.tags.split(",");
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public List<PlayerList.Player> getPlayerList() {
        return players;
    }

    public int getDbEnabled() {
        return dbEnabled;
    }

    public void setDbEnabled(int dbEnabled) {
        this.dbEnabled = dbEnabled;
    }

    private static final int retries = 1;
    public synchronized void loadInfo() throws IOException {
        infoAge = System.currentTimeMillis();
        for (int i=1; ; i++) {
            try {
                info = A2S.info(address);
                return;
            } catch(SocketTimeoutException timeout) {
                if (i>=retries) throw timeout;
            }
        }
    }
    public synchronized void loadRules() throws IOException {
        rulesAge = System.currentTimeMillis();
        for (int i=1; ; i++) {
            try {
                rules = A2S.rules(address);
                return;
            } catch(SocketTimeoutException timeout) {
                if (i>=retries) throw timeout;
            }
        }
    }
    public synchronized void loadPlayers() throws IOException {
        playerListAge = System.currentTimeMillis();
        for (int i=1; ; i++) {
            try {
                players = A2S.players(address);
                return;
            } catch(SocketTimeoutException timeout) {
                if (i>=retries) throw timeout;
            }
        }
    }
    public synchronized long getInfoAge() {
        return System.currentTimeMillis()-infoAge;
    }
    public synchronized long getRulesAge() {
        return System.currentTimeMillis()-rulesAge;
    }
    public synchronized long getPlayerListAge() {
        return System.currentTimeMillis()-playerListAge;
    }

    public void addLobby(Lobby lobby) {
        info.players += (short) lobby.size();
        lobby.join(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server server)) return false;
        return Objects.equals(address, server.address) && region == server.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, region);
    }
}
