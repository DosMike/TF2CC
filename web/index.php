<?php
// this file is for user facing pages
ini_set('display_errors', 1); ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// sql helpers
require_once "includes/dbcon.i.php";

// session
session_name($session_name);
session_set_cookie_params(7*24*3600, '/', 'tf2casual.community', true);
session_start();

// guard flag so processors know how the endpoint is accessed
$fromIndex = true;

// get all headers compytibility
if (function_exists('getallheaders')) {
	$_HEADER = getallheaders();
} else {
	$_HEADER = array ();
	foreach ($_SERVER as $name => $value) {
		if (substr($name, 0, 5) == 'HTTP_') {
			$headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
		}
	}
	return $headers;
}

// routing. source it from request url if there's no dot, ?p= otherwise
$route = '';
$requestbase = trim($_SERVER['REQUEST_URI'],'/');
if (str_starts_with($requestbase, $webroot)) {
	//remove web root if set
	$requestbase = ltrim(substr($requestbase, strlen($webroot)), '/');
}
if (!empty($requestbase)) {
	$requestbase = preg_split('/[?#]/', $requestbase)[0]; //drop query or fragment
	$route = ltrim($requestbase, '/');
}
// map route to page
if (empty($route) || 'index' == $route) {
	$contenttype = 'Dashboard';
} elseif ('news' == $route) {
	$contenttype = 'News';
} elseif ('about' == $route) {
	$contenttype = 'About';
} elseif ('servers' == $route) {
	$contenttype = 'list';
} elseif (str_starts_with($route, 'connect/')) {
	$_GET['IP'] = substr($route, 8);
	$contenttype = 'connect';
} else {
	http_response_code(404);
	$contenttype = 'Unknown';
}

// required a user agent
//  bad user agents can be blocked here as well
//  blocking user agents is intendet to lock out broken apps
if (empty($_HEADER['User-Agent'])) {
	http_response_code(400);
	die("Bad Request");
}

// other script dependencies here
require "includes/steamidutils.i.php";

// prepare/load page template
require "includes/render/html.php";

// load request processor
include "includes/processor/".strtolower($contenttype).".php";

// actually process the request and output
$result=process($contenttype);
if ($result !== false) {
	if (isset($result['Error'])) {
		// assume bad request unless we have an actual error code
		http_response_code(isset($restult['HttpCode']) ? $restult['HttpCode'] : 400);
	}
	output($contenttype, $result);
}