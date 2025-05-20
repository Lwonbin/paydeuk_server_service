-- USERS TABLE:
--      ROLE == USER
INSERT INTO users (name, username, password, personal_auth_key, phone, email, pay_password, address, role, birth_date,
                   status, created_at, updated_at)
VALUES ('일반유저', 'user', '$2a$10$B4vWjDxTtmML4Uwx03JVMeRJgrcJC3tVlfPUVILfrAqJfbH2SMIPS', '12345', '010-1234-5678', 'test@test.com',
        NULL, '서울 마포구 월드컵북로 434 상암 IT Tower', 'USER', '1990-01-01', 'active', NOW(),
        NOW());

--      ROLE == ADMIN
INSERT INTO users (name, username, password, personal_auth_key, phone, email, pay_password, address, role, birth_date,
                   status, created_at, updated_at)
VALUES ('관리자', 'admin', '$2a$10$B4vWjDxTtmML4Uwx03JVMeRJgrcJC3tVlfPUVILfrAqJfbH2SMIPS', '1234', '010-0000-0000', 'admin@example.com', NULL, NULL, 'ADMIN',
        '1980-01-01', 'ACTIVE', NOW(), NOW());

-- CARD TABLE
-- card_id == 1
INSERT INTO card (name, type, image_url, annual_fee, company, created_at, updated_at)
VALUES ('현대카드 M', 'credit', 'dummyurl', 30000, 'HYUNDAI', NOW(), NOW()),
('신한카드 Mr.Life', 'credit', 15000, 'SHINHAN' ,NOW(), NOW()),
('KB국민 다담카드', 'credit', 'dummyurl', 15000, 'KOOKMIN', NOW(), NOW()),
('삼성카드 taptap O', 'credit', 'dummyurl', 10000, 'SAMSUNG', NOW(), NOW()),
('카드의 정석 오하CHECK', 'debit', 'dummyurl', 0, 'WOORI', NOW(), NOW());

-- USER_CARD TABLE
INSERT INTO user_card (user_id, card_id, card_token, card_number, is_default_card, created_at, updated_at)
VALUES (1, 1, 'hyundai_m_token', '4017', 1, NOW(), NOW()),
(1, 2, 'shinhan_mrlife_token', '2228', 0, NOW(), NOW()),
(1, 3, 'kookmin_dadam_token', '4009', 0, NOW(), NOW())
(1, 4, 'samsung_taptap_token', '4129', 0, NOW(), NOW())
(1, 5, 'woori_jungsuk_token', '2000', 0, NOW(), NOW());

-- MERCHANT TABLE
INSERT INTO merchant (name, is_active, commission_rate, business_number, manager_name, phone, manager_phone, category,
                      created_at, updated_at)
VALUES ('스타벅스', 1, '10%', '123-45-67890', '김스벅', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()), -- 1
       ('마켓컬리', 1, '10%', '123-45-67890', '이컬리', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()), -- 2
       ('배달의민족', 1, '10%', '123-45-67890', '박배민', '02-1234-5678', '010-8765-4321', 'food_beverage', NOW(), NOW()), -- 3
       ('넷플릭스', 1, '10%', '123-45-67890', '최넷플', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()), -- 4
       ('멜론', 1, '10%', '123-45-67890', '정멜론', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()), -- 5
       ('네이버플러스멤버십', 1, '10%', '123-45-67890', '김네멤', '02-1234-5678', '010-8765-4321', 'subscribe', NOW(), NOW()), -- 6
       ('쿠팡', 1, '10%', '123-45-67890', '이쿠팡', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),  -- 7
       ('무신사', 1, '10%', '123-45-67890', '박신사', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),  -- 8
       ('이마트몰', 1, '10%', '123-45-67890', '최마트', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()),  -- 9
       ('오늘의집', 1, '10%', '123-45-67890', '정오집', '02-1234-5678', '010-8765-4321', 'shopping', NOW(), NOW()), -- 10
       ('CGV', 1, '10%', '123-45-67890', '김지비', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()), -- 11
       ('교보문고', 1, '10%', '123-45-67890', '이교보', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()), -- 12
       ('롯데월드', 1, '10%', '123-45-67890', '박롯데', '02-1234-5678', '010-8765-4321', 'culture', NOW(), NOW()), -- 13
       ('KTX', 1, '10%', '123-45-67890', '최기차', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW()), -- 14
       ('고속버스', 1, '10%', '123-45-67890', '정고속', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW()), -- 15
       ('항공', 1, '10%', '123-45-67890', '정항공', '02-1234-5678', '010-8765-4321', 'transportation', NOW(), NOW()); -- 16

