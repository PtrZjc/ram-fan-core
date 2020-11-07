CREATE SCHEMA IF NOT EXISTS ram_data;

CREATE TABLE IF NOT EXISTS ram_data.character (
    id                  SMALLINT        PRIMARY KEY,
    name                TEXT,
    status              TEXT,
    species             TEXT,
    type                TEXT,
    gender              TEXT,
    origin_id           SMALLINT,
    location_id         SMALLINT,
    image               BYTEA
);

CREATE TABLE IF NOT EXISTS ram_data.episode (
    id                  SMALLINT PRIMARY KEY,
    name                TEXT,
    air_date            TEXT,
    episode             TEXT
);

CREATE TABLE IF NOT EXISTS ram_data.location (
    id                  SMALLINT PRIMARY KEY,
    name                TEXT,
    type                TEXT,
    dimension           TEXT
);

CREATE TABLE IF NOT EXISTS ram_data.character_episode (
    character_id        SMALLINT        NOT NULL        REFERENCES ram_data.character,
    episode_id          SMALLINT        NOT NULL        REFERENCES ram_data.episode,
    PRIMARY KEY (character_id, episode_id)
);

CREATE TABLE IF NOT EXISTS ram_data.character_location (
    character_id        SMALLINT        NOT NULL        REFERENCES ram_data.character,
    location_id         SMALLINT        NOT NULL        REFERENCES ram_data.location,
    PRIMARY KEY (character_id, location_id)
);