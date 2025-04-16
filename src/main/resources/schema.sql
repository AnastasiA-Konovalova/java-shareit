CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id serial PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id serial PRIMARY KEY,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    start_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(255) DEFAULT 'WAITING',
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id serial PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP,
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user FOREIGN KEY (author_id) REFERENCES users (id)
);
