SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbsbuffer_schemes`
-- ----------------------------
DROP TABLE IF EXISTS `bbsbuffer_schemes`;
CREATE TABLE `bbsbuffer_schemes` (
  `char_obj_id` int(11) NOT NULL,
  `scheme_id` int(11) NOT NULL,
  `scheme_name` varchar(13) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbsbuffer_schemes
-- ----------------------------
