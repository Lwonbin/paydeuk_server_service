-- USERS TABLE:
--      ROLE == USER
INSERT INTO users (name, username, password, personal_auth_key, phone, email, pay_password, address, role, birth_date,
                   status, created_at, updated_at)
VALUES ('일반유저', 'user', '$2a$10$rRykd2BvvRKuyPcZmytSgOz07Aoqtjrz8jkiOnJSd21DjBFx12tLy', '12345', '010-1234-5678', 'test@test.com',
        NULL, '서울 마포구 월드컵북로 434 상암 IT Tower', 'USER', '1990-01-01', 'active', NOW(),
        NOW());

--      ROLE == ADMIN
INSERT INTO users (name, username, password, personal_auth_key, phone, email, pay_password, address, role, birth_date,
                   status, created_at, updated_at)
VALUES ('관리자', 'admin', '$2a$10$rRykd2BvvRKuyPcZmytSgOz07Aoqtjrz8jkiOnJSd21DjBFx12tLy', '1234', '010-0000-0000', 'admin@example.com', NULL, NULL, 'ADMIN',
        '1980-01-01', 'ACTIVE', NOW(), NOW());

-- CARD TABLE
INSERT INTO card (name, type, image_url, annual_fee, company, created_at, updated_at)
-- card_id == 1
VALUES ('현대카드 M', 'credit', 'dummyurl', 30000, 'hyundai', NOW(), NOW());

-- USER_CARD TABLE
INSERT INTO user_card (user_id, card_id, card_token, card_number, is_default_card, created_at, updated_at)
VALUES (1, 1, 'mock_token', '1234', 1, NOW(), NOW());

-- MERCHANT TABLE
INSERT INTO merchant (name, is_active, commission_rate, business_number, manager_name, phone, manager_phone, category,
                      created_at, updated_at)
VALUES ('스타벅스', 1, '10%', '123-45-67890', '김스벅', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()),
       ('마켓컬리', 1, '10%', '123-45-67890', '이컬리', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()),
       ('배달의민족', 1, '10%', '123-45-67890', '박배민', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()),
       ('넷플릭스', 1, '10%', '123-45-67890', '최넷플', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()),
       ('멜론', 1, '10%', '123-45-67890', '정멜론', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()),
       ('네이버플러스멤버십', 1, '10%', '123-45-67890', '김네멤', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()),
       ('쿠팡', 1, '10%', '123-45-67890', '이쿠팡', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),
       ('무신사', 1, '10%', '123-45-67890', '박신사', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),
       ('이마트몰', 1, '10%', '123-45-67890', '최마트', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),
       ('오늘의집', 1, '10%', '123-45-67890', '정오집', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),
       ('CGV', 1, '10%', '123-45-67890', '김지비', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()),
       ('교보문고', 1, '10%', '123-45-67890', '이교보', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()),
       ('롯데월드', 1, '10%', '123-45-67890', '박롯데', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()),
       ('KTX', 1, '10%', '123-45-67890', '최기차', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW()),
       ('고속버스', 1, '10%', '123-45-67890', '정고속', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW()),
       ('항공', 1, '10%', '123-45-67890', '정항공', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW());

-- BENEFIT TABLE
INSERT INTO benefit (title, description, benefit_type, has_additional_condition, merchant_id, created_at, updated_at)
VALUES ('기본혜택', '국내외 가맹점 1.5% M포인트 적립', 'point', 1, NULL, NOW(), NOW()),
       ('추가혜택', '컬리 5% M포인트 적립', 'point', 1, 2, NOW(), NOW()), -- 컬리
       ('추가혜택', '쿠팡 5% M포인트 적립', 'point', 1, 7, NOW(), NOW()), -- 쿠팡
       ('추가혜택', '이마트 5% M포인트 적립', 'point', 1, 9, NOW(), NOW());
-- 이마트


-- SPENDING_RANGE TABLE
INSERT INTO spending_range (min_spending, max_spending)
VALUES (500000, NULL),
       (1000000, NULL);

-- BENEFIT_CONDITION TABLE
INSERT INTO benefit_condition (benefit_id, spending_range_id, value, category, created_at, updated_at)
VALUES (2, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),
       (3, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),
       (4, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW());


-- DISCOUNT TABLE
INSERT INTO discount (benefit_id, spending_range_id, apply_type, amount, created_at, updated_at)
VALUES (1, 1, 'rate', 1.5, NOW(), NOW()),
       (1, 2, 'rate', 5, NOW(), NOW());

-- CARD_BENEFIT TABLE
INSERT INTO card_benefit (card_id, benefit_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()),
       (1, 2, NOW(), NOW()),
       (1, 3, NOW(), NOW()),
       (1, 4, NOW(), NOW());


