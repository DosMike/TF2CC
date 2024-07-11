package com.github.dosmike.a2s;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class PlayerList extends ArrayList<PlayerList.Player> implements A2SMessage.A2SDeserializable {

    public static class Player {
        public int index;
        public String name;
        public int score;
        public float duration;
        public int deaths=0; ///< TheShip
        public int money=0; ///< TheShip

        Player(ByteBuffer payload) {
            index = payload.get() & 0x0ff; //someone might care about index
            name = A2SMessage.getString(payload);
            score = payload.getInt();
            duration = payload.getFloat();
        }

        @Override
        public String toString() {
            return "Player{" +
                    "name='" + name + '\'' +
                    ", score=" + score +
                    ", duration=" + duration +
                    ", deaths=" + deaths +
                    ", money=" + money +
                    '}';
        }
    }

    PlayerList(ByteBuffer payload, long expectedGame) {
        if (payload.remaining() <= 2) return;
        int count = payload.get() & 0x0ff;
        ensureCapacity(count);
        for (int i=0; i<count; i++) {
            add(new Player(payload));
        }
        if (ServerInfo.SteamAppId.TheShipMurderParty.id == expectedGame ||
            ServerInfo.SteamAppId.TheShip_DedicatedServer.id == expectedGame) {
            for (int i=0; i<count; i++) {
                Player p = get(i);
                p.deaths = payload.getInt();
                p.money = payload.getInt();
            }
        }
    }

    public PlayerList() {}

}
