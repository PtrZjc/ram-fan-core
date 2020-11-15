DROP TABLE IF EXISTS ram_data.character CASCADE;
CREATE TABLE ram_data.character (
    id                  BIGINT         PRIMARY KEY,
    name                TEXT,
    status              TEXT,
    species             TEXT,
    type                TEXT,
    gender              TEXT,
    origin_id           BIGINT,
    location_id         BIGINT,
    image               BYTEA
);

DROP TABLE IF EXISTS ram_data.episode CASCADE;
CREATE TABLE ram_data.episode (
    id                  BIGINT         PRIMARY KEY,
    name                TEXT,
    air_date            TEXT,
    episode             TEXT
);

DROP TABLE IF EXISTS ram_data.location CASCADE;
CREATE TABLE ram_data.location (
    id                  BIGINT         PRIMARY KEY,
    name                TEXT,
    type                TEXT,
    dimension           TEXT
);

DROP TABLE IF EXISTS ram_data.character_episode CASCADE;
CREATE TABLE ram_data.character_episode (
    character_id        BIGINT        NOT NULL        REFERENCES ram_data.character,
    episode_id          BIGINT        NOT NULL        REFERENCES ram_data.episode,
    PRIMARY KEY (character_id, episode_id)
);

DROP TABLE IF EXISTS ram_data.character_location CASCADE;
CREATE TABLE ram_data.character_location (
    character_id        BIGINT        NOT NULL        REFERENCES ram_data.character,
    location_id         BIGINT        NOT NULL        REFERENCES ram_data.location,
    PRIMARY KEY (character_id, location_id)
);