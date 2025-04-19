BEGIN WORK;
DROP TABLE IF EXISTS Videos;

CREATE TABLE Videos (
    Id              CHAR(11) PRIMARY KEY, 
    Type            CHAR(5) NOT NULL,  
    Resolution      INT NOT NULL,
    Title           VARCHAR(200), 
    Channel         VARCHAR(50) NOT NULL, 
    Uploaded        DATE NOT NULL, 
    Downloaded      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    Path            VARCHAR(200) NOT NULL, 
    CHECK (Resolution IN (144, 240, 360, 480, 720, 1080, 1440)), 
    CHECK (Type IN ('video', 'audio'))
);
COMMIT;