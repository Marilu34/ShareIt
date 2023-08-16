create TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    email VARCHAR(256) NOT NULL,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT EMAIL_UNIQUE UNIQUE(email)
);

create TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    available BOOLEAN NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id)
);

create TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    stat_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(10) DEFAULT 'WAITING',
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

create TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    text VARCHAR(2024) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

create TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    description VARCHAR(2024) NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id)
);

alter table items add CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id);