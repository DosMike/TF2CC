<?php

if (empty($fromAction)) die("Illegal direct invocation");

header('Content-Type: application/json');

$result = ['maps'=>[], 'regions'=>[]];

require_once("includes/mapgroups.php");

$mappool = [];
foreach ($mapgroups as $k => $entry) {
    $mappool = array_merge($mappool, $entry['maps']);
}

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