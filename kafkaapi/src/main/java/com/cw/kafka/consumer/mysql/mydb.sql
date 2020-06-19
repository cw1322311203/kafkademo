/*
Navicat MySQL Data Transfer

Source Server         : 192.168.139.101
Source Server Version : 50544
Source Host           : 192.168.139.101:3306
Source Database       : mydb

Target Server Type    : MYSQL
Target Server Version : 50544
File Encoding         : 65001

Date: 2020-06-19 22:30:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `offset`
-- ----------------------------
DROP TABLE IF EXISTS `offset`;
CREATE TABLE `offset` (
  `consumer_group` varchar(255) NOT NULL DEFAULT '',
  `sub_topic` varchar(255) NOT NULL DEFAULT '',
  `sub_topic_partition_id` int(11) NOT NULL DEFAULT '0',
  `sub_topic_partition_offset` bigint(20) NOT NULL,
  `timestamp` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`consumer_group`,`sub_topic`,`sub_topic_partition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of offset
-- ----------------------------
INSERT INTO `offset` VALUES ('mysql_offset', 'mysql_store_offset', '0', '32', '2020年06月19日 22:15:48');
INSERT INTO `offset` VALUES ('mysql_offset', 'mysql_store_offset', '1', '30', '2020年06月19日 22:15:48');
INSERT INTO `offset` VALUES ('mysql_offset', 'mysql_store_offset', '2', '35', '2020年06月19日 22:16:38');
