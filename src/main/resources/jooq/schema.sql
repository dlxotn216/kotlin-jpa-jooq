CREATE TABLE USR_USER
(
    USER_KEY BIGINT       NOT NULL PRIMARY KEY,
    USER_ID  VARCHAR(256) NOT NULL UNIQUE,
    EMAIL    VARCHAR(256) NOT NULL UNIQUE,
    NAME     VARCHAR(512) NOT NULL
);
CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE STD_STUDY
(
    STUDY_KEY BIGINT       NOT NULL PRIMARY KEY,
    STUDY_ID  VARCHAR(256) NOT NULL UNIQUE,
    NAME      VARCHAR(512) NOT NULL
);
CREATE SEQUENCE STUDY_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE STD_USER
(
    USER_KEY  BIGINT NOT NULL,
    STUDY_KEY BIGINT NOT NULL
);
ALTER TABLE STD_USER ADD PRIMARY KEY (USER_KEY, STUDY_KEY);