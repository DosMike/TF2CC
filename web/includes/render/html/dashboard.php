<?php

function htmlHeader($data) {?>
    <script src="/script/request.min.js"></script>
    <script src="/script/app.min.js"></script><?php
}

function htmlRender($data) {

    $loggedin = !empty($_SESSION['dbid']);

    if ($loggedin) {
    ?><h1>Lobby<span class="lobbyctrl2"><button type="button" id="invite">Invite</button> <button type="button" id="join">Join</button> <button type="button" id="leave">Leave</button></span></h1>
    <span class="lobby"><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div></span>
    <span class="lobbyctrl"><button type="button" id="ready">Ready</button></span>
    <?php } else {
    ?><h1>Lobby</h1><h2 style="margin-bottom: 1em">The lobby feature requires yout to log in</h2><?php } ?>

    <h1>Region</h1>
    <ul class="regions">
        <li><label style="background-image: URL(assets/rg_use.png);"><input type="checkbox" value="USE" /><span>US East</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_usw.png);"><input type="checkbox" value="USW" /><span>US West</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_sa.png);"><input type="checkbox" value="SA" /><span>South America</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_eu.png);"><input type="checkbox" value="EU" /><span>Europe</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_as.png);"><input type="checkbox" value="AS" /><span>Asia</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_au.png);"><input type="checkbox" value="AU" /><span>Australia</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_me.png);"><input type="checkbox" value="ME" /><span>Middle East</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/rg_af.png);"><input type="checkbox" value="AF" /><span>Africa</span><span class="servercount">...</span></label></li>
    </ul>

    <h1>Maps</h1>
    <label class="mapgroup"><h2>Attack / Defend<input type="checkbox" value="c:attackdefend" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/cp_altitude.png);"><input type="checkbox" value="cp_altitude" /><span>cp_altitude</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_brew.png);"><input type="checkbox" value="cp_brew" /><span>cp_brew</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_dustbowl.png);"><input type="checkbox" value="cp_dustbowl" /><span>cp_dustbowl</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_egypt_final.png);"><input type="checkbox" value="cp_egypt_final" /><span>cp_egypt_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_gorge.png);"><input type="checkbox" value="cp_gorge" /><span>cp_gorge</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_gravelpit.png);"><input type="checkbox" value="cp_gravelpit" /><span>cp_gravelpit</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_hardwood_final.png);"><input type="checkbox" value="cp_hardwood_final" /><span>cp_hardwood_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_junction_final.png);"><input type="checkbox" value="cp_junction_final" /><span>cp_junction_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_mercenarypark.png);"><input type="checkbox" value="cp_mercenarypark" /><span>cp_mercenarypark</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_mossrock.png);"><input type="checkbox" value="cp_mossrock" /><span>cp_mossrock</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_mountainlab.png);"><input type="checkbox" value="cp_mountainlab" /><span>cp_mountainlab</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_steel.png);"><input type="checkbox" value="cp_steel" /><span>cp_steel</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_sulfur.png);"><input type="checkbox" value="cp_sulfur" /><span>cp_sulfur</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Control Point<input type="checkbox" value="c:controlpoint" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/cp_5gorge.png);"><input type="checkbox" value="cp_5gorge" /><span>cp_5gorge</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_badlands.png);"><input type="checkbox" value="cp_badlands" /><span>cp_badlands</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_coldfront.png);"><input type="checkbox" value="cp_coldfront" /><span>cp_coldfront</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_fastlane.png);"><input type="checkbox" value="cp_fastlane" /><span>cp_fastlane</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_foundry.png);"><input type="checkbox" value="cp_foundry" /><span>cp_foundry</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_freight_final1.png);"><input type="checkbox" value="cp_freight_final1" /><span>cp_freight_final1</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_granary.png);"><input type="checkbox" value="cp_granary" /><span>cp_granary</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_gullywash_final1.png);"><input type="checkbox" value="cp_gullywash_final1" /><span>cp_gullywash_final1</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_metalworks.png);"><input type="checkbox" value="cp_metalworks" /><span>cp_metalworks</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_powerhouse.png);"><input type="checkbox" value="cp_powerhouse" /><span>cp_powerhouse</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_process_final.png);"><input type="checkbox" value="cp_process_final" /><span>cp_process_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_reckoner.png);"><input type="checkbox" value="cp_reckoner" /><span>cp_reckoner</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_snakewater_final1.png);"><input type="checkbox" value="cp_snakewater_final1" /><span>cp_snakewater_final1</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_standin_final.png);"><input type="checkbox" value="cp_standin_final" /><span>cp_standin_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_sunshine.png);"><input type="checkbox" value="cp_sunshine" /><span>cp_sunshine</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_vanguard.png);"><input type="checkbox" value="cp_vanguard" /><span>cp_vanguard</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_yukon_final.png);"><input type="checkbox" value="cp_yukon_final" /><span>cp_yukon_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_well.png);"><input type="checkbox" value="cp_well" /><span>cp_well</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Capture the Flag<input type="checkbox" value="c:capturetheflag" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/ctf_2fort.png);"><input type="checkbox" value="ctf_2fort" /><span>ctf_2fort</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_doublecross.png);"><input type="checkbox" value="ctf_doublecross" /><span>ctf_doublecross</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_landfall.png);"><input type="checkbox" value="ctf_landfall" /><span>ctf_landfall</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_pelican_peak.png);"><input type="checkbox" value="ctf_pelican_peak" /><span>ctf_pelican_peak</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_sawmill.png);"><input type="checkbox" value="ctf_sawmill" /><span>ctf_sawmill</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_turbine.png);"><input type="checkbox" value="ctf_turbine" /><span>ctf_turbine</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_well.png);"><input type="checkbox" value="ctf_well" /><span>ctf_well</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>King of the Hill<input type="checkbox" value="c:kingofthehill" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/koth_badlands.png);"><input type="checkbox" value="koth_badlands" /><span>koth_badlands</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_brazil.png);"><input type="checkbox" value="koth_brazil" /><span>koth_brazil</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_cascade.png);"><input type="checkbox" value="koth_cascade" /><span>koth_cascade</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_harvest_final.png);"><input type="checkbox" value="koth_harvest" /><span>koth_harvest</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_highpass.png);"><input type="checkbox" value="koth_highpass" /><span>koth_highpass</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_king.png);"><input type="checkbox" value="koth_king" /><span>koth_king</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_lakeside_final.png);"><input type="checkbox" value="koth_lakeside_final" /><span>koth_lakeside_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_lazarus.png);"><input type="checkbox" value="koth_lazarus" /><span>koth_lazarus</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_nucleus.png);"><input type="checkbox" value="koth_nucleus" /><span>koth_nucleus</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_rotunda.png);"><input type="checkbox" value="koth_rotunda" /><span>koth_rotunda</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_sawmill.png);"><input type="checkbox" value="koth_sawmill" /><span>koth_sawmill</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_sharkbay.png);"><input type="checkbox" value="koth_sharkbay" /><span>koth_sharkbay</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_suijin.png);"><input type="checkbox" value="koth_suijin" /><span>koth_suijin</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_viaduct.png);"><input type="checkbox" value="koth_viaduct" /><span>koth_viaduct</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Payload<input type="checkbox" value="c:payload" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/pl_badwater.png);"><input type="checkbox" value="pl_badwater" /><span>pl_badwater</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_barnblitz.png);"><input type="checkbox" value="pl_barnblitz" /><span>pl_barnblitz</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_borneo.png);"><input type="checkbox" value="pl_borneo" /><span>pl_borneo</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_breadspace.png);"><input type="checkbox" value="pl_breadspace" /><span>pl_breadspace</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_camber.png);"><input type="checkbox" value="pl_camber" /><span>pl_camber</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_cashworks.png);"><input type="checkbox" value="pl_cashworks" /><span>pl_cashworks</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_emerge.png);"><input type="checkbox" value="pl_emerge" /><span>pl_emerge</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_enclosure_final.png);"><input type="checkbox" value="pl_enclosure_final" /><span>pl_enclosure_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_frontier_final.png);"><input type="checkbox" value="pl_frontier_final" /><span>pl_frontier_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_goldrush.png);"><input type="checkbox" value="pl_goldrush" /><span>pl_goldrush</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_hoodoo_final.png);"><input type="checkbox" value="pl_hoodoo_final" /><span>pl_hoodoo_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_phoenix.png);"><input type="checkbox" value="pl_phoenix" /><span>pl_phoenix</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_pier.png);"><input type="checkbox" value="pl_pier" /><span>pl_pier</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_snowycoast.png);"><input type="checkbox" value="pl_snowycoast" /><span>pl_snowycoast</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_swiftwater_final1.png);"><input type="checkbox" value="pl_swiftwater_final1" /><span>pl_swiftwater_final1</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_thundermountain.png);"><input type="checkbox" value="pl_thundermountain" /><span>pl_thundermountain</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_upward.png);"><input type="checkbox" value="pl_upward" /><span>pl_upward</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pl_venice.png);"><input type="checkbox" value="pl_venice" /><span>pl_venice</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Payload Race<input type="checkbox" value="c:payloadrace" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/plr_bananabay.png);"><input type="checkbox" value="plr_bananabay" /><span>plr_bananabay</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/plr_hightower.png);"><input type="checkbox" value="plr_hightower" /><span>plr_hightower</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/plr_nightfall_final.png);"><input type="checkbox" value="plr_nightfall_final" /><span>plr_nightfall_final</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/plr_pipeline.png);"><input type="checkbox" value="plr_pipeline" /><span>plr_pipeline</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Misc<input type="checkbox" value="c:misc" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/ctf_2fort_invasion.png);"><input type="checkbox" value="ctf_2fort_invasion" /><span>ctf_2fort_invasion</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_degrootkeep.png);"><input type="checkbox" value="cp_degrootkeep" /><span>cp_degrootkeep</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/vsh_distillery.png);"><input type="checkbox" value="vsh_distillery" /><span>vsh_distillery</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/sd_doomsday.png);"><input type="checkbox" value="sd_doomsday" /><span>sd_doomsday</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/tc_hydro.png);"><input type="checkbox" value="tc_hydro" /><span>tc_hydro</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/vsh_nucleus.png);"><input type="checkbox" value="vsh_nucleus" /><span>vsh_nucleus</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/koth_probed.png);"><input type="checkbox" value="koth_probed" /><span>koth_probed</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pd_selbyen.png);"><input type="checkbox" value="pd_selbyen" /><span>pd_selbyen</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/vsh_skirmish.png);"><input type="checkbox" value="vsh_skirmish" /><span>vsh_skirmish</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/cp_snowplow.png);"><input type="checkbox" value="cp_snowplow" /><span>cp_snowplow</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/vsh_tinyrock.png);"><input type="checkbox" value="vsh_tinyrock" /><span>vsh_tinyrock</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pd_watergate.png);"><input type="checkbox" value="pd_watergate" /><span>pd_watergate</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Mannpower<input type="checkbox" value="c:mannpower" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/ctf_foundry.png);"><input type="checkbox" value="ctf_foundry" /><span>ctf_foundry</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_gorge.png);"><input type="checkbox" value="ctf_gorge" /><span>ctf_gorge</span><span class="servercount">...</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_hellfire.png);"><input type="checkbox" value="ctf_hellfire" /><span>ctf_hellfire</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/ctf_thundermountain.png);"><input type="checkbox" value="ctf_thundermountain" /><span>ctf_thundermountain</span><span class="servercount">...</span></label></li>
    </ul><label class="mapgroup"><h2>Passtime<input type="checkbox" value="c:passtime" /></h2></label><ul class="mapchoice">
        <li><label style="background-image: URL(assets/maps/pass_brickyard.png);"><input type="checkbox" value="pass_brickyard" /><span>pass_brickyard</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pass_district.png);"><input type="checkbox" value="pass_district" /><span>pass_district</span><span class="servercount">...</span></label></li>
        <li><label style="background-image: URL(assets/maps/pass_timbertown.png);"><input type="checkbox" value="pass_timbertown" /><span>pass_timbertown</span><span class="servercount">...</span></label></li>
    </ul>

    <div id="soundboard" style="display:none;">
        <audio name="click"><source src="assets/click.mp3" type="audio/mp3"></audio>
        <audio name="click_off"><source src="assets/click_off.mp3" type="audio/mp3"></audio>
        <audio name="ready_up"><source src="assets/ready_up.mp3" type="audio/mp3"></audio>
        <audio name="ready_down"><source src="assets/ready_down.mp3" type="audio/mp3"></audio>
        <audio name="queue"><source src="assets/queue.mp3" type="audio/mp3"></audio>
        <audio name="redirect"><source src="assets/queue_done.mp3" type="audio/mp3"></audio>
    </div><?php

    return "Lobby";
}
