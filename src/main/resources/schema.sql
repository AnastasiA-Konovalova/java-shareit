CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    start_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(255) DEFAULT 'WAITING',
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user FOREIGN KEY (author_id) REFERENCES users (id)
);

ALTER TABLE comments ADD COLUMN created TIMESTAMP;


INSERT INTO users (email, name)
VALUES ('email1@mail.ru', 'userName1');

INSERT INTO users (email, name)
VALUES ('email2@mail.ru', 'userName2');

INSERT INTO users (email, name)
VALUES ('email3@mail.ru', 'userName3');

INSERT INTO items (name, description, available, owner_id)
VALUES ('itemName1', 'itemDescription1', false,
(SELECT id FROM USERS WHERE id = 1));

INSERT INTO items (name, description, available, owner_id)
VALUES ('itemName2', 'itemDescription2', false,
(SELECT id FROM USERS WHERE id = 2));

INSERT INTO bookings (item_id, booker_id, start_booking, end_booking, status)
VALUES ((SELECT id FROM items WHERE id = 1),
        (SELECT id FROM users WHERE id = 1),
        '2025-04-12 10:00:00',
        '2025-04-13 10:00:00',
        'WAITING');

INSERT INTO comments (text, item_id, author_id, created)
VALUES ('Отличная вещь!', 1, 2, now());
