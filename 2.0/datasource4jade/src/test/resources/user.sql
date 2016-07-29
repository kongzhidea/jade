/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2016-07-26 18:06:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '',
  `realname` varchar(50) DEFAULT NULL COMMENT '',
  `privs` text COMMENT '权限',
  `status` char(1) DEFAULT NULL COMMENT '',
  `ctime` datetime DEFAULT NULL,
  `city_name` varchar(40) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  KEY `idx_real_name` (`realname`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
