CREATE TABLE merchant_category
(
    id            BIGINT PRIMARY KEY,
    category_name VARCHAR(255)
);

CREATE TABLE benefit
(
    id                       BIGINT PRIMARY KEY,
    description              VARCHAR(100),
    title                    VARCHAR(100),
    type                     ENUM ('discount', 'point', 'cashback'),
    spending_range_id        BIGINT,
    has_additional_condition BOOLEAN,
    created_at               TIMESTAMP,
    updated_at               TIMESTAMP
);

CREATE TABLE merchant
(
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(20),
    benefit_id      BIGINT,
    category_id     BIGINT,
    is_active       BOOLEAN,
    commission_rate VARCHAR(255),
    business_number VARCHAR(255),
    manager_name    VARCHAR(255),
    phone           VARCHAR(255),
    manager_phone   VARCHAR(255),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    FOREIGN KEY (benefit_id) REFERENCES benefit (id),
    FOREIGN KEY (category_id) REFERENCES merchant_category (id)
);

CREATE TABLE card_company
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE card
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(30),
    type       ENUM ('credit', 'debit'),
    image_url  VARCHAR(255),
    annual_fee BIGINT,
    company_id BIGINT,
    FOREIGN KEY (company_id) REFERENCES card_company (id)
);

CREATE TABLE users
(
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(10),
    phone           VARCHAR(20),
    password        BIGINT,
    email           VARCHAR(30),
    pay_password    VARCHAR(6),
    address         VARCHAR(30),
    role            ENUM ('admin', 'user'),
    birth_date      TIMESTAMP,
    default_card_id BIGINT,
    status          ENUM ('active', 'inactive', 'temporary'),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE user_card
(
    id          BIGINT PRIMARY KEY,
    user_id     BIGINT,
    card_id     BIGINT,
    card_token  VARCHAR(30),
    card_number INT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (card_id) REFERENCES card (id)
);

CREATE TABLE spending_range
(
    id           BIGINT PRIMARY KEY,
    min_spending BIGINT,
    max_spending BIGINT
);

CREATE TABLE condition_category
(
    id   BIGINT PRIMARY KEY,
    type ENUM ('per_transaction_limit', 'daily_limit_count', 'monthly_limit_count', 'daily_discount_limit', 'monthly_discount_limit')
);

CREATE TABLE condition_rule
(
    id                BIGINT PRIMARY KEY,
    benefit_id        BIGINT,
    category_id       BIGINT,
    spending_range_id BIGINT,
    value             BIGINT,
    FOREIGN KEY (benefit_id) REFERENCES benefit (id),
    FOREIGN KEY (category_id) REFERENCES condition_category (id),
    FOREIGN KEY (spending_range_id) REFERENCES spending_range (id)
);

CREATE TABLE discount
(
    id                BIGINT PRIMARY KEY,
    benefit_id        BIGINT,
    spending_range_id BIGINT,
    apply_type        ENUM ('rate', 'amount'),
    amount            BIGINT,
    FOREIGN KEY (benefit_id) REFERENCES benefit (id),
    FOREIGN KEY (spending_range_id) REFERENCES spending_range (id)
);

CREATE TABLE card_benefit
(
    id         BIGINT PRIMARY KEY,
    card_id    BIGINT,
    benefit_id BIGINT,
    FOREIGN KEY (card_id) REFERENCES card (id),
    FOREIGN KEY (benefit_id) REFERENCES benefit (id)
);

CREATE TABLE applied_benefit
(
    id              BIGINT PRIMARY KEY,
    card_benefit_id BIGINT,
    applied_amount  INT,
    FOREIGN KEY (card_benefit_id) REFERENCES card_benefit (id)
);

CREATE TABLE payment
(
    id                 BIGINT PRIMARY KEY,
    product_name       VARCHAR(30),
    amount             INT,
    status             BOOLEAN,
    card_id            BIGINT,
    merchant_id        BIGINT,
    applied_benefit_id BIGINT,
    FOREIGN KEY (card_id) REFERENCES card (id),
    FOREIGN KEY (merchant_id) REFERENCES merchant (id),
    FOREIGN KEY (applied_benefit_id) REFERENCES applied_benefit (id)
);

CREATE TABLE notification
(
    id               BIGINT PRIMARY KEY,
    user_id          BIGINT,
    email_notify     BOOLEAN,
    sms_notify       BOOLEAN,
    marketing_notify BOOLEAN,
    security_notify  BOOLEAN,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE announcement
(
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT,
    title      VARCHAR(30),
    content    VARCHAR(300),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER TABLE user_card
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id);