/*
Navicat MySQL Data Transfer

Source Server         : 200
Source Server Version : 50641
Source Host           : 10.40.40.200:3306
Source Database       : frame-api

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2018-11-10 11:38:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sms_log`
-- ----------------------------
DROP TABLE IF EXISTS `sms_log`;
CREATE TABLE `sms_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `phone_num` VARCHAR(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '手机号码',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=651 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送记录';

-- ----------------------------
-- Records of sms_log
-- ----------------------------

-- ----------------------------
-- Table structure for `person`
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `age` INT(11) NOT NULL DEFAULT '0' COMMENT '年龄',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of person
-- ----------------------------
INSERT INTO `person` VALUES ('1', 'jack', '2399', '1541817314000');
INSERT INTO `person` VALUES ('2', 'tom', '25', '1541817314000');
INSERT INTO `person` VALUES ('3', 'dddd', '56', '1541819296396');

-- ----------------------------
-- Table structure for `sys_admin`
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
  `id` VARCHAR(40) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '主键',
  `mag_type` INT(2) NOT NULL DEFAULT '0' COMMENT '类型 0-普通管理员/1-超级管理员',
  `username` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `pwd` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES ('dfde0f72-af48-40a9-8c27-2dd06627ee8b', '1', 'admin', 'e10adc3949ba59abbe56e057f20f883e');