-- BENEFIT TABLE
INSERT INTO benefit (title, description, benefit_type, has_additional_condition, merchant_id, created_at, updated_at)
VALUES ('기본혜택', '국내외 가맹점 1.5% M포인트 적립', 'point', 1, NULL, NOW(), NOW()),          -- 현대카드 M / 전 가맹점 (1)
       ('추가혜택', '컬리 5% M포인트 적립', 'point', 1, 2, NOW(), NOW()),                      -- 현대카드 M / 컬리 (2)
       ('추가혜택', '쿠팡 5% M포인트 적립', 'point', 1, 7, NOW(), NOW()),                      -- 현대카드 M / 쿠팡 (3)
       ('추가혜택', '이마트 5% M포인트 적립', 'point', 1, 9, NOW(), NOW()),                    -- 현대카드 M / 이마트 (4)
       ('TIME 할인 서비스 ', '스타벅스 10% 할인', 'discount', 1,  1, NOW(), NOW()),             -- 신한카드 Mr.Life / 스타벅스 (5)
       ('TIME 할인 서비스 ', '쿠팡 10% 할인', 'discount', 1,  7, NOW(), NOW()),                  -- 신한카드 Mr.Life / 쿠팡 (6)
       ('TIME 할인 서비스 ', '이마트 10% 할인', 'discount', 1,  9, NOW(), NOW()),                 -- 신한카드 Mr.Life / 이마트 (7)
       ('기본 할인 서비스 (공통)', 'CGV 3500원 할인', 'discount', 1, 11, NOW(), NOW()),             -- 국민카드 다담카드 / CGV (8)
       ('기본 할인 서비스 (공통)', '롯데월드 50% 현장 할인', 'discount', 1, 13, NOW(), NOW()),         -- 국민카드 다담카드 / 롯데월드 (9)
       ('선택 할인 서비스 (직장인팩)', '스타벅스 7% 포인트 적립', 'point', 1, 1, NOW(), NOW()),          -- 국민카드 다담카드 / 스타벅스 (10)
       ('선택 할인 서비스 (쇼핑팩)', '쿠팡 5% 포인트 적립', 'point', 1, 7, NOW(), NOW()),              -- 국민카드 다담카드 / 쿠팡 (11)
       ('라이프스타일 패키지 (커피)', '스타벅스 50% 할인', 'discount', 1, 1, NOW(), NOW()),            -- 삼성카드 tap tap O / 스타벅스 (12)
       ('라이프스타일 패키지 (쇼핑)', '쿠팡 7% 할인', 'discount', 1, 7, NOW(), NOW()),           -- 삼성카드 tap tap O / 쿠팡 (13)
       ('CGV, 롯데시네마 할인', 'CGV 5000원 할인', 'discount', 1, 11, NOW(), NOW()),               -- 삼성카드 tap tap O / CGV (14)
       ('[오늘 하루 SHOPPING] 온라인 쇼핑', '무신사 5% 캐시백', 'CASHBACK', 1, 8),                    -- 우리카드 카드의 정석오하CHECK / 무신사 (15)
       ('[오늘 하루 SHOPPING] 온라인 쇼핑', '쿠팡 5% 캐시백', 'CASHBACK', 1, 7),                    -- 우리카드 카드의 정석오하CHECK / 쿠팡 (16)
       ('[오늘 하루 EAT] 카페', '스타벅스 5% 캐시백', 'CASHBACK', 1, 1),                    -- 우리카드 카드의 정석오하CHECK / 스타벅스 (17)
       ('[오늘 하루 SHOPPING] 배달앱', '배달의 민족 5% 캐시백', 'CASHBACK', 1, 3),                    -- 우리카드 카드의 정석오하CHECK / 배달의 민족 (18)
       ('[오늘 하루 SHOPPING] 배달앱', '마켓컬리 5% 캐시백', 'CASHBACK', 1, 2),                    -- 우리카드 카드의 정석오하CHECK / 마켓컬리 (19)
       ('[오늘 하루 PLAY] 디지털구독', '넷플릭스 5% 캐시백', 'CASHBACK', 1, 4);                    -- 우리카드 카드의 정석오하CHECK / 넷플릭스 (20)

