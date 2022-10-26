/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : localhost:3306
 Source Schema         : food

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 12/10/2020 09:35:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for deliveryman
-- ----------------------------
DROP TABLE IF EXISTS `deliveryman`;
CREATE TABLE `deliveryman`
(
    `id`     int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '骑手id',
    `name`   varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `date`   datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of deliveryman
-- ----------------------------
BEGIN;
INSERT INTO `deliveryman`
VALUES (1, 'wangxiaoer', 'AVALIABLE', '2020-06-10 20:30:17');
COMMIT;
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`
(
    `id`             int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '订单id',
    `status`         varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `address`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单地址',
    `account_id`     int(0)                                                 NULL DEFAULT NULL COMMENT '用户id',
    `product_id`     int(0)                                                 NULL DEFAULT NULL COMMENT '产品id',
    `deliveryman_id` int(0)                                                 NULL DEFAULT NULL COMMENT '骑手id',
    `settlement_id`  int(0)                                                 NULL DEFAULT NULL COMMENT '结算id',
    `reward_id`      int(0)                                                 NULL DEFAULT NULL COMMENT '积分奖励id',
    `price`          decimal(10, 2)                                         NULL DEFAULT NULL COMMENT '价格',
    `date`           datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  AUTO_INCREMENT = 403
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`
(
    `id`            int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '产品id',
    `name`          varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `price`         decimal(9, 2)                                          NULL DEFAULT NULL COMMENT '单价',
    `restaurant_id` int(0)                                                 NULL DEFAULT NULL COMMENT '地址',
    `status`        varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `date`          datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
BEGIN;
INSERT INTO `product`
VALUES (2, 'eqwe', 23.25, 1, 'AVALIABLE', '2020-05-06 19:19:04');
COMMIT;
DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant`
(
    `id`            int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '餐厅id',
    `name`          varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `address`       varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
    `status`        varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `settlement_id` int(0)                                                 NULL DEFAULT NULL COMMENT '结算id',
    `date`          datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of restaurant
-- ----------------------------
BEGIN;
INSERT INTO `restaurant`
VALUES (1, 'qeqwe', '2weqe', 'OPEN', 1, '2020-05-06 19:19:39');
COMMIT;
DROP TABLE IF EXISTS `reward`;
CREATE TABLE `reward`
(
    `id`       int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '奖励id',
    `order_id` int(0)                                                 NULL DEFAULT NULL COMMENT '订单id',
    `amount`   decimal(9, 2)                                          NULL DEFAULT NULL COMMENT '积分量',
    `status`   varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `date`     datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  AUTO_INCREMENT = 8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `settlement`;
CREATE TABLE `settlement`
(
    `id`             int(0)                                                 NOT NULL AUTO_INCREMENT COMMENT '结算id',
    `order_id`       int(0)                                                 NULL DEFAULT NULL COMMENT '订单id',
    `transaction_id` int(0)                                                 NULL DEFAULT NULL COMMENT '交易id',
    `amount`         decimal(9, 2)                                          NULL DEFAULT NULL COMMENT '金额',
    `status`         varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
    `date`           datetime(0)                                            NULL DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  AUTO_INCREMENT = 1168
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

