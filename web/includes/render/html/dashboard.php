<?php

function htmlHeader($data) {?>
    <script src="/script/request.min.js"></script>
    <script src="/script/app.min.js"></script><?php
}

function htmlRender($data) {
    require_once("includes/mapgroups.php");

    $loggedin = !empty($_SESSION['dbid']);

    if ($loggedin) {
    ?><h1>Lobby<span class="lobbyctrl2"><button type="button" id="invite">Invite</button> <button type="button" id="join">Join</button> <button type="button" id="leave">Leave</button></span></h1>
    <span class="lobby"><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div><div><img src="assets/avatar_blank.png" title="Not connected"/></div></span>
    <span class="lobbyctrl"><button type="button" id="ready">Ready</button></span>
    <?php } else {
    ?><h1>Lobby</h1><h2 style="margin-bottom: 1em">The lobby feature requires you to log in</h2><?php } ?>

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

    <h1>Maps</h1><?php
    foreach ($mapgroups as $name => $entry) { ?>
    <label class="mapgroup"><h2><?=$entry['label']?><input type="checkbox" value="c:<?=$name?>" /></h2></label><ul class="mapchoice"><?php
        foreach ($entry['maps'] as $map) { ?>
        <li><label style="background-image: URL(assets/maps/<?=$map?>.png);"><input type="checkbox" value="<?=$map?>" /><span><?=$map?></span><span class="servercount">...</span></label></li><?php
        }
        ?>
    </ul><?php
    }
    ?>

    <div id="soundboard" style="display:none;">
        <audio name="click"><source src="assets/click.mp3" type="audio/mp3"></audio>
        <audio name="click_off"><source src="assets/click_off.mp3" type="audio/mp3"></audio>
        <audio name="ready_up"><source src="assets/ready_up.mp3" type="audio/mp3"></audio>
        <audio name="ready_down"><source src="assets/ready_down.mp3" type="audio/mp3"></audio>
        <audio name="queue"><source src="assets/queue.mp3" type="audio/mp3"></audio>
        <audio name="redirect"><source src="assets/queue_done.mp3" type="audio/mp3"></audio>
    </div><?php

    return "Lobby";
};
