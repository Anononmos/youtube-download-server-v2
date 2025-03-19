BEGIN;
CREATE TABLE Videos (
    Id              CHAR(11) PRIMARY KEY, 
    Type            BOOL, 
    Title           VARCHAR(200), 
    Channel         VARCHAR(50) NOT NULL, 
    Uploaded        DATE NOT NULL, 
    Downloaded      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    Path            VARCHAR(200) NOT NULL
);
COMMIT;