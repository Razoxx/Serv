SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbsbuffer_classes`
-- ----------------------------
DROP TABLE IF EXISTS `bbsbuffer_classes`;
CREATE TABLE `bbsbuffer_classes` (
  `char_obj_id` int(11) NOT NULL,
  `buffclass_id` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbsbuffer_classes
-- ----------------------------

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

-- ----------------------------
-- Table structure for `bbsbuffer_scheme_skills`
-- ----------------------------
DROP TABLE IF EXISTS `bbsbuffer_scheme_skills`;
CREATE TABLE `bbsbuffer_scheme_skills` (
  `char_obj_id` int(11) NOT NULL,
  `scheme_id` int(11) NOT NULL,
  `skill_id` int(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbsbuffer_scheme_skills
-- ----------------------------
