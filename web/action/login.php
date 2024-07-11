<?php
require_once("includes/authmain.php");

if (empty($fromAction)) die("Illegal direct invocation");

function jcurl($at) {
    $ch = curl_init();
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_URL, $at);
	$result = curl_exec($ch);
	curl_close($ch);
	return json_decode($result, true);
}

if(!$auth->IsUserLoggedIn())
{
    header("Location: ".$auth->GetLoginURL());
}
else
{
    $steamid = new SteamID($auth->SteamID);
    $newacc = false;
    $_SESSION['id_changed'] = false;
    sqlSelect('sessions', ['ID', 'SessionID', 'DisplayName', 'AvatarURL'], ['AccountID'=>$steamid->accountID]);
    if (($row=sqlGetRow())!=null) {
        $_SESSION["dbid"] = $row['ID'];
        $_SESSION['id_changed'] = session_id() == $row['SessionID'];
    } else {
        $newacc = true;
    }

    $json = jcurl("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=".$steam_api_key."&steamids=".$auth->SteamID);

    $_SESSION["username"] = htmlspecialchars($json['response']['players'][0]['personaname']);
    $_SESSION["avatar"] = $json['response']['players'][0]['avatarmedium'];

    if ($newacc) {
        $_SESSION["dbid"] = sqlInsert('sessions', ['SessionID'=>sqlEscape(session_id()),'AccountID'=>$steamid->accountID,'DisplayName'=>$_SESSION["username"],'AvatarURL'=>$_SESSION["avatar"]]);
    } else {
        sqlUpdate('sessions', ['SessionID'=>sqlEscape(session_id()),'DisplayName'=>$_SESSION["username"],'AvatarURL'=>$_SESSION["avatar"]], ['AccountID'=>$steamid->accountID]);
    }

    header("Location: /{$webroot}");
}