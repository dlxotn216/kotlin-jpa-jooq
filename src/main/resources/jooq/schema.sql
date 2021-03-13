CREATE TABLE USR_USER
(
    USER_KEY BIGINT       NOT NULL PRIMARY KEY,
    USER_ID  VARCHAR(256) NOT NULL UNIQUE,
    EMAIL    VARCHAR(256) NOT NULL UNIQUE,
    NAME     VARCHAR(512) NOT NULL
);

CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;