-- SPENDING_RANGE TABLE
INSERT INTO spending_range (min_spending, max_spending)
VALUES (500000, NULL),          -- (1) 현대카드 M
       (1000000, NULL),         -- (2) 현대카드 M
       (300000, NULL),          -- (3) 신한카드 Mr.Life
       (300000, 500000),        -- (4) 신한카드 Mr.Life
       (500000, 1000000),       -- (5) 신한카드 Mr.Life
       (1000000, NULL),         -- (6) 신한카드 Mr.Life
       (300000, NULL),          -- (7) 국민카드 다담카드
       (300000, 600000),        -- (8) 국민카드 다담카드
       (600000, NULL),          -- (9) 국민카드 다담카드
       (300000, NULL),          -- (10) 삼성카드 tap tap O
       (200000, NULL);          -- (11) 우리카드 카드의 정석 오하CHECK

-- BENEFIT_CONDITION TABLE
INSERT INTO benefit_condition (benefit_id, spending_range_id, value, category, created_at, updated_at)
VALUES (2, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),    -- 현대카드 M 추가혜택 컬리
       (3, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),    -- 현대카드 M 추가혜택 쿠팡
       (4, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),    -- 현대카드 M 추가혜택 이마트
       (5, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),      -- 신한카드 Mr.Life TIME 할인 스타벅스
       (5, NULL, 1, 'DAILY_LIMIT_COUNT', NOW(), NOW()),             -- 신한카드 Mr.Life TIME 할인 스타벅스
       (5, NULL, 10, 'MONTHLY_LIMIT_COUNT', NOW(), NOW()),          -- 신한카드 Mr.Life TIME 할인 스타벅스
       (5, 4, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 스타벅스
       (5, 5, 20000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 스타벅스
       (5, 6, 30000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 스타벅스
       (6, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),      -- 신한카드 Mr.Life TIME 할인 쿠팡
       (6, NULL, 1, 'DAILY_LIMIT_COUNT', NOW(), NOW()),             -- 신한카드 Mr.Life TIME 할인 쿠팡
       (6, NULL, 10, 'MONTHLY_LIMIT_COUNT', NOW(), NOW()),          -- 신한카드 Mr.Life TIME 할인 쿠팡
       (6, 4, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 쿠팡
       (6, 5, 20000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 쿠팡
       (6, 6, 30000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 쿠팡
       (7, NULL, 5000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),      -- 신한카드 Mr.Life TIME 할인 이마트
       (7, NULL, 1, 'DAILY_LIMIT_COUNT', NOW(), NOW()),             -- 신한카드 Mr.Life TIME 할인 이마트
       (7, 4, 3000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),        -- 신한카드 Mr.Life TIME 할인 이마트
       (7, 5, 5000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),        -- 신한카드 Mr.Life TIME 할인 이마트
       (7, 6, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 신한카드 Mr.Life TIME 할인 이마트
       (8, NULL, 2, 'DAILY_LIMIT_COUNT', NOW(), NOW()),             -- 국민카드 다담카드 기본 할인 서비스 (공통) CGV
       (8, NULL, 4, 'MONTHLY_LIMIT_COUNT', NOW(), NOW()),           -- 국민카드 다담카드 기본 할인 서비스 (공통) CGV
       (9, NULL, 1, 'DAILY_LIMIT_COUNT', NOW(), NOW()),             -- 국민카드 다담카드 기본 할인 서비스 (공통) 롯데월드
       (9, NULL, 2, 'MONTHLY_LIMIT_COUNT', NOW(), NOW()),           -- 국민카드 다담카드 기본 할인 서비스 (공통) 롯데월드
       (10, 8, 7000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 국민카드 다담카드 선택 할인 서비스 (직장인팩) 스타벅스
       (10, 9, 14000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),      -- 국민카드 다담카드 선택 할인 서비스 (직장인팩) 스타벅스
       (11, 8, 5000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),       -- 국민카드 다담카드 선택 할인 서비스 (쇼핑팩) 쿠팡
       (11, 9, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),      -- 국민카드 다담카드 선택 할인 서비스 (쇼핑팩) 쿠팡
       (12, NULL, 10000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),   -- 삼성카드 tap tap O 라이프스타일 패키지 (커피) 스타벅스
       (13, NULL, 5000, 'MONTHLY_DISCOUNT_LIMIT', NOW(), NOW()),    -- 삼성카드 tap tap O 라이프스타일 패키지 (쇼핑) 쿠팡
       (14, NULL, 1, 'DAILY_LIMIT_COUNT', NOW(), NOW()),            -- 삼성카드 tap tap O CGV, 롯데시네마 할인
       (14, NULL, 2, 'MONTHLY_LIMIT_COUNT', NOW(), NOW()),          -- 삼성카드 tap tap O CGV, 롯데시네마 할인
       (15, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),     -- 우리카드 카드의정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 무신사
       (16, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),     -- 우리카드 카드의정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 쿠팡
       (17, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),     -- 우리카드 카드의정석 오하CHECK [오늘 하루 EAT] 카페 스터벅스
       (18, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),     -- 우리카드 카드의정석 오하CHECK [오늘 하루 EAT] 배달앱 배달의 민족
       (19, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW()),     -- 우리카드 카드의정석 오하CHECK [오늘 하루 EAT] 배달앱 마켓컬리
       (20, NULL, 1000, 'PER_TRANSACTION_LIMIT', NOW(), NOW());     -- 우리카드 카드의정석 오하CHECK [오늘 하루 PLAY] OTT 넷플릭스

-- DISCOUNT TABLE
INSERT INTO discount (benefit_id, spending_range_id, apply_type, amount, created_at, updated_at)
VALUES (1, 1, 'rate', 1.5, NOW(), NOW()),       -- 현대카드 M 기본혜택 전가맹점
       (2, 2, 'rate', 5, NOW(), NOW()),         -- 현대카드 M 추가혜택 컬리
       (3, 2, 'rate', 5, NOW(), NOW()),         -- 현대카드 M 추가혜택 쿠팡
       (4, 2, 'rate', 5, NOW(), NOW()),         -- 현대카드 M 추가혜택 이마트
       (5, 3, 'rate', 10, NOW(), NOW()),        -- 신한카드 Mr.Life TIME 할인 스타벅스
       (6, 3, 'rate', 10, NOW(), NOW()),        -- 신한카드 Mr.Life TIME 할인 쿠팡
       (7, 3, 'rate', 10, NOW(), NOW()),        -- 신한카드 Mr.Life TIME 할인 이마트
       (8, 7, 'amount', 3500, NOW(), NOW()),    -- 국민카드 다담카드 CGV
       (9, 7, 'rate', 50, NOW(), NOW()),        -- 국민카드 다담카드 롯데월드
       (10, 7, 'rate', 7, NOW(), NOW()),        -- 국민카드 다담카드 스타벅스
       (11, 7, 'rate', 5, NOW(), NOW()),        -- 국민카드 다담카드 쿠팡
       (12, 10, 'rate', 50, NOW(), NOW()),      -- 삼성카드 tap tap O 라이프스타일 패키지 (커피) 스타벅스
       (13, 10, 'rate', 7, NOW(), NOW()),       -- 삼성카드 tap tap O  라이프스타일 패키지 (쇼핑) 쿠팡
       (14, 10, 'amount', 5000, NOW(), NOW()),  -- 삼성카드 tap tap O CGV, 롯데시네마 할인 CGV
       (15, 11, 'rate', 5, NOW(), NOW()),       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 무신사
       (15, 11, 'rate', 5, NOW(), NOW()),       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 쿠팡
       (15, 11, 'rate', 5, NOW(), NOW()),       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 카페 스타벅스
       (15, 11, 'rate', 5, NOW(), NOW()),       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 배달앱 배달의 민족
       (15, 11, 'rate', 5, NOW(), NOW()),       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 배달앱 마켓컬리
       (15, 11, 'rate', 5, NOW(), NOW());       -- 우리카드 카드의 정석 오하CHECK [오늘 하루 PLAY] OTT 넷플릭스

-- CARD_BENEFIT TABLE
INSERT INTO card_benefit (card_id, benefit_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()),    -- 현대카드 M - 기본 혜택
       (1, 2, NOW(), NOW()),    -- 현대카드 M - 추가혜택 (컬리)
       (1, 3, NOW(), NOW()),    -- 현대카드 M - 추가혜택 (쿠팡)
       (1, 4, NOW(), NOW()),    -- 현대카드 M - 추가혜택 (이마트)
       (2, 5, NOW(), NOW()),    -- 신한카드 Mr.Life - TIME 할인 (스타벅스)
       (2, 6, NOW(), NOW()),    -- 신한카드 Mr.Life - TIME 할인 (쿠팡)
       (2, 7, NOW(), NOW()),    -- 신한카드 Mr.Life - TIME 할인 (이마트)
       (3, 8, NOW(), NOW()),    -- 국민카드 다담카드 - 기본 할인 서비스 (공통) CGV
       (3, 9, NOW(), NOW()),    -- 국민카드 다담카드 - 기본 할인 서비스 (공통) 롯데월드
       (3, 10, NOW(), NOW()),   -- 국민카드 다담카드 - 선택 할인 서비스 (직장인팩) 스타벅스
       (3, 11, NOW(), NOW()),   -- 국민카드 다담카드 - 선택 할인 서비스 (쇼핑팩) 쿠팡
       (4, 12, NOW(), NOW()),   -- 삼성카드 tap tap O - 라이프스타일 패키지 (커피) 스타벅스
       (4, 13, NOW(), NOW()),   -- 삼성카드 tap tap O - 라이프스타일 패키지 (쇼핑) 쿠팡
       (4, 14, NOW(), NOW()),   -- 삼성카드 tap tap O - CGV,롯데시네마 할인 CGV
       (5, 15, NOW(), NOW()),   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 무신사
       (5, 16, NOW(), NOW()),   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 SHOPPING] 온라인 쇼핑 쿠팡
       (5, 17, NOW(), NOW()),   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 카페 스타벅스
       (5, 18, NOW(), NOW()),   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 배달앱 배달의 민족
       (5, 19, NOW(), NOW()),   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 EAT] 배달앱 마켓컬리
       (5, 20, NOW(), NOW());   -- 우리카드 카드의 정석 오하CHECK [오늘 하루 PLAY] OTT 넷플릭스



-- PAYMENT TABLE
INSERT INTO payment (product_name, amount, payment_success, user_card_id, merchant_id, card_benefit_id, discount_amount, created_at, updated_at)
VALUES 
    ('스타벅스 아메리카노', 4500, true, 1, 1, 1, 450, NOW(), NOW()),
    ('스타벅스 카페라떼', 5000, true, 1, 1, 1, 500, NOW(), NOW()),
    ('컬리 생필품', 25000, true, 1, 2, 2, 1250, NOW(), NOW()),
    ('쿠팡 전자제품', 150000, true, 1, 7, 3, 7500, NOW(), NOW()),
    ('이마트 식료품', 50000, true, 1, 9, 4, 2500, NOW(), NOW()),
    ('스타벅스 케이크', 6000, false, 2, 1, 1, 0, NOW(), NOW()),
    ('컬리 신선식품', 35000, true, 2, 2, 2, 1750, NOW(), NOW()),
    ('쿠팡 의류', 80000, true, 2, 7, 3, 4000, NOW(), NOW()),
    ('이마트 가전제품', 200000, true, 2, 9, 4, 10000, NOW(), NOW()),
    ('스타벅스 디저트', 7000, true, 2, 1, 1, 700, NOW(), NOW());


