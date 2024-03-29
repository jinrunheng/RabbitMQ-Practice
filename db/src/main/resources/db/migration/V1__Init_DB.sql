SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for deliveryman
-- ----------------------------
DROP TABLE IF EXISTS `deliveryman`;
CREATE TABLE `deliveryman`
(
    `id`     int(0)      NOT NULL AUTO_INCREMENT COMMENT '骑手id',
    `name`   varchar(36) NULL DEFAULT NULL COMMENT '名称',
    `status` varchar(36) NULL DEFAULT NULL COMMENT '状态',
    `date`   datetime(0) NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of deliveryman
-- ----------------------------
BEGIN;
INSERT INTO `deliveryman`
VALUES (1, '王小二', 'AVAILABLE', '2020-06-10 20:30:17');
COMMIT;

DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`
(
    `id`             int(0)         NOT NULL AUTO_INCREMENT COMMENT '订单id',
    `status`         varchar(36)    NULL DEFAULT NULL COMMENT '状态',
    `address`        varchar(64)    NULL DEFAULT NULL COMMENT '订单地址',
    `account_id`     int(0)         NULL DEFAULT NULL COMMENT '用户id',
    `product_id`     int(0)         NULL DEFAULT NULL COMMENT '产品id',
    `deliveryman_id` int(0)         NULL DEFAULT NULL COMMENT '骑手id',
    `settlement_id`  int(0)         NULL DEFAULT NULL COMMENT '结算id',
    `reward_id`      int(0)         NULL DEFAULT NULL COMMENT '积分奖励id',
    `price`          decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
    `date`           datetime(0)    NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  AUTO_INCREMENT = 403
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`
(
    `id`            int(0)        NOT NULL AUTO_INCREMENT COMMENT '产品id',
    `name`          varchar(36)   NULL DEFAULT NULL COMMENT '名称',
    `price`         decimal(9, 2) NULL DEFAULT NULL COMMENT '单价',
    `restaurant_id` int(0)        NULL DEFAULT NULL COMMENT '地址',
    `status`        varchar(36)   NULL DEFAULT NULL COMMENT '状态',
    `date`          datetime(0)   NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
BEGIN;
INSERT INTO `product`
VALUES (2, '汉堡', 23.25, 1, 'AVAILABLE', '2020-05-06 19:19:04');
COMMIT;
DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant`
(
    `id`            int(0)      NOT NULL AUTO_INCREMENT COMMENT '餐厅id',
    `name`          varchar(36) NULL DEFAULT NULL COMMENT '名称',
    `address`       varchar(36) NULL DEFAULT NULL COMMENT '地址',
    `status`        varchar(36) NULL DEFAULT NULL COMMENT '状态',
    `settlement_id` int(0)      NULL DEFAULT NULL COMMENT '结算id',
    `date`          datetime(0) NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of restaurant
-- ----------------------------
BEGIN;
INSERT INTO `restaurant`
VALUES (1, 'KFC', '深圳市华强北店', 'OPEN', 1, '2020-05-06 19:19:39');
COMMIT;
DROP TABLE IF EXISTS `reward`;
CREATE TABLE `reward`
(
    `id`       int(0)        NOT NULL AUTO_INCREMENT COMMENT '奖励id',
    `order_id` int(0)        NULL DEFAULT NULL COMMENT '订单id',
    `amount`   decimal(9, 2) NULL DEFAULT NULL COMMENT '积分量',
    `status`   varchar(36)   NULL DEFAULT NULL COMMENT '状态',
    `date`     datetime(0)   NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  AUTO_INCREMENT = 8
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `settlement`;
CREATE TABLE `settlement`
(
    `id`             int(0)        NOT NULL AUTO_INCREMENT COMMENT '结算id',
    `order_id`       int(0)        NULL DEFAULT NULL COMMENT '订单id',
    `transaction_id` int(0)        NULL DEFAULT NULL COMMENT '交易id',
    `amount`         decimal(9, 2) NULL DEFAULT NULL COMMENT '金额',
    `status`         varchar(36)   NULL DEFAULT NULL COMMENT '状态',
    `date`           datetime(0)   NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  AUTO_INCREMENT = 1168
  ROW_FORMAT = Dynamic;

