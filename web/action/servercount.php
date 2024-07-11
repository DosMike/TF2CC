<?php

if (empty($fromAction)) die("Illegal direct invocation");

header('Content-Type: application/json');

$result = ['maps'=>[], 'regions'=>[]];

$mappool = ["cp_5gorge", "cp_altitude", "cp_badlands", "cp_brew", "cp_coldfront", "cp_dustbowl", "cp_egypt_final",
            "cp_fastlane", "cp_foundry", "cp_freight_final1", "cp_gorge", "cp_granary", "cp_gravelpit",
            "cp_gullywash_final1", "cp_hardwood_final", "cp_junction_final", "cp_mercenarypark", "cp_metalworks",
            "cp_mossrock", "cp_mountainlab", "cp_powerhouse", "cp_process_final", "cp_reckoner", "cp_snakewater_final1",
            "cp_standin_final", "cp_steel", "cp_sulfur", "cp_sunshine", "cp_vanguard", "cp_yukon_final", "ctf_2fort",
            "ctf_doublecross", "ctf_landfall", "ctf_pelican_peak", "ctf_sawmill", "ctf_turbine", "ctf_well", "cp_well",
            "koth_badlands", "koth_brazil", "koth_cascade", "koth_harvest_final", "koth_highpass", "koth_king",
            "koth_lakeside_final", "koth_lazarus", "koth_nucleus", "koth_rotunda", "koth_sawmill", "koth_sharkbay",
            "koth_suijin", "koth_viaduct", "pl_badwater", "pl_barnblitz", "pl_borneo", "pl_breadspace", "pl_camber",
            "pl_cashworks", "pl_emerge", "pl_enclosure_final", "pl_frontier_final", "pl_goldrush", "pl_hoodoo_final",
            "pl_phoenix", "pl_pier", "pl_snowycoast", "pl_swiftwater_final1", "pl_thundermountain", "pl_upward",
            "pl_venice", "plr_bananabay", "plr_hightower", "plr_nightfall_final", "plr_pipeline", "ctf_2fort_invasion",
            "cp_degrootkeep", "vsh_distillery", "sd_doomsday", "tc_hydro", "vsh_nucleus", "koth_probed", "pd_selbyen",
            "vsh_skirmish", "cp_snowplow", "vsh_tinyrock", "pd_watergate", "ctf_foundry", "ctf_gorge", "ctf_hellfire",
            "ctf_thundermountain", "pass_brickyard", "pass_district", "pass_timbertown" ];

sqlSelect('servers', ['Map', 'Region'], '`Enabled` = 1 AND `LastUpdate` > DATE_SUB(NOW(), INTERVAL 1 hour)');
while (($row = sqlGetRow())!==null) {
    if (!in_array($row['Map'], $mappool) || empty($row['Map']) || empty($row['Region'])) continue;
    if (array_key_exists($row['Map'], $result['maps'])) {
        $result['maps'][$row['Map']] += 1;
    } else {
        $result['maps'][$row['Map']] = 1;
    }
    if (array_key_exists($row['Region'], $result['regions'])) {
        $result['regions'][$row['Region']] += 1;
    } else {
        $result['regions'][$row['Region']] = 1;
    }
}

echo json_encode($result);