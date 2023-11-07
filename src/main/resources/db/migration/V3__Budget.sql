CREATE TABLE author
(
    id     SERIAL PRIMARY KEY,
    created_at   TIMESTAMP  NOT NULL,
    full_name  VARCHAR(200)  NOT NULL
);

ALTER TABLE budget
    ADD author_id int REFERENCES author;