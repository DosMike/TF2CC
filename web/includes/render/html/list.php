<?php


function htmlHeader($data) {
    return ['title'=>"TF2 Community Casual | Server List"];
}

function htmlRender($data) {

    ?><h1>Server List</h1><p>This is a dump of the servers that should currently be in the pool for play. There is no particular order to this. Currently <?=count($data['servers'])."/".$data['raw_count']?> eligible servers run maps from the casual pool to queue into.</p><ul class="serverlisting"><?php

    foreach ($data['servers'] as $server) {
        ?><li><a href="/connect/<?=$server['address']?>" target="_blank" style="background-image: URL(assets/maps/<?=$server['map']?>.png);"><span><?=htmlspecialchars($server['name'])?><br><?=$server['region']?> &bull; <?=$server['map']?></span></a></li><?php
    }

    ?></ul><?php

    return "Servers";
}