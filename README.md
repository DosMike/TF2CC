
# TF2 Community Casual

This is a system implementing a 3rd party match making for a casual-like experience in TF2.

## Structure
This project is a Dockerized package with slim frontend, and a mixed backend. Static data from the database and login management is handled though PHP, while the lobby management, search configuration and scrapping is managed through a WebSocket in a Java application. Traefik is used for TLS and to delegate connections to the correct containers.

The idea is to use the network of public server instance, filtering them for vanilla gameplay. Lobbies are managed through a website with "sign-in though Steam". Find other players via Lobby-invite codes and allow them to pick what maps they want to play. The custom match maker then picks a server and automatically sends players to the instance via steam://connect/ links.

Additional features like avoid lists or community favorite maps would be possible as well, but are currently not implemented.

## Local Setup
A lot of stuff was developed locally, but might no longer fully support that since the whole project moved to a server. Some features just required a domain, and from that point on, it might have gotten hard-coded into some places. You can try to `docker compose up --build` it anyways if you want :)

The `apache` and `php` containers are just there to host the `web` directory. `matchmaker` is the Java application that you can build locally, but it will probably not work without the shared `db` container, that hosts the SQL database (If you have the AMP stack running you can drop e.g. adminer into the web directory to manage the database). The first time you set up, you'll have to create the tables as defined in the .sql file in the `internal` directory. The `minify` container is a one-shot container that minifies the JavaScript and CSS files on startup. And all the rest is handled by the `traefik` container.

