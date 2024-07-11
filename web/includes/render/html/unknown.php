<?php

function htmlHeader($data) {
    return ['title'=>"TF2 Community Casual | Error"];
}

function htmlRender($data) {
    global $route;
    ?><content class="comment">
    <h1>Oops, something went wrong</h1>
    <p>The page you are looking for does not exist</p>
</content><?php
return $route."?";
}