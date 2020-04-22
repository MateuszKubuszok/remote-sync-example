# Favourites synchronisation service

This is PoC of a simple synchronisation service that connects (at the moment only) to Deezer, fetches favourites
and synchronised them to the database. If continued this project could be used to e.g. synchronisation of favourites
between Deeze, Spotify, etc.

Currently it:

 * fetches data for a single user and synchronizes it with a database

## Usage of PoC

You have to have JVM and Docker installed.

1. obtain Deezer `ACCESS_TOKEN` (you can find it in a section below - it is valid for 60 minutes)
2. start sbt with `DEEZER_ACCESS_TOKEN` set up. You can use `./sbt` launcher
   ```
   DEEZER_ACCESS_TOKEN=[access token] ./sbt
   ```
3. initiate Postgres with `docker-compose up`
4. start test run with `reStart`

You might want to test against in-memory implementation instead of database if you run with argument `in-memory`.

## `ACCESS_TOKEN`

* create a new application on [Deezer](https://developers.deezer.com/myapps/create) and set:
  ```
  domain: http://localhost
  redirect url after ToC: http://localhost/callback
  ```
* start in terminal:
  ```
  sudo nc -lp 80
  ```
  this will allow you to catch the callback with access token
* run:
  ```
  https://connect.deezer.com/oauth/auth.php?app_id=[YOUR_APP_ID]&redirect_uri=http://localhost/callback&perms=basic_access,listening_history
  ```
  replacing `[YOUR_APP_ID]` with the ID you generated in the first step
* in `nc` log you'll see something like
  ```
  GET /callback?code=fra726ee3b2768fad09e8bb53c4f8893 HTTP/1.1
  ```
  copy the part after `code`
* query:
  ```
  https://connect.deezer.com/oauth/access_token.php?app_id=[YOUR_APP_ID]&secret=[YOUR_APP_SECRET]&code=[CODE_FROM_ABOVE]
  ```
* set `DEEZER_ACCESS_TOKEN` environment variable to value obtained from this call

## TODO in PoC

There are clients, models and database queries implemented for synchronising favourite albums and tracks, but I didn't
manage to implement the routines yet.
