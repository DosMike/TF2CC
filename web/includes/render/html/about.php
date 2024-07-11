<?php

function htmlRender($data) {
    require_once('includes/Parsedown.php');
    $parsedown = new Parsedown();
    echo $parsedown->text(<<<ABOUT_END
# About
Community Casual is an alternative match making system for TF. The idea is very similar to [mastercomfig's Community QuickPlay](https://comfig.app/quickplay/), giving control over the servers back
to the community, but keeping some Casual features. Namely lobbies and map choice.

This project aims to maintain a currated list of servers that are suitable for match making. Servers are initially picked though the following rules, but
communities can reach out to have their networks removed/added if needed.

 - Servers must be listed on the server browser, and must not use strict matchmaking.
 - Servers must not be password protected.
 - Servers must have VAC enabled.
 - Servers must have a public, non-SDR ([Steam Datagram Relay](https://partner.steamgames.com/doc/features/multiplayer/steamdatagramrelay)) IP, i.e. do not use the -enablefakeip launch param. This is so your server has a consistent IP.
 - Servers must respond to A2S queries in a reasonable amount of time.
 - Servers must be running on a dedicated server (srcds, not a listen server).
 - The server must have a max player cap between 24 and 32.
 - The server must be running any active casual rotation map.
 - The server must not have the following tags: friendlyfire, highlander, trade, noquickplay, hidden, rtd, deathmatch, jump, ff2 (among some other common gamemode tags)
 - Servers that violate [the community guidelines](https://help.steampowered.com/en/faqs/view/6862-8119-C23E-EA7B) may be delisted.
 - Servers must not actively trick players or lie to them or make it difficult for them to find what they are looking for. Servers must report accurate information about itself to the Steam infrastructure and to clients.
 - All of the information in the server browser must be accurate: number of players, number of bots, map, ping, region, etc.
 - Bots must be easily identified by players as bots. Do not give a bot an avatar or assign it a ping value.
 - Servers cannot name themselves according to the naming conventions used by Valve, or any other server organization.
 - Servers must not be malicious, by exposing clients to malware or spyware, or by bypassing cl_disablehtmlmotd.
 - The engine uses tags to communicate to clients that certain modifications are in effect. Servers must use these tags properly. If a server makes these modifications using built-in engine functionality, they will be set automatically, and all a server needs to do is make sure they don't actively block them from working. However, if the server uses a plugin to make gameplay modifications with very similar effects, it is THEIR responsibility to ensure that the tags are set. Players do not care how the gameplay modification was implemented, they just want to know whether it is active or not.
 - Here are some examples of tags that must be set accurately: friendlyfire, respawntimes, norespawntime, increased_maxplayers, nocrits, dmgspread, highlander, gravity, mvm
 - No opening a MOTD window (hidden or visible) that is not requested
 - No forcing clients to view the MOTD until a timer has expired
 - No browser popups
 - No giving or selling gameplay advantage to players
 - No kicking players to make room for reserved slots. Players need to be able to reliably queue into a server
 - No modifying stock maps, models, or materials
 - No running non-default game modes: prop hunt, dodgeball, class wars, randomizer, engineer fortress, etc
 - No running non-default addons like custom weapons, x10, all crits, etc
 - No disabling objectives
 - No granting or modifying economy items, or taking actions that devalue players' items, or interfering with the TF2 economy. This was enforced in the old Quickplay system.
 - You must advertise your game name as "Team Fortress", the default. This was enforced in the old Quickplay system.
 - Your sv_region should be set to the server's actual region.
 - No using invalid characters in the server name to increase its sorting order.
 - Servers must be registered with a game server account tied to an owning Steam account, and log into that account when the server boots using sv_setsteamaccount.

 This list is based on mastercomfigs list (it's just a very good list, OK).

## How it works:

- You log in with Steam so we can put you in lobbies with other players.
- Once everyone is ready, the match maker in the backend tries to find a server with one of the selected regions and maps.
- Once a server is found, the web browser opens a steam://connect/ link to the servers IP for every player.
- If you close the website or log out, you drop from a lobby.

Servers are polled in a fixed interval, providing a good estimate over what maps are ran and how full servers are.
The servers are checked again before trying to queue in a lobby to avoid joining empty or full servers as much as possible.
However, since the servers are not running any special plugins to reserver your slots, other players can still join before you!
To minimize the risk of not getting in a server, it's recommended to open the game before and always allowing the website to open steam:// links.
Please note that the ingame overlay can not automatically connect you to a server!

# <br>Attributions

Map images are sourced from the [TF2 Wiki](https://wiki.teamfortress.com/wiki/Main_Page)

The TF2 Build font is from TF2 (duh)

Sounds used:
- https://freesound.org/people/Andreas.Mustola/sounds/256282/
- https://freesound.org/people/mediatheksuche/sounds/623712/
- https://freesound.org/people/newlocknew/sounds/720588/
- https://freesound.org/people/DJames619/sounds/389250/
- https://freesound.org/people/FOSSarts/sounds/740227/

Valve, the Valve logo, Steam, the Steam logo, Team Fortress, the Team Fortress logo, Source, the Source logo are trademarks and/or registered trademarks of Valve Corporation in the U.S. and/or other countries. All other trademarks are property of their respective owners in the US and other countries.

# <br>TF2 Community Casual's Privacy Policy
This Privacy Policy governs the manner in which TF2 Community Casual collects, uses, maintains and discloses information collected from users (each, a "User") of the TF2 Community Casual's website ("Site","TF2CC"). This privacy policy applies to the Site and all products and services offered by TF2CC.

## TF2CC access to SteamCommunity Data
TF2CC collects information made publicly available by you via the SteamAPI which allows services such as TF2CC to collect data. The User that has generated content for said Site has agreed its use under the [Steam Subscriber](http://store.steampowered.com/subscriber_agreement/#6) agreement. This Privacy policy has been written to conform with the SteamAPI policy

**TF2CC does not have access or collect your personal SteamCommunity data**

An example of a steam API call can be viewed [here](https://steamcommunity.com/id/dosmike?xml=1) Please note this old XML method has been deprecated for some years but provides a basic example of what an API is.
There is a [Steam Web API Wiki](https://developer.valvesoftware.com/wiki/Steam_Web_API) if you would like to read more into the current SteamAPI and how to use this yourself and or to learn how TF2CC and other websites collects this information from Steam.

## What is the non-personal information we collect from Steam used for?
The data that we collect about gaming profiles is used to show a representation of all Users in a Users lobby (Lobby section of the Play/front-page).
This service includes but not limited to displaying user name and avatar.

Further expansion on data collected from the SteamAPI and how we deal with certain fields
Other information passed though on the SteamAPI is not stored in a database or processed with TF2CC.


## The right to erasure (the right to be forgotten) / GDPR / DataProtection / Personally identifiable information / Opt out
### SteamCommunity Data
TF2CC has no access to personal information held on steam servers only what we are given over the public Steam API (For API data held by TF2CC please see Data Removal)
TF2CC Understands common requests surrounding the Gaming avatar field "avatarIcon" on the SteamAPI which is available from Steam via the community API
* <b>Gaming avatar</b>
    TF2CC does not store the avatar but does store the URL to this image in the database.


## GDPR / CCPA
What personal information can I expect TF2CC to store
* None

## Removal of non-personal data collected from the SteamAPI
non-personal data

All non-personal data, including but not limited to user name and avatar, are removed from the database as soon as the User actively logs out.

## Non-Personal identification information

### Web browser cookies

Our Site may use "cookies" to enhance User experience. User's web browser places cookies on their hard drive for record-keeping purposes and sometimes to track information about them. User may choose to set their web browser to refuse cookies, or to alert you when cookies are being sent. If they do so, note that some parts of the Site may not function properly.

### Third party websites

Users may find advertising or other content on our Site that link to the sites and services of our partners, suppliers, advertisers, sponsors, licensors and other third parties. We do not control the content or links that appear on these sites and are not responsible for the practices employed by websites linked to or from our Site. In addition, these sites or services, including their content and links, may be constantly changing. These sites and services may have their own privacy policies and customer service policies. Browsing and interaction on any other website, including websites which have a link to our Site, is subject to that website's own terms and policies.

## Changes to this privacy policy

TF2CC has the discretion to update this privacy policy at any time so we recommend you check back when using the TF2CC.

## Your acceptance of these terms

By using this Site, you signify your acceptance of this policy and terms of service. If you do not agree to this policy, please do not use our Site. Your continued use of the Site following the posting of changes to this policy will be deemed your acceptance of those changes.

ABOUT_END);
    return "About";
}
