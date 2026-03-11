use cs_2025_34;
#必须有一个学号为249050433的学生信息！因为有表关联！

create table stu_info
(
    stu_no        int         not null comment '学号'
        primary key,
    stu_name      varchar(10) null comment '学生姓名',
    stu_pwd       varchar(16) null comment '密码',
    stu_telephone varchar(11) null comment '学生手机号',
    stu_status    int         null comment '1默认在校学生，0默认退休或其他原因'
);

-- 1. 车站信息表
CREATE TABLE station_info
(
    station_id     INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    station_name   VARCHAR(50) NOT NULL,
    station_code   VARCHAR(10) NOT NULL,
    city_name      VARCHAR(50),
    station_type   VARCHAR(20),
    station_status INT DEFAULT 1,
    UNIQUE KEY idx_station_code (station_code)
);
INSERT INTO station_info (station_name, station_code, city_name, station_type, station_status)
VALUES ('北京南', 'VNP', '北京', '高铁站', 1),
       ('上海虹桥', 'AOH', '上海', '高铁站', 1),
       ('杭州东', 'HGH', '杭州', '高铁站', 1),
       ('南京南', 'NKH', '南京', '高铁站', 1),
       ('广州南', 'IZQ', '广州', '高铁站', 1);

-- 2. 高铁车次表
CREATE TABLE train_info
(
    train_id         VARCHAR(20) NOT NULL PRIMARY KEY,
    train_number     VARCHAR(10) NOT NULL,
    train_type       VARCHAR(20),
    start_station_id INT,
    end_station_id   INT,
    departure_time   TIME,
    arrival_time     TIME,
    run_time         VARCHAR(20),
    train_status     INT DEFAULT 1,
    FOREIGN KEY (start_station_id) REFERENCES station_info (station_id),
    FOREIGN KEY (end_station_id) REFERENCES station_info (station_id)
);
INSERT INTO train_info
(train_id, train_number, train_type, start_station_id, end_station_id, departure_time, arrival_time, run_time,
 train_status)
VALUES ('G1001', 'G1', '复兴号', 1, 2, '08:00:00', '12:30:00', '4小时30分', 1),
       ('G1002', 'G2', '复兴号', 2, 1, '14:00:00', '18:30:00', '4小时30分', 1),
       ('G2001', 'G31', '和谐号', 1, 3, '09:00:00', '13:00:00', '4小时', 1);

-- 3. 车次停靠站表
CREATE TABLE train_stop_info
(
    stop_id        INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    train_id       VARCHAR(20),
    station_id     INT,
    stop_sequence  INT,
    arrival_time   TIME,
    departure_time TIME,
    stop_duration  VARCHAR(10),
    stop_status    INT DEFAULT 1,
    FOREIGN KEY (train_id) REFERENCES train_info (train_id),
    FOREIGN KEY (station_id) REFERENCES station_info (station_id)
);
INSERT INTO train_stop_info (train_id, station_id, stop_sequence, arrival_time, departure_time, stop_duration,
                             stop_status)
VALUES ('G1001', 1, 1, '07:55:00', '08:00:00', '5分钟', 1),
       ('G1001', 4, 2, '10:30:00', '10:35:00', '5分钟', 1),
       ('G1001', 2, 3, '12:25:00', '12:30:00', '5分钟', 1),
       ('G2001', 1, 1, '08:55:00', '09:00:00', '5分钟', 1),
       ('G2001', 3, 2, '12:55:00', '13:00:00', '5分钟', 1);

-- 4. 车厢信息表（支持选座功能）
CREATE TABLE carriage_info
(
    carriage_id     INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    train_id        VARCHAR(20),
    carriage_number VARCHAR(10),
    carriage_type   VARCHAR(20),  -- 一等座、二等座、商务座
    seat_count      INT,
    seat_layout     VARCHAR(200), -- 座位布局JSON
    carriage_status INT DEFAULT 1,
    FOREIGN KEY (train_id) REFERENCES train_info (train_id)
);
INSERT INTO carriage_info (train_id, carriage_number, carriage_type, seat_count, seat_layout, carriage_status)
VALUES ('G1001', '01', '商务座', 10, '{"rows": 5, "columns": 2, "aisle": [2]}', 1),
       ('G1001', '02', '一等座', 20, '{"rows": 5, "columns": 4, "aisle": [2]}', 1),
       ('G1001', '03', '二等座', 30, '{"rows": 10, "columns": 5, "aisle": [2, 4]}', 1);

