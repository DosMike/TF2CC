package com.github.dosmike.a2s;

import java.nio.ByteBuffer;

public class ServerInfo implements A2SMessage.A2SDeserializable {

    /**
     * Non-exhaustive list to reduce the amount of magic numbers
     */
    public enum SteamAppId {

        SourceSDK(211),
        SourceSDK2006(215),
        SourceSDK2007(218),
        CounterStrikeSource(240),
        DayOfDefeatSource(300),
        HalfLife2Deathmatch(320),
        HalfLifeDeathmatchSource(360),
        TeamFortress2(440),
        Left4Dead(500),
        Left4Dead2(550),
        Dota2(570),
        Portal2(620),
        AlienSwarm(630),
        CounterStrikeGlobalOffensive(730),
        SinEpisodes(1300),
        SiNEpisodesSDK(1317),
        CounterStrikeGlobalOffensive_(1800),
        TheShipMurderParty(2400),
        GarrysMod(4000),
        ZombiePanicSource(17500),
        AgeOfChivalry(17510),
        Synergy(17520),
        DIPRIP(17530),
        EternalSilence(17550),
        PiratesVikingsKnights2(17570),
        Dystopia(17580),
        InsurgencyMod(17700),
        NuclearDawn(17710),
        Insurgency(222880),
        NoMoreRoomInHell(224260),
        Contagion(238430),
        SourceSDKBase2013MP(243750),

        Left4Dead_DedicatedServer(510),
        Left4Dead2_DedicatedServer(560),
        CounterStrikeGlobalOffensive_DedicatedServer(740),
        SiN1_DedicatedServer(1314),
        TheShip_DedicatedServer(2403),
        ZombiePanicSource_DedicatedServer(17505),
        AgeOfChivalry_DedicatedServer(17515),
        Synergy_DedicatedServer(17525),
        DIPRIP_DedicatedServer(17535),
        InsurgencyMod_DedicatedServer(17705),
        NuclearDawn_DedicatedServer(111710),
        Left4Dead2_DedicatedServer_(222860),
        TeamFortress2_DedicatedServer(232250),
        DayOfDefeatSource_DedicatedServer(232290),
        CounterStrikeSource_DedicatedServer(232330),
        HalfLife2Deathmatch_DedicatedServer(232370),
        Insurgency_DedicatedServer(237410),
        SourceSDK2013_DedicatedServer(244310),
        SevenDaysToDie_DedicatedServer(294420),
        NoMoreRoomInHell_DedicatedServer(317670),
        DayOfInfamy_DedicatedServer(462310),
        ;
        public int id;
        SteamAppId(int appid) { id = appid; }
        public static SteamAppId findById(int id) {
            for (SteamAppId app : values())
                if (app.id == id) return app;
            return null;
        }
    }

    public enum ServerType {
        INVALID,
        Dedicated, ///< 'd' dedicated server
        Listen, ///< 'l' Non-Dedicated server
        Proxy, ///< 'p' SourceTV proxy
    }
    public enum Environment {
        INVALID,
        Linux,
        Windows,
        Mac,
    }
    public enum Visibility {
        Public,
        Private,
    }
    public enum VACSecurity {
        Unsecured,
        Secured,
    }
    public enum TheShipMode {
        INVALID,
        Hunt,
        Elimination,
        Duel,
        Deathmatch,
        VIPTeam,
        TeamElimination,
    }
    public enum ExtraDataFlag {
        EDF_GamePort(0x80),
        EDF_ServerSteamId(0x10),
        EDF_SourceTV(0x40),
        EDF_Tags(0x20),
        EDF_SteamAppId(0x01),
        ;
        public final int flag;
        ExtraDataFlag(int flag) {this.flag = flag;}
    }

