CREATE TABLE IF NOT EXISTS users(
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(256) NOT NULL,
    user_email VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS wallets(
    wallet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT not null,
    balance int not null default 0
);

DROP TABLE IF EXISTS transcations;

CREATE TABLE IF NOT EXISTS transactions(
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    send_wallet_id BIGINT NOT NULL,
    receiver_wallet_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(10) NOT NULL,
    date DATE NOT NULL
);
