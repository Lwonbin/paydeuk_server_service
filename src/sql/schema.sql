DROP TABLE IF EXISTS EXAMPLE;

CREATE TABLE EXAMPLE (
                         EXAM_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         NAME VARCHAR(255) NOT NULL,
                         CREATED_AT DATETIME(6) NOT NULL,
                         UPDATED_AT DATETIME(6) NOT NULL
);