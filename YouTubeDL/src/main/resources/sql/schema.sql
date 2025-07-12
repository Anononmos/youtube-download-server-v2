CREATE TABLE updated (
    id BOOL PRIMARY KEY DEFAULT true,
    last_updated Date Not Null DEFAULT CURRENT_DATE,
    CONSTRAINT id CHECK (id)
);

CREATE TABLE video (
    -- Video properties
    media Media NOT NULL, 
    id CHAR(11) NOT NULL PRIMARY KEY, 
    title VARCHAR(127), 
    channel VARCHAR(255),
    channel_id CHAR(24) NOT NULL,
    uploaded DATE NOT NULL,

    -- File properties
    duration INTERVAL NOT NULL,
    resolution SMALLINT, 
    downloaded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(255) NOT NULL, 

    CONSTRAINT category CHECK ( category in ('Audio', 'Video') ), 
    CONSTRAINT resolution CHECK ( resolution in (NULL, 144, 240, 360, 480, 720, 1080, 1440) )
);

CREATE TABLE channel (
    id CHAR(11) NOT NULL PRIMARY KEY, 
    name VARCHAR(127), 
);