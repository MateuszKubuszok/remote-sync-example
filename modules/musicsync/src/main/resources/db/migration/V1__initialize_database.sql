-- create table users

CREATE TABLE users (
  id UUID PRIMARY KEY
);


-- create mock user

INSERT INTO users (id)
VALUES ('c380d6cb-b66d-40ac-a2a0-fd11c53be7db');

-- create artist tables

CREATE TABLE artists (
  id        UUID PRIMARY KEY,
  deezer_id INT  UNIQUE,
  name      TEXT NOT NULL
);

CREATE TABLE user_artists (
  user_id    UUID   NOT NULL UNIQUE,
  artist_ids UUID[]
);


-- create albums tables

CREATE TABLE albums (
  id           UUID PRIMARY KEY,
  deezer_id    INT  UNIQUE,
  artist_id    UUID NOT NULL REFERENCES artists,
  title        TEXT NOT NULL,
  release_date DATE NOT NULL
);

CREATE TABLE user_albums (
  user_id    UUID   NOT NULL UNIQUE,
  albums_ids UUID[]
);


-- create albums tables

CREATE TABLE tracks (
  id           UUID PRIMARY KEY,
  deezer_id    INT  UNIQUE,
  artist_id    UUID NOT NULL REFERENCES artists,
  title        TEXT NOT NULL,
  duration     INT  NOT NULL
);

CREATE TABLE user_tracks (
  user_id    UUID   NOT NULL UNIQUE,
  tracks_ids UUID[]
);
