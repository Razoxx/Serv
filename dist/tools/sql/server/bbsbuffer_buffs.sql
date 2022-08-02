SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbsbuffer_buffs`
-- ----------------------------
DROP TABLE IF EXISTS `bbsbuffer_buffs`;
CREATE TABLE `bbsbuffer_buffs` (
  `skill_id` int(4) NOT NULL,
  `order_id` int(4) NOT NULL AUTO_INCREMENT,
  `duration_minutes` int(10) NOT NULL DEFAULT '60',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbsbuffer_buffs
-- ----------------------------
INSERT INTO `bbsbuffer_buffs` VALUES ('264', '1', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('267', '2', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('305', '3', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('304', '4', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('265', '5', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('268', '6', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('266', '7', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('269', '8', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('270', '9', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('308', '10', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('306', '11', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('529', '12', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('349', '13', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('363', '14', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('364', '15', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('988', '16', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('5841', '17', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('530', '18', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('271', '19', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('273', '20', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('274', '21', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('275', '22', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('276', '23', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('365', '24', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('915', '25', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('310', '26', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('272', '27', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('307', '28', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('309', '29', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('311', '30', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1548', '31', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1182', '32', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1189', '33', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1191', '34', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1352', '35', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1353', '36', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1392', '37', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('277', '38', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1043', '39', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1443', '40', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1442', '41', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1499', '42', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1500', '43', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1501', '44', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1502', '45', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1503', '46', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1504', '47', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1388', '48', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1389', '49', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1542', '50', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1461', '51', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1068', '52', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1059', '53', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1086', '54', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1085', '55', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1040', '56', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1036', '57', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1045', '58', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1048', '59', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1035', '60', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1259', '61', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1354', '62', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1362', '63', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1077', '64', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1242', '65', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1303', '66', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1078', '67', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1243', '68', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1062', '69', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1204', '70', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1087', '71', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1240', '72', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1044', '73', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1307', '74', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1397', '75', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1032', '76', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1033', '77', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1073', '78', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1257', '79', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1390', '80', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1391', '81', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1310', '82', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1284', '83', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1517', '84', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1519', '85', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1009', '86', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1562', '87', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1005', '88', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1006', '89', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1008', '90', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1250', '91', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1251', '92', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1253', '93', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1252', '94', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1309', '95', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1261', '96', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1364', '97', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1268', '98', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1308', '99', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1282', '100', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1355', '101', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1356', '102', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1357', '103', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1414', '104', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1363', '105', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1413', '106', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1415', '107', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1416', '108', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('982', '109', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('1232', '110', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('4699', '111', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('4700', '112', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('4703', '113', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('4702', '114', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('825', '115', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('826', '116', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('827', '117', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('828', '118', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('829', '119', '180');
INSERT INTO `bbsbuffer_buffs` VALUES ('830', '120', '180');
