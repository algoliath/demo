CREATE TABLE template
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR NOT NULL,
    member_id INTEGER NOT NULL,
    PRIMARY KEY (id)
);
