<?php
// this file is for api requests
ini_set('display_errors', 1); ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// sql helper
require_once "includes/dbcon.i.php";

require_once "includes/steamidutils.i.php";

// session
session_name($session_name);
session_set_cookie_params(7*24*3600, '/', 'tf2casual.community', true);
session_start();

// guard flag so processors know how the endpoint is accessed
$fromAction = true;

// special actions, these bypass our system

if ($_GET['do']=='login') {
    include("action/login.php");
} elseif ($_GET['do']=='logout') {
    include("action/logout.php");
} elseif ($_GET['do']=='usertags') {
    include("action/loadusers.php");
} elseif ($_GET['do']=='servercount') {
    include("action/servercount.php");
} else {
    die ("Invalid action");
}
