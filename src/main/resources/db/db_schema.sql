CREATE TABLE IF NOT EXISTS public.character (
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

CREATE TABLE IF NOT EXISTS public.episode (
    id                  SMALLINT PRIMARY KEY,
    name                TEXT,
    air_date            TEXT,
    episode             TEXT
);

CREATE TABLE IF NOT EXISTS public.location (
    id                  SMALLINT PRIMARY KEY,
    name                TEXT,
    type                TEXT,
    dimension           TEXT
);

CREATE TABLE IF NOT EXISTS public.character_episode (
    character_id        SMALLINT        NOT NULL        REFERENCES public.character,
    episode_id          SMALLINT        NOT NULL        REFERENCES public.episode,
    PRIMARY KEY (character_id, episode_id)
);

CREATE TABLE IF NOT EXISTS public.character_location (
    character_id        SMALLINT        NOT NULL        REFERENCES public.character,
    location_id         SMALLINT        NOT NULL        REFERENCES public.location,
    PRIMARY KEY (character_id, location_id)
);