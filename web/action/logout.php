<?php
require_once("includes/authmain.php");

if (empty($fromAction)) die("Illegal direct invocation");

if (!empty($_SESSION["dbid"])) sqlDelete('sessions', ['ID'=>$_SESSION["dbid"]]);

session_unset();
session_destroy();
$auth->LogOut();

header("Location: /{$webroot}");