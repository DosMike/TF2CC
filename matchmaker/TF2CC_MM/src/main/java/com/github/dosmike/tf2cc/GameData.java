package com.github.dosmike.tf2cc;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class GameData {

    static Logger logger = LoggerFactory.getLogger("GameData");

    public static final int MaxLobbySize = 6;

    public enum Category {
        Event,
        AttackDefend,
        CaptureTheFlag,
        ControlPoint,
        KingOfTheHill,
        Payload,
        PayloadRace,
        Misc,
        Mannpower,
        PassTime,
        Custom,
    }

    public enum GameMode {
        Arena,
        AttackDefend,
        CaptureTheFlag,
        ControlPoint,
        KingOfTheHill,
        Mannpower,
        MannVsMachine,
        MedievalMode,
        PassTime,
        Payload,
        PayloadRace,
        PlayerDestruction,
        RobotDestruction,
        SpecialDelivery,
        TerritorialControl,
        VersusSaxtonHale,
        ZombieInfection,
        Custom,
    }

    public enum Maps {
        CTF_Applejack("ctf_applejack", new Category[]{Category.Event}, new GameMode[]{GameMode.CaptureTheFlag}),
        PD_AtomSmash("pd_atom_smash", new Category[]{Category.Event}, new GameMode[]{GameMode.PlayerDestruction}),
        CP_Burghausen("cp_burghausen", new Category[]{Category.Event}, new GameMode[]{GameMode.MedievalMode}),
        KOTH_Cachoeira("koth_cachoeira", new Category[]{Category.Event}, new GameMode[]{GameMode.KingOfTheHill}),
        CP_Canaveral("cp_canaveral_5cp", new Category[]{Category.Event}, new GameMode[]{GameMode.ControlPoint}),
        PL_Embargo("pl_embargo", new Category[]{Category.Event}, new GameMode[]{GameMode.Payload}),
        CP_Hadal("cp_hadal", new Category[]{Category.Event}, new GameMode[]{GameMode.AttackDefend}),
        KOTH_Megaton("koth_megaton", new Category[]{Category.Event}, new GameMode[]{GameMode.KingOfTheHill}),
        PL_Odyssey("pl_odyssey", new Category[]{Category.Event}, new GameMode[]{GameMode.Payload}),
        CP_Overgrown("cp_overgrown", new Category[]{Category.Event}, new GameMode[]{GameMode.AttackDefend}),

        CP_Altitude("cp_altitude", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Brew("cp_brew", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Dustbowl("cp_dustbowl", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Egypt("cp_egypt_final", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Gorge("cp_gorge", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Gravelpit("cp_gravelpit", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Hardwood("cp_hardwood_final", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Junction("cp_junction_final", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_MercenaryPark("cp_mercenarypark", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Mossrock("cp_mossrock", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_MountainLab("cp_mountainlab", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Steel("cp_steel", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CP_Sulfur("cp_sulfur", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),
        CTF_Haarp("ctf_haarp", new Category[]{Category.AttackDefend}, new GameMode[]{GameMode.AttackDefend}),

        CP_5Gorge("cp_5gorge", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Badlands("cp_badlands", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Coldfront("cp_coldfront", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Fastlane("cp_fastlane", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Foundry("cp_foundry", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Freight("cp_freight_final1", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Granary("cp_granary", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Gullywash("cp_gullywash_final1", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Metalworks("cp_metalworks", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Powerhouse("cp_powerhouse", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Process("cp_process_final", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Reckoner("cp_reckoner", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Snakewater("cp_snakewater_final1", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Standin("cp_standin_final", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Sunshine("cp_sunshine", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Vanguard("cp_vanguard", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Yukon("cp_yukon_final", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),
        CP_Well("cp_well", new Category[]{Category.ControlPoint}, new GameMode[]{GameMode.ControlPoint}),

        CTF_2Fort("ctf_2fort", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_2Fort_Invasion("ctf_2fort_invasion", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Doublecross("ctf_doublecross", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Frosty("ctf_frosty", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Landfall("ctf_landfall", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_PelicanPeak("ctf_pelican_peak", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Sawmill("ctf_sawmill", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Turbine("ctf_turbine", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),
        CTF_Well("ctf_well", new Category[]{Category.CaptureTheFlag}, new GameMode[]{GameMode.CaptureTheFlag}),

        KOTH_Badlands("koth_badlands", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Brazil("koth_brazil", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Cascade("koth_cascade", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Harvest("koth_harvest_final", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Highpass("koth_highpass", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_KongKing("koth_king", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Lakeside("koth_lakeside_final", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Lazarus("koth_lazarus", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Nucleus("koth_nucleus", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Probed("koth_probed", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Rotunda("koth_rotunda", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Sawmill("koth_sawmill", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Sharkbay("koth_sharkbay", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Snowtower("koth_snowtower", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Suijin("koth_suijin", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),
        KOTH_Viaduct("koth_viaduct", new Category[]{Category.KingOfTheHill}, new GameMode[]{GameMode.KingOfTheHill}),

        PL_Badwater("pl_badwater", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Barnblitz("pl_barnblitz", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Borneo("pl_borneo", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_BreadSpace("pl_breadspace", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Camber("pl_camber", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Cashworks("pl_cashworks", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Emerge("pl_emerge", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Enclosure("pl_enclosure_final", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Frontier("pl_frontier_final", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Goldrush("pl_goldrush", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Hoodoo("pl_hoodoo_final", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Phoenix("pl_phoenix", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Pier("pl_pier", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Rumford("pl_rumford_event", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Snowycoast("pl_snowycoast", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Swiftwater("pl_swiftwater_final1", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Thundermountain("pl_thundermountain", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Upward("pl_upward", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),
        PL_Venice("pl_venice", new Category[]{Category.Payload}, new GameMode[]{GameMode.Payload}),

        PLR_BananaBay("plr_bananabay", new Category[]{Category.PayloadRace}, new GameMode[]{GameMode.PayloadRace}),
        PLR_Hacksaw("plr_hacksaw", new Category[]{Category.PayloadRace}, new GameMode[]{GameMode.PayloadRace}),
        PLR_Hightower("plr_hightower", new Category[]{Category.PayloadRace}, new GameMode[]{GameMode.PayloadRace}),
        PLR_Nightfall("plr_nightfall_final", new Category[]{Category.PayloadRace}, new GameMode[]{GameMode.PayloadRace}),
        PLR_Pipeline("plr_pipeline", new Category[]{Category.PayloadRace}, new GameMode[]{GameMode.PayloadRace}),

        CP_DegrootKeep("cp_degrootkeep", new Category[]{Category.Misc}, new GameMode[]{GameMode.MedievalMode}),
        VSH_Distillery("vsh_distillery", new Category[]{Category.Misc}, new GameMode[]{GameMode.VersusSaxtonHale}),
        SD_Doomsday("sd_doomsday", new Category[]{Category.Misc}, new GameMode[]{GameMode.SpecialDelivery}),
        TC_Hydro("tc_hydro", new Category[]{Category.Misc}, new GameMode[]{GameMode.TerritorialControl}),
        VSH_Nucleus("vsh_nucleus", new Category[]{Category.Misc}, new GameMode[]{GameMode.VersusSaxtonHale}),
        PD_Selbyen("pd_selbyen", new Category[]{Category.Misc}, new GameMode[]{GameMode.PlayerDestruction}),
        VSH_Skirmish("vsh_skirmish", new Category[]{Category.Misc}, new GameMode[]{GameMode.VersusSaxtonHale}),
        CP_Snowplow("cp_snowplow", new Category[]{Category.Misc}, new GameMode[]{GameMode.ControlPoint}),
        VSH_TinyRock("vsh_tinyrock", new Category[]{Category.Misc}, new GameMode[]{GameMode.VersusSaxtonHale}),
        PD_Watergate("pd_watergate", new Category[]{Category.Misc}, new GameMode[]{GameMode.PlayerDestruction}),

        CTF_Foundry("ctf_foundry", new Category[]{Category.Mannpower}, new GameMode[]{GameMode.Mannpower}),
        CTF_Gorge("ctf_gorge", new Category[]{Category.Mannpower}, new GameMode[]{GameMode.Mannpower}),
        CTF_Hellfire("ctf_hellfire", new Category[]{Category.Mannpower}, new GameMode[]{GameMode.Mannpower}),
        CTF_Thundermountain("ctf_thundermountain", new Category[]{Category.Mannpower}, new GameMode[]{GameMode.Mannpower}),

        PASS_Brickyard("pass_brickyard", new Category[]{Category.PassTime}, new GameMode[]{GameMode.PassTime}),
        PASS_Disrict("pass_district", new Category[]{Category.PassTime}, new GameMode[]{GameMode.PassTime}),
        PASS_Timbertown("pass_timbertown", new Category[]{Category.PassTime}, new GameMode[]{GameMode.PassTime}),
        ;

        private final String name;
        private final ArrayList<Category> categories;
        private final ArrayList<GameMode> gameModes;

        Maps(String name, Category[] categories, GameMode[] gameModes) {
            this.name = name;
            this.categories = new ArrayList<>(1);
            Collections.addAll(this.categories, categories);
            this.gameModes = new ArrayList<>(1);
            Collections.addAll(this.gameModes, gameModes);
        }

        public static Set<Maps> findBy(Category category) {
            return Arrays.stream(values()).filter(m->m.categories.contains(category)).collect(Collectors.toSet());
        }
        public static Set<Maps> findBy(GameMode gamemode) {
            return Arrays.stream(values()).filter(m->m.gameModes.contains(gamemode)).collect(Collectors.toSet());
        }
        public static Maps fromMapName(String mapName) {
            return Arrays.stream(values()).filter(m->m.name.equals(mapName)).findAny().orElse(null);
        }

        public Set<Category> getCategories() {
            return new HashSet<>(categories);
        }
        public Set<GameMode> getGameModes() {
            return new HashSet<>(gameModes);
        }
        public String getMapName() {
            return name;
        }
    }

    private static long minGameVersion = 0L;
    private static long gameVersionAge = 0L;

    public static long getGameVersion() {
        if (minGameVersion != 0L && System.currentTimeMillis()-gameVersionAge < 6*3600000)
            return minGameVersion;
        gameVersionAge = System.currentTimeMillis();

        try {
            HttpsURLConnection con = (HttpsURLConnection)new URI("https://api.steampowered.com/IGCVersion_440/GetServerVersion/v1/").toURL().openConnection();
            con.setInstanceFollowRedirects(true);
            if (con.getResponseCode() != 200) {
                logger.warn("Could not fetch game server version, HTTP response {}", con.getResponseMessage());
                return minGameVersion;
            }
            ObjectMapper mapper = new ObjectMapper();
            TreeNode root = mapper.readTree(con.getInputStream());
            if (root.isObject() && root instanceof ObjectNode rootObject) {
                if (rootObject.findValue("success") instanceof ValueNode value && value.asBoolean(false)
                    && rootObject.findValue("min_allowed_version") instanceof ValueNode newMinVersion) {
                    minGameVersion = newMinVersion.asLong(minGameVersion);
                }
            }
            logger.info("Minimum game server version updated to {}", minGameVersion);
        } catch (IOException|URISyntaxException e) {
            logger.error("Could not fetch game server version", e);
        }
        return minGameVersion;
    }

}
