CREATE TABLE updated (
    id BOOL PRIMARY KEY DEFAULT true,
    last_updated Date Not Null DEFAULT CURRENT_DATE,
    CONSTRAINT id CHECK (id)
);

CREATE TABLE CHANNEL (
    id CHAR(24) NOT NULL PRIMARY KEY,
    channel_name TEXT NOT NULL, 
    videos INT NOT NULL DEFAULT 1 CHECK (videos >= 0)
);

CREATE TABLE VIDEO (
    media MEDIA NOT NULL, 
    id CHAR(11) NOT NULL PRIMARY KEY, 
    title TEXT NOT NULL, 
    channel CHAR(24) NOT NULL REFERENCES CHANNEL(id), 
    duration INTERVAL NOT NULL, 
    uploaded DATE NOT NULL, 
    downloaded TIMESTAMP DEFAULT NOW(),
    resolution SMALLINT,
    file_path TEXT NOT NULL, 
    CONSTRAINT valid_resolutions CHECK (resolution IN (NULL, 144, 240, 360, 480, 720, 1080, 1440))
);