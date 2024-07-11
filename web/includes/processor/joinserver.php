<?php

function process() {
    global $render;
    if ($render != "html") return [];

    $address = $_GET['ip'] ?? '';
    if (preg_match('/^[0-9]{1,3}(\\.[0-9]{1,3}){3}:[0-9]{2,5}$/', $address) === false)
        return ['Error' => 'Invalid request (2)'];
    $ipport = explode(':', $address);

    sqlSelect('servers', Condition: ['IP' => $ipport[0], 'Port' => $ipport[1]]);
    if (($row=sqlGetRow())===null)
        return ['Error' => 'Server is not part of this network'];

    return [];
}