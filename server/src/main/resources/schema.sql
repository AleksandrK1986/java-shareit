drop table if exists bookings cascade;
drop table if exists comments cascade;
drop table if exists items cascade;
drop table if exists requests cascade;
drop table if exists users cascade;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email) );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000),
    requestor_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_requestor_id FOREIGN KEY(requestor_id) REFERENCES users (id) ON DELETE CASCADE );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_owner_id FOREIGN KEY(owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_items_request_id FOREIGN KEY(request_id) REFERENCES requests (id) ON DELETE CASCADE );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(100) NOT NULL,
    CONSTRAINT fk_bookings_item_id FOREIGN KEY(item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY(booker_id) REFERENCES users (id) ON DELETE CASCADE );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_item_id FOREIGN KEY(item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author_id FOREIGN KEY(author_id) REFERENCES users (id) ON DELETE CASCADE );

