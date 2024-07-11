<?php

if (empty($fromAction)) die("Illegal direct invocation");

header('Content-Type: application/json');

if (empty($_GET['ids'])) die('[]');

$numbers = [];
foreach ($_GET['ids'] as $id) $numbers[] = intval($id);
if (count($numbers) > 6 || empty($numbers)) die('[]');

sqlSelect('sessions', ['AccountID', 'DisplayName', 'AvatarURL'], '`AccountID` IN ('.implode(', ', $numbers).')');
$result = [];
while(($row = sqlGetRow())!==null) {
    $result[] = [
        'accountid' => $row['AccountID'],
        'username' => $row['DisplayName'],
        'avatar' => $row['AvatarURL'],
    ];
}

echo json_encode($result);