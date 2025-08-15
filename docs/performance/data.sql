-- =========================================================
-- 0) 수열 테이블 생성 (TEMPORARY 아님: DataGrip/세션 이슈 회피)
-- =========================================================
DROP TABLE IF EXISTS digits;
CREATE TABLE digits (d TINYINT PRIMARY KEY);
INSERT INTO digits (d) VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9);

DROP TABLE IF EXISTS seq_1m;
CREATE TABLE seq_1m (n INT PRIMARY KEY);
INSERT INTO seq_1m (n)
SELECT  a.d + b.d*10 + c.d*100 + d1.d*1000 + e.d*10000 + f.d*100000 + 1
FROM digits a
         CROSS JOIN digits b
         CROSS JOIN digits c
         CROSS JOIN digits d1
         CROSS JOIN digits e
         CROSS JOIN digits f
ORDER BY 1;

DROP TABLE IF EXISTS seq_50;
CREATE TABLE seq_50 (n INT PRIMARY KEY);
INSERT INTO seq_50 (n)
SELECT n FROM seq_1m LIMIT 50;

-- =========================================================
-- 1) (세션에서 가능한) 대량 적재 옵션만 적용
-- =========================================================
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;

-- =========================================================
-- 2) brand
-- =========================================================
INSERT INTO brand (name, description) VALUES
                                          ('user1', '루프스'),
                                          ('user2', '소마웨어'),
                                          ('user3', '에이블코어'),
                                          ('user4', '하이로닉스');

-- =========================================================
-- 2-1) product.like_count 컬럼 추가 (버전 호환)
-- =========================================================
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'like_count'
);
SET @ddl := IF(@col_exists = 0,
               'ALTER TABLE product ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0',
               'SELECT "like_count already exists"');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================================================
-- 3) product (1,000,000건)
--    brand_id: 1..4 / price: (100..2000)*100
-- =========================================================
INSERT INTO product (brand_id, name, price, created_at, updated_at)
SELECT
    1 + ((n * 2654435761) MOD 4)                                       AS brand_id,
    CONCAT('Product Name ', n)                                          AS name,
    (100 + ((n * 1103515245) MOD 1901)) * 100                           AS price,
    NOW(), NOW()
FROM seq_1m;

-- =========================================================
-- 4) stock (1,000,000건)
--    quantity: 10..500
-- =========================================================
INSERT INTO stock (product_id, quantity)
SELECT
    n                                                                   AS product_id,
    10 + ((n * 1664525) MOD 491)                                        AS quantity
FROM seq_1m;

-- =========================================================
-- 5) user (1,000,000건)
--    user_id: user-0000001 .. user-1000000 (7자리 패딩)
-- =========================================================
INSERT INTO `user` (user_id, name, email, password, phone_number, birth_date, gender)
SELECT
    CONCAT('user-', LPAD(n, 7, '0'))                                    AS user_id,
    CONCAT('User ', n)                                                  AS name,
    CONCAT('user', LPAD(n, 7, '0'), '@example.com')                     AS email,
    MD5(CONCAT('pw-', n))                                               AS password,
    CONCAT('010-',
           LPAD(((n * 1013904223) MOD 10000), 4, '0'), '-',
           LPAD(((n * 8253729)    MOD 10000), 4, '0'))                  AS phone_number,
    DATE_ADD('1970-01-01',
             INTERVAL ((n * 214013 + 2531011)
        MOD DATEDIFF('2006-01-01','1970-01-01')) DAY)                   AS birth_date,
    IF(((n * 1234567) MOD 2) = 0, 'MALE', 'FEMALE')                     AS gender
FROM seq_1m;

-- =========================================================
-- 6) likes
--    각 product의 like_count = 1 + ((p.n * 1103515245) % 50)
--    (유니크 인덱스 존재 시 드롭 → 적재 → 재생성)
-- =========================================================
-- 유니크 인덱스 존재시만 드롭 (버전 호환)
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'likes'
      AND INDEX_NAME = 'uk_likes_user_product'
);
SET @ddl := IF(@idx_exists > 0,
               'ALTER TABLE likes DROP INDEX uk_likes_user_product',
               'SELECT "uk_likes_user_product not exists"');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 제품별 좋아요 개수: 1..50
INSERT INTO likes (product_id, user_id)
SELECT
    p.n AS product_id,
    CONCAT('user-', LPAD( 1 + (((p.n - 1) * 50 + (s.n - 1)) MOD 1000000), 7, '0')) AS user_id
FROM seq_1m p
         JOIN seq_50 s
              ON s.n <= 1 + ((p.n * 1103515245) MOD 50);

-- 유니크 인덱스 없을 때만 생성 (버전 호환)
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'likes'
      AND INDEX_NAME = 'uk_likes_user_product'
);
SET @ddl := IF(@idx_exists = 0,
               'ALTER TABLE likes ADD CONSTRAINT uk_likes_user_product UNIQUE (user_id, product_id)',
               'SELECT "uk_likes_user_product already exists"');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- (옵션) 조회/집계용 보조 인덱스
-- CREATE INDEX idx_likes_product ON likes(product_id);

-- =========================================================
-- 6-1) product.like_count 집계 반영
-- =========================================================
UPDATE product p
    JOIN (
    SELECT product_id, COUNT(*) AS cnt
    FROM likes
    GROUP BY product_id
    ) c ON c.product_id = p.id
    SET p.like_count = c.cnt;

-- 좋아요가 전혀 없는 상품이 있다면 0 유지 (DEFAULT 0로 충분)

-- =========================================================
-- 7) 커밋 & 옵션 원복
-- =========================================================
COMMIT;

SET unique_checks = 1;
SET foreign_key_checks = 1;
