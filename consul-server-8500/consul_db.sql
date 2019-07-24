/*
 Navicat Premium Data Transfer

 Source Server         : 62.234.44.124-腾讯云
 Source Server Type    : MySQL
 Source Server Version : 50644
 Source Host           : 62.234.44.124:3306
 Source Schema         : consul_db

 Target Server Type    : MySQL
 Target Server Version : 50644
 File Encoding         : 65001

 Date: 18/07/2019 17:46:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for node
-- ----------------------------
DROP TABLE IF EXISTS `node`;
CREATE TABLE `node`  (
  `id` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '节点ID',
  `node` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '节点名',
  `address` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `health` tinyint(4) NULL DEFAULT 1 COMMENT '健康状态，1健康，0为不健康',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of node
-- ----------------------------
INSERT INTO `node` VALUES ('1', 'testNode', '127.0.0.1', 1);

-- ----------------------------
-- Table structure for service
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service`  (
  `id` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '服务ID',
  `service` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '服务名称',
  `address` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '提供服务的ip地址',
  `port` varchar(16) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '提供服务的端口号',
  `health` tinyint(4) NULL DEFAULT 1 COMMENT '服务健康状态，1为健康，0为不健康',
  `node_id` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '提供服务的节点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `node-service`(`node_id`) USING BTREE,
  CONSTRAINT `node-service` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of service
-- ----------------------------
INSERT INTO `service` VALUES ('1', 'testService', '127.0.0.1', '8080', 1, '1');
INSERT INTO `service` VALUES ('2', 'redis', '12123213', NULL, 1, '1');
INSERT INTO `service` VALUES ('3', 'redis', '12123213', '8090', 0, '1');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `service_id` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `value` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  INDEX `service_id_index`(`service_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES ('1', 'version=1.0');
INSERT INTO `tag` VALUES ('1', 'address=127.0.0.1');
INSERT INTO `tag` VALUES ('2', 'version=2.0');

SET FOREIGN_KEY_CHECKS = 1;
