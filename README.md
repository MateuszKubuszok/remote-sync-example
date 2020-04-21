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
  https://connect.deezer.com/oauth/access_token.php?app_id=[YOUR_APP_ID]]&secret=[YOUR_APP_SECRET]]&code=[CODE_FROM_ABOVE]]
  ```
* set `DEEZER_ACCESS_TOKEN` environment variable to value obtained from this call