-- 5. 座位信息表
CREATE TABLE seat_info
(
    seat_id          INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    carriage_id      INT,
    seat_number      VARCHAR(10),
    seat_row         INT,
    seat_col         INT,
    seat_type        VARCHAR(20),   -- 靠窗、过道、中间
    seat_status      INT DEFAULT 0, -- 0:空, 1:已售, 2:锁定
    lock_time        DATETIME,
    seat_price       DECIMAL(10, 2),
    seat_status_desc VARCHAR(20),
    FOREIGN KEY (carriage_id) REFERENCES carriage_info (carriage_id)
);

-- 插入座位数据示例（用存储过程批量生成）
DELIMITER $$
CREATE PROCEDURE generate_seats_for_carriage()
BEGIN
    DECLARE v_carriage_id INT;
    DECLARE v_rows INT;
    DECLARE v_cols INT;
    DECLARE v_row INT DEFAULT 1;
    DECLARE v_col INT DEFAULT 1;
    DECLARE v_seat_number VARCHAR(10);
    DECLARE v_seat_type VARCHAR(20);

    -- 为01车厢（商务座）生成座位
    SET v_carriage_id = 1;
    SET v_rows = 5;
    SET v_cols = 2;

    WHILE v_row <= v_rows
        DO
            SET v_col = 1;
            WHILE v_col <= v_cols
                DO
                    SET v_seat_number = CONCAT(v_row,
                                               CASE v_col
                                                   WHEN 1 THEN 'A'
                                                   WHEN 2 THEN 'C'
                                                   END);

                    SET v_seat_type = CASE
                                          WHEN v_col = 1 THEN '靠窗'
                                          WHEN v_col = 2 THEN '过道'
                        END;

                    INSERT INTO seat_info
                    (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
                    VALUES (v_carriage_id, v_seat_number, v_row, v_col, v_seat_type, 0, 300.00);

                    SET v_col = v_col + 1;
                END WHILE;
            SET v_row = v_row + 1;
        END WHILE;

-- 为02车厢（一等座）生成座位
    SET v_carriage_id = 2;
    SET v_rows = 5;
    SET v_cols = 4;
    SET v_row = 1;

    WHILE v_row <= v_rows
        DO
            SET v_col = 1;
            WHILE v_col <= v_cols
                DO
                    SET v_seat_number = CONCAT(v_row,
                                               CASE v_col
                                                   WHEN 1 THEN 'A'
                                                   WHEN 2 THEN 'C'
                                                   WHEN 3 THEN 'D'
                                                   WHEN 4 THEN 'F'
                                                   END);

                    SET v_seat_type = CASE
                                          WHEN v_col IN (1, 4) THEN '靠窗'
                                          WHEN v_col IN (2, 3) THEN '过道'
                        END;

                    INSERT INTO seat_info
                    (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
                    VALUES (v_carriage_id, v_seat_number, v_row, v_col, v_seat_type, 0, 200.00);

                    SET v_col = v_col + 1;
                END WHILE;
            SET v_row = v_row + 1;
        END WHILE;

-- 为03车厢（二等座）生成座位
    SET v_carriage_id = 3;
    SET v_rows = 10;
    SET v_cols = 5;
    SET v_row = 1;

    WHILE v_row <= v_rows
        DO
            SET v_col = 1;
            WHILE v_col <= v_cols
                DO
                    SET v_seat_number = CONCAT(v_row,
                                               CASE v_col
                                                   WHEN 1 THEN 'A'
                                                   WHEN 2 THEN 'B'
                                                   WHEN 3 THEN 'C'
                                                   WHEN 4 THEN 'D'
                                                   WHEN 5 THEN 'F'
                                                   END);

                    SET v_seat_type = CASE
                                          WHEN v_col IN (1, 5) THEN '靠窗'
                                          WHEN v_col IN (2, 4) THEN '中间'
                                          WHEN v_col = 3 THEN '过道'
                        END;

                    INSERT INTO seat_info
                    (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
                    VALUES (v_carriage_id, v_seat_number, v_row, v_col, v_seat_type, 0, 100.00);

                    SET v_col = v_col + 1;
                END WHILE;
            SET v_row = v_row + 1;
        END WHILE;
END$$
DELIMITER ;

CALL generate_seats_for_carriage();
DROP PROCEDURE generate_seats_for_carriage;

-- 6. 票价表
CREATE TABLE ticket_price_info
(
    price_id         INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    train_id         VARCHAR(20),
    start_station_id INT,
    end_station_id   INT,
    carriage_type    VARCHAR(20),
    ticket_price     DECIMAL(10, 2),
    price_status     INT DEFAULT 1,
    FOREIGN KEY (train_id) REFERENCES train_info (train_id),
    FOREIGN KEY (start_station_id) REFERENCES station_info (station_id),
    FOREIGN KEY (end_station_id) REFERENCES station_info (station_id)
);
INSERT INTO ticket_price_info (train_id, start_station_id, end_station_id, carriage_type, ticket_price, price_status)
VALUES ('G1001', 1, 2, '商务座', 1300.00, 1),
       ('G1001', 1, 2, '一等座', 800.00, 1),
       ('G1001', 1, 2, '二等座', 500.00, 1);

-- 7. 订单表
CREATE TABLE ticket_order_info
(
    order_id          INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_no          VARCHAR(50) NOT NULL UNIQUE,
    stu_no            INT,
    train_id          VARCHAR(20),
    start_station_id  INT,
    end_station_id    INT,
    carriage_id       INT,
    seat_id           INT,
    passenger_name    VARCHAR(50),
    passenger_id_card VARCHAR(20),
    ticket_price      DECIMAL(10, 2),
    order_status      INT      DEFAULT 0, -- 0:待支付, 1:已支付, 2:已取消, 3:已过期
    order_time        DATETIME DEFAULT CURRENT_TIMESTAMP,
    pay_time          DATETIME,
    order_expire      DATETIME,
    order_status_desc VARCHAR(50),
    FOREIGN KEY (stu_no) REFERENCES stu_info (stu_no),
    FOREIGN KEY (train_id) REFERENCES train_info (train_id),
    FOREIGN KEY (start_station_id) REFERENCES station_info (station_id),
    FOREIGN KEY (end_station_id) REFERENCES station_info (station_id),
    FOREIGN KEY (carriage_id) REFERENCES carriage_info (carriage_id),
    FOREIGN KEY (seat_id) REFERENCES seat_info (seat_id)
);

-- 8. 支付表
CREATE TABLE payment_info
(
    payment_id          INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id            INT,
    payment_no          VARCHAR(50) NOT NULL UNIQUE,
    payment_amount      DECIMAL(10, 2),
    payment_method      VARCHAR(20),   -- 微信支付、支付宝
    payment_status      INT DEFAULT 0, -- 0:待支付, 1:已支付, 2:支付失败
    payment_time        DATETIME,
    transaction_id      VARCHAR(100),
    payment_status_desc VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES ticket_order_info (order_id)
);

-- 索引优化
CREATE INDEX idx_train_departure ON train_info (departure_time);
CREATE INDEX idx_order_stu ON ticket_order_info (stu_no, order_status);
CREATE INDEX idx_seat_status ON seat_info (carriage_id, seat_status);
CREATE INDEX idx_stop_train ON train_stop_info (train_id, stop_sequence);

# -- 插入G1002车次（如果不存在）
#INSERT IGNORE INTO train_info # (train_id, train_number, train_type, start_station_id, end_station_id, departure_time, arrival_time, run_time, train_status)
#VALUES
# ('G1002', 'G2', '复兴号', 2, 1, '14:00:00', '18:30:00', '4小时30分', 1);

-- 为G1002添加车厢（复制G1001的数据，修改train_id）
INSERT INTO carriage_info (train_id, carriage_number, carriage_type, seat_count, seat_layout, carriage_status)
SELECT 'G1002', carriage_number, carriage_type, seat_count, seat_layout, carriage_status
FROM carriage_info
WHERE train_id = 'G1001';

-- 获取新插入的车厢ID
SELECT *
FROM carriage_info
WHERE train_id = 'G1002';

-- 为新车厢生成座位（需要根据返回的carriage_id调整下面的数值）
-- 假设新插入的车厢carriage_id为4,5,6（01,02,03车）

-- 为商务座车厢（carriage_id=4）生成座位
INSERT INTO seat_info (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
VALUES (4, '1A', 1, 1, '靠窗', 0, 300.00),
       (4, '1C', 1, 2, '过道', 0, 300.00),
       (4, '2A', 2, 1, '靠窗', 0, 300.00),
       (4, '2C', 2, 2, '过道', 0, 300.00),
       (4, '3A', 3, 1, '靠窗', 0, 300.00),
       (4, '3C', 3, 2, '过道', 0, 300.00),
       (4, '4A', 4, 1, '靠窗', 0, 300.00),
       (4, '4C', 4, 2, '过道', 0, 300.00),
       (4, '5A', 5, 1, '靠窗', 0, 300.00),
       (5, '5C', 5, 2, '过道', 0, 300.00);

-- 为一等座车厢（carriage_id=5）生成座位
INSERT INTO seat_info (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
VALUES (5, '1A', 1, 1, '靠窗', 0, 200.00),
       (5, '1C', 1, 2, '过道', 0, 200.00),
       (5, '1D', 1, 3, '过道', 0, 200.00),
       (5, '1F', 1, 4, '靠窗', 0, 200.00),
       (5, '2A', 2, 1, '靠窗', 0, 200.00),
       (5, '2C', 2, 2, '过道', 0, 200.00),
       (5, '2D', 2, 3, '过道', 0, 200.00),
       (5, '2F', 2, 4, '靠窗', 0, 200.00),
       (5, '3A', 3, 1, '靠窗', 0, 200.00),
       (5, '3C', 3, 2, '过道', 0, 200.00),
       (5, '3D', 3, 3, '过道', 0, 200.00),
       (5, '3F', 3, 4, '靠窗', 0, 200.00),
       (5, '4A', 4, 1, '靠窗', 0, 200.00),
       (5, '4C', 4, 2, '过道', 0, 200.00),
       (5, '4D', 4, 3, '过道', 0, 200.00),
       (5, '4F', 4, 4, '靠窗', 0, 200.00),
       (5, '5A', 5, 1, '靠窗', 0, 200.00),
       (5, '5C', 5, 2, '过道', 0, 200.00),
       (5, '5D', 5, 3, '过道', 0, 200.00),
       (5, '5F', 5, 4, '靠窗', 0, 200.00);

-- 为二等座车厢（carriage_id=6）生成座位
INSERT INTO seat_info (carriage_id, seat_number, seat_row, seat_col, seat_type, seat_status, seat_price)
VALUES (6, '1A', 1, 1, '靠窗', 0, 100.00),
       (6, '1B', 1, 2, '中间', 0, 100.00),
       (6, '1C', 1, 3, '过道', 0, 100.00),
       (6, '1D', 1, 4, '中间', 0, 100.00),
       (6, '1F', 1, 5, '靠窗', 0, 100.00),
       (6, '2A', 2, 1, '靠窗', 0, 100.00),
       (6, '2B', 2, 2, '中间', 0, 100.00),
       (6, '2C', 2, 3, '过道', 0, 100.00),
       (6, '2D', 2, 4, '中间', 0, 100.00),
       (6, '2F', 2, 5, '靠窗', 0, 100.00),
       (6, '3A', 3, 1, '靠窗', 0, 100.00),
       (6, '3B', 3, 2, '中间', 0, 100.00),
       (6, '3C', 3, 3, '过道', 0, 100.00),
       (6, '3D', 3, 4, '中间', 0, 100.00),
       (6, '3F', 3, 5, '靠窗', 0, 100.00),
       (6, '4A', 4, 1, '靠窗', 0, 100.00),
       (6, '4B', 4, 2, '中间', 0, 100.00),
       (6, '4C', 4, 3, '过道', 0, 100.00),
       (6, '4D', 4, 4, '中间', 0, 100.00),
       (6, '4F', 4, 5, '靠窗', 0, 100.00),
       (6, '5A', 5, 1, '靠窗', 0, 100.00),
       (6, '5B', 5, 2, '中间', 0, 100.00),
       (6, '5C', 5, 3, '过道', 0, 100.00),
       (6, '5D', 5, 4, '中间', 0, 100.00),
       (6, '5F', 5, 5, '靠窗', 0, 100.00),
       (6, '6A', 6, 1, '靠窗', 0, 100.00),
       (6, '6B', 6, 2, '中间', 0, 100.00),
       (6, '6C', 6, 3, '过道', 0, 100.00),
       (6, '6D', 6, 4, '中间', 0, 100.00),
       (6, '6F', 6, 5, '靠窗', 0, 100.00),
       (6, '7A', 7, 1, '靠窗', 0, 100.00),
       (6, '7B', 7, 2, '中间', 0, 100.00),
       (6, '7C', 7, 3, '过道', 0, 100.00),
       (6, '7D', 7, 4, '中间', 0, 100.00),
       (6, '7F', 7, 5, '靠窗', 0, 100.00),
       (6, '8A', 8, 1, '靠窗', 0, 100.00),
       (6, '8B', 8, 2, '中间', 0, 100.00),
       (6, '8C', 8, 3, '过道', 0, 100.00),
       (6, '8D', 8, 4, '中间', 0, 100.00),
       (6, '8F', 8, 5, '靠窗', 0, 100.00),
       (6, '9A', 9, 1, '靠窗', 0, 100.00),
       (6, '9B', 9, 2, '中间', 0, 100.00),
       (6, '9C', 9, 3, '过道', 0, 100.00),
       (6, '9D', 9, 4, '中间', 0, 100.00),
       (6, '9F', 9, 5, '靠窗', 0, 100.00),
       (6, '10A', 10, 1, '靠窗', 0, 100.00),
       (6, '10B', 10, 2, '中间', 0, 100.00),
       (6, '10C', 10, 3, '过道', 0, 100.00),
       (6, '10D', 10, 4, '中间', 0, 100.00),
       (6, '10F', 10, 5, '靠窗', 0, 100.00);