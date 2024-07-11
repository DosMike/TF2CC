<?php

function process() {
    $address = $_GET['IP'] ?? '';
    if (preg_match('/^[0-9]{1,3}(\\.[0-9]{1,3}){3}:[0-9]{2,5}$/', $address) !== 1)
        return ['Error' => 'Invalid request'];
    $ipport = explode(':', $address);

    sqlSelect('servers', ['Map', 'Name'], "`Address` = '{$ipport[0]}' AND `Port` = '{$ipport[1]}' AND `Enabled` = 1 AND `LastUpdate` > DATE_SUB(NOW(), INTERVAL 1 hour)");
    if (($row=sqlGetRow())===null)
        return ['Error' => 'Server is not part of this network'];

    return ['Name' => $row['Name'], 'Map' => $row['Map'], 'Address' => $_GET['IP']];
}