    public int protocol;
    public String name;
    public String map;
    public String folder; ///< mod name (e.g. tf)
    public String game;
    public long steamAppId;
    public short players;
    public short maxPlayers;
    public short bots;
    public ServerType type;
    public Environment environment;
    public Visibility visibility;
    public VACSecurity vac;
    public TheShipMode theShipMode=TheShipMode.INVALID;
    public short theShipWitnesses=0;
    public short theShipDuration=0;
    public String version="";
    public int extraDataFlag=0;
    public short gamePort=0;
    public long serverSteamId=0;
    public short sourceTvPort=0;
    public String sourceTvName="";
    public String tags="";

    ServerInfo(ByteBuffer payload, long expectedGame) {
        protocol = payload.get();
        name = A2SMessage.getString(payload);
        map = A2SMessage.getString(payload);
        folder = A2SMessage.getString(payload);
        game = A2SMessage.getString(payload);
        steamAppId = payload.getShort();
        players = (short)(payload.get() & 0x0ff);
        maxPlayers = (short)(payload.get() & 0x0ff);
        bots = (short)(payload.get() & 0x0ff);
        type = switch ((char)(payload.get())) {
            case 'd' -> ServerType.Dedicated;
            case 'l' -> ServerType.Listen;
            case 'p' -> ServerType.Proxy;
            default -> ServerType.INVALID;
        };
        environment = switch ((char)(payload.get())) {
            case 'l' -> Environment.Linux;
            case 'w' -> Environment.Windows;
            case 'm' -> Environment.Mac;
            default -> Environment.INVALID;
        };
        visibility = payload.get()!=0 ? Visibility.Private : Visibility.Public;
        vac = payload.get()!=0 ? VACSecurity.Secured : VACSecurity.Unsecured;
        if (SteamAppId.TheShipMurderParty.id == steamAppId || SteamAppId.TheShip_DedicatedServer.id == steamAppId) {
            theShipMode = switch (payload.get()) {
                case 0 -> TheShipMode.Hunt;
                case 1 -> TheShipMode.Elimination;
                case 2 -> TheShipMode.Duel;
                case 3 -> TheShipMode.Deathmatch;
                case 4 -> TheShipMode.VIPTeam;
                case 5 -> TheShipMode.TeamElimination;
                default -> TheShipMode.INVALID;
            };
            theShipWitnesses = (short)(payload.get() & 0x0ff);
            theShipDuration = (short)(payload.get() & 0x0ff);
        }
        version = A2SMessage.getString(payload);

        if (!payload.hasRemaining()) return;
        extraDataFlag = payload.get() & 0x0ff;

        if ((extraDataFlag & ExtraDataFlag.EDF_GamePort.flag)!=0) {
            gamePort = payload.getShort();
        }
        if ((extraDataFlag & ExtraDataFlag.EDF_ServerSteamId.flag)!=0) {
            serverSteamId = payload.getLong();
        }
        if ((extraDataFlag & ExtraDataFlag.EDF_SourceTV.flag)!=0) {
            sourceTvPort = payload.getShort();
            sourceTvName = A2SMessage.getString(payload);
        }
        if ((extraDataFlag & ExtraDataFlag.EDF_Tags.flag)!=0) {
            tags = A2SMessage.getString(payload);
        }
        if ((extraDataFlag & ExtraDataFlag.EDF_GamePort.flag)!=0) {
            steamAppId = payload.getLong();
        }
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "protocol=" + protocol +
                ", name='" + name + '\'' +
                ", map='" + map + '\'' +
                ", folder='" + folder + '\'' +
                ", game='" + game + '\'' +
                ", steamAppId=" + steamAppId +
                ", players=" + players +
                ", maxPlayers=" + maxPlayers +
                ", bots=" + bots +
                ", type=" + type +
                ", environment=" + environment +
                ", visibility=" + visibility +
                ", vac=" + vac +
                ", theShipMode=" + theShipMode +
                ", theShipWitnesses=" + theShipWitnesses +
                ", theShipDuration=" + theShipDuration +
                ", version='" + version + '\'' +
                ", extraDataFlag=" + extraDataFlag +
                ", gamePort=" + gamePort +
                ", serverSteamId=" + serverSteamId +
                ", sourceTvPort=" + sourceTvPort +
                ", sourceTvName='" + sourceTvName + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
