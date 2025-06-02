DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS user_card;
DROP TABLE IF EXISTS card_benefit;
DROP TABLE IF EXISTS discount;
DROP TABLE IF EXISTS benefit_condition;
DROP TABLE IF EXISTS benefit;
DROP TABLE IF EXISTS merchant;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS spending_range;

CREATE TABLE spending_range (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                min_spending BIGINT,
                                max_spending BIGINT
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                       name VARCHAR(10) NOT NULL,
                       username VARCHAR(20) NOT NULL,
                       password VARCHAR(60) NOT NULL,
                       personal_auth_key VARCHAR(100) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       email VARCHAR(30),
                       payment_pin_code VARCHAR(60),
                       image_url VARCHAR(255),
                       address VARCHAR(30),
                       role VARCHAR(10) NOT NULL,
                       birth_date VARCHAR(10) NOT NULL,
                       status VARCHAR(10) NOT NULL,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);

CREATE TABLE card (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      name VARCHAR(30) NOT NULL,
                      type VARCHAR(10) NOT NULL,
                      image_url VARCHAR(200) NOT NULL,
                      annual_fee BIGINT NOT NULL,
                      company VARCHAR(20) NOT NULL,
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP
);

CREATE TABLE merchant (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                          name VARCHAR(20) NOT NULL,
                          is_active BOOLEAN NOT NULL,
                          commission_rate VARCHAR(10) NOT NULL,
                          business_number VARCHAR(20) NOT NULL,
                          manager_name VARCHAR(20) NOT NULL,
                          phone VARCHAR(20) NOT NULL,
                          manager_phone VARCHAR(20) NOT NULL,
                          category VARCHAR(20) NOT NULL,
                          is_deleted BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE benefit (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                         description VARCHAR(100),
                         title VARCHAR(100),
                         benefit_type VARCHAR(20) NOT NULL,
                         has_additional_condition BOOLEAN NOT NULL,
                         merchant_id BIGINT,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);

CREATE TABLE benefit_condition (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                   benefit_id BIGINT NOT NULL,
                                   spending_range_id BIGINT,
                                   "value" BIGINT,
                                   category VARCHAR(30) NOT NULL,
                                   created_at TIMESTAMP,
                                   updated_at TIMESTAMP,
                                   FOREIGN KEY (benefit_id) REFERENCES benefit (id),
                                   FOREIGN KEY (spending_range_id) REFERENCES spending_range (id)
);


CREATE TABLE discount (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                          benefit_id BIGINT NOT NULL,
                          spending_range_id BIGINT NOT NULL,
                          apply_type VARCHAR(10) NOT NULL,
                          amount FLOAT NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (benefit_id) REFERENCES benefit (id),
                          FOREIGN KEY (spending_range_id) REFERENCES spending_range (id)
);

CREATE TABLE card_benefit (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                              card_id BIGINT NOT NULL,
                              benefit_id BIGINT NOT NULL,
                              created_at TIMESTAMP,
                              updated_at TIMESTAMP,
                              FOREIGN KEY (card_id) REFERENCES card (id),
                              FOREIGN KEY (benefit_id) REFERENCES benefit (id)
);

CREATE TABLE user_card (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                           user_id BIGINT NOT NULL,
                           card_id BIGINT NOT NULL,
                           card_token VARCHAR(100) NOT NULL,
                           card_number VARCHAR(40) NOT NULL,
                           is_default_card BOOLEAN NOT NULL,
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users (id),
                           FOREIGN KEY (card_id) REFERENCES card (id)
);

CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                         product_name VARCHAR(30) NOT NULL,
                         amount INT NOT NULL,
                         payment_success BOOLEAN NOT NULL,
                         user_card_id BIGINT NOT NULL,
                         merchant_id BIGINT,
                         card_benefit_id BIGINT NOT NULL,
                         discount_amount INT NOT NULL,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         FOREIGN KEY (user_card_id) REFERENCES user_card (id),
                         FOREIGN KEY (merchant_id) REFERENCES merchant (id),
                         FOREIGN KEY (card_benefit_id) REFERENCES card_benefit (id)
);
