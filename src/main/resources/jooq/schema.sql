CREATE TABLE USR_USER
(
    USER_KEY    BIGINT        NOT NULL PRIMARY KEY,
    USER_ID     VARCHAR(256)  NOT NULL UNIQUE,
    EMAIL       VARCHAR(256)  NOT NULL UNIQUE,
    NAME        VARCHAR(512)  NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);
CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE STD_STUDY
(
    STUDY_KEY   BIGINT        NOT NULL PRIMARY KEY,
    STUDY_ID    VARCHAR(256)  NOT NULL UNIQUE,
    NAME        VARCHAR(512)  NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);
CREATE SEQUENCE STUDY_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE STD_USER
(
    USER_KEY    BIGINT        NOT NULL,
    STUDY_KEY   BIGINT        NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);
ALTER TABLE STD_USER
    ADD PRIMARY KEY (USER_KEY, STUDY_KEY);

CREATE TABLE REVINFO
(
    REV      BIGINT    NOT NULL,
    REVTSTMP BIGINT NOT NULL
);
CREATE SEQUENCE REV_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE MST_ROLE
(
    ROLE_KEY    BIGINT        NOT NULL PRIMARY KEY,
    ROLE_ID     VARCHAR(256)  NOT NULL UNIQUE,
    NAME        VARCHAR(512)  NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);
CREATE SEQUENCE ROLE_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE MST_ROLE_HIS
(
    ROLE_KEY    BIGINT        NOT NULL PRIMARY KEY,
    REV         BIGINT        NOT NULL,
    REVTYPE     BIGINT        NOT NULL,
    ROLE_ID     VARCHAR(256)  NOT NULL UNIQUE,
    NAME        VARCHAR(512)  NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);

CREATE TABLE USR_ROLE
(
    USER_KEY    BIGINT        NOT NULL,
    ROLE_KEY    BIGINT        NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);

ALTER TABLE USR_ROLE
    ADD PRIMARY KEY (USER_KEY, ROLE_KEY);

CREATE TABLE USR_ROLE_HIS
(
    USER_KEY    BIGINT        NOT NULL,
    ROLE_KEY    BIGINT        NOT NULL,
    REV         BIGINT        NOT NULL,
    REVTYPE     BIGINT        NOT NULL,
    DELETED     BOOLEAN       NOT NULL,
    REASON      VARCHAR(1000) NOT NULL,
    CREATED_BY  BIGINT        NOT NULL,
    CREATED_AT  TIMESTAMP     NOT NULL,
    MODIFIED_BY BIGINT        NOT NULL,
    MODIFIED_AT TIMESTAMP     NOT NULL
);

ALTER TABLE USR_ROLE_HIS
    ADD PRIMARY KEY (USER_KEY, ROLE_KEY, MODIFIED_AT);