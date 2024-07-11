<?php
if (empty($fromIndex) && empty($fromAction)) die("Render not from main");

/** wrapper to prefix webroot */
function makeLink($page) {
    global $webroot;
    return str_replace('//', '/', "/{$webroot}/{$page}");
}
function makeAction($actionSlug) {
    global $webroot;
    return str_replace('//', '/', "/{$webroot}/action.php?do={$actionSlug}");
}

function loginButton() {
    if (empty($_SESSION['steamid'])) {
        ?><a id="login" href="<?=makeAction('login')?>"><img src="assets/steam_login.png" /></a><?php
    } else {
        $steamid = new SteamID($_SESSION['steamid']);
        ?><a id="logout" href="<?=makeAction('logout')?>" data-accountid="<?=$steamid->accountID?>"><span class="user"><?= $_SESSION['username'] ?><br><span>Click to Log out</span></span><img class="avatar" src="<?= $_SESSION['avatar'] ?>" /></a><?php
    }
}

function output($type, $data) {
    global $Authorization;
	//spliterating this more allows for cleaner per-page content
	require "html/".strtolower($type).".php";
    $title = "TF2 Community Casual";
    if (strcasecmp($type, "dashboard")!=0) $title .= " | ". ucwords($type);
    $description = "Play the casual map-pool on community servers";

    ?><!DOCTYPE html>
<html lang="en"><head>

    <link href="/assets/icon512.png" rel="icon" />
    <link href="/css/style.min.css" rel="stylesheet" />
    <link href="/css/alerty.min.css" rel="stylesheet" />
    <script src="/script/alerty.min.js"></script><?php

    if (function_exists('htmlHeader')) {
        $meta_patch = htmlHeader($data);
        if (!empty($meta_patch)) {
            if (array_key_exists("title", $meta_patch)) $title = $meta_patch['title'];
            if (array_key_exists("description", $meta_patch)) $description = $meta_patch['description'];
        }
    }
    ?>

    <meta charset="utf-8" />
    <title><?=$title?></title>
    <!-- SEO TAGS yay, everybody loves SEO -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name=viewport content="width=600">
    <meta name="description" content="<?=$description?>">
    <meta name="theme-color" content="#cf7336">
    <meta property="og:title" content="<?=$title?>" />
    <meta property="og:description" content="<?=$description?>">
    <meta property="og:type" content="website" />
    <meta property="og:image" content="https://tf2casual.community/assets/icon512.png" />
    <meta name="twitter:card" content="app">

    <link rel="manifest" href="manifest.json" />

</head><body>
    <main><?php

    $crumb = htmlRender($data);

    function hereClass($type, $target) { return (strtolower($type) == $target) ? ' class="here"' : ""; }

    ?></main>
    <header><span class="left"><span class="top"><a href="<?=makeLink('')?>"><h1>Community Casual</h1></a></span><span class="bot"><ul><?php
        ?><li><a href="<?=makeLink('')?>"<?=hereClass($type,'dashboard')?>>Play</a></li><?php
        ?><li><a href="<?=makeLink('servers')?>"<?=hereClass($type,'list')?>>Servers</a></li><?php
        ?><li><a href="<?=makeLink('news')?>"<?=hereClass($type,'news')?>>News</a></li><?php
        ?><li><a href="<?=makeLink('about')?>"<?=hereClass($type,'about')?>>About</a></li><?php
        ?><li><a href="https://discord.gg/bvdadSxMm2">Discord</a></li><?php
    ?></ul></span></span><span class="right"><?= loginButton() ?></span></header>
    <footer><a href="https://github.com/DosMike/TF2CC">Community Casual on GitHub</a> ♥ 24w28a - BETA</footer>
    <div class="queueoverlay">
        <div class="spinner"><div></div></div><h1>Queueing...</h1>
    </div>
</body></html><?php
}
