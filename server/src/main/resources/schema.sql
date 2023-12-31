CREATE TABLE IF NOT EXISTS USERS
(
    ID          BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME        VARCHAR                                              NOT NULL,
    EMAIL       VARCHAR UNIQUE                                       NOT NULL
    );
CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID           BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    DESCRIPTION  VARCHAR                                              NOT NULL,
    REQUEST_ID   BIGINT  REFERENCES USERS (id) ON delete CASCADE,
    CREATED      TIMESTAMP  WITHOUT TIME ZONE                         NOT NULL
);
CREATE TABLE IF NOT EXISTS ITEMS
(
    ID           BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME         VARCHAR                                              NOT NULL,
    DESCRIPTION  VARCHAR                                              NOT NULL,
    IS_AVAILABLE BOOLEAN                                              NOT NULL,
    OWNER_ID     BIGINT  REFERENCES USERS (id),
    REQUEST_ID   BIGINT  REFERENCES REQUESTS (id)
    );
CREATE TABLE IF NOT EXISTS BOOKINGS
(
    ID            BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    START_BOOKING TIMESTAMP                                            NOT NULL,
    END_BOOKING   TIMESTAMP                                            NOT NULL,
    ITEM_ID       BIGINT  REFERENCES ITEMS (id) ON DELETE CASCADE,
    BOOKER_ID     BIGINT  REFERENCES USERS (id) ON DELETE CASCADE,
    STATUS        VARCHAR
    );
CREATE TABLE IF NOT EXISTS COMMENTS
(
    ID            BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    TEXT          VARCHAR                                              NOT NULL,
    ITEM_ID       BIGINT  REFERENCES ITEMS (id) ON DELETE CASCADE,
    AUTHOR_ID     BIGINT  REFERENCES USERS (id) ON DELETE CASCADE,
    CREATED       TIMESTAMP
    );
