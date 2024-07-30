<?php

function htmlRender($data) {
    require_once('includes/Parsedown.php');
    $parsedown = new Parsedown();
    echo $parsedown->text(<<<NEWS_END
# News

## 24w31a

Finally, more than a week delayed, the summer update has also arrived on this website :D

Sorry for the delay, but I had a planned vacation to Ireland and of course Valve decided to push the update *just* the night before I left.

Anyways - New maps are indexed and I made the map lists in the background a bit more maintainable (hopefully).

~ UwU

## 24w28a

Fixed some minor issues, where you would always get cueued into an arbitrary first server, not it picks a random best. Also added a feature to
send a notification to everyone from the backend in case I restart the services, you should now at least know what stuff is breaking ;D

Oh and I guess I fixed some minor audio issues when loading the page with audio permissions (e.g. as PWA) during Cookie loading.

~ UwU

## 24w27a

The lobbies should work now, from invites to kicking. There is no block-list feature for players or servers yet, but that's not a focus right now
(let's get it to work at all first ;D)

The matchmaker is now in place as well, scanning for suitable servers as you ready up, and returning a matching configuration with a high population
if possible.

I also added a server list so you can see what servers are currently tracked (and complain about it on discord ig).
That reminds me, I also made a discord, in case you are looking for players or to report issues or something.

~ UwU and have fun!

## 24w26a

Installing a reverse proxy and getting a cert going for https took longer that i would have liked, but here we are:

    https://tf2casual.community

Now on to fixing my lobby management :P

~ UwU

## 24w25a

Yay, new project! Let's see for how long I will host this, server cost yada yada...

The front end is looking good so far, backend is mostly managing lobbies rn.
I also implemented the scrapper for community servers, from roughly 3000, it returns about 500 that pass the filter i currently set.
Maybe there's bugs, maybe I'm too strict, the future will tell :)

Moving this thing from my local setup to the hosting provider took more time than expected...
Frontend and lobby management should work, the rest I'll figure out next week.

Keep your eyes peeled for the GitHub upload in a few weeks(?) - I want to get it somewhat done before uplodaing it.

~ UwU and out!

NEWS_END);
    return "News";
}
