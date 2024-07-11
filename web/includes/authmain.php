<?php
if(empty(session_id())) {
    die ("What are you doing?");
}

require("SteamAuth/SteamAuth.class.php");

$auth = new SteamAuth();
$auth->SetOnLoginCallback(function($steamid){ return true; });
$auth->SetOnLoginFailedCallback(function(){ $_SESSION['last_login_failed']=true; });
$auth->Init();