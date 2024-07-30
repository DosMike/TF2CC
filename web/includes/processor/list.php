<?php

function process() {
    require_once("includes/mapgroups.php");

    $mappool = [];
    foreach ($mapgroups as $k => $entry) {
        $mappool = array_merge($mappool, $entry['maps']);
    }

    $total = 0;
    $result = [];
    sqlSelect('servers', ['Address', 'Port', 'Name', 'Map', 'Region'], '`Enabled` = 1 AND `LastUpdate` > DATE_SUB(NOW(), INTERVAL 1 hour)');
    while (($row=sqlGetRow())!==null) {
        $total += 1;
        if (!in_array($row['Map'], $mappool)) continue;
        $result[] = [
            'address' => $row['Address'].':'.$row['Port'],
            'name' => $row['Name'],
            'map' => $row['Map'],
            'region' => $row['Region'],
        ];
    }

    return ['raw_count'=>$total, 'servers'=>$result ];
}