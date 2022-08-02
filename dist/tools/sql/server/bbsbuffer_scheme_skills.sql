SET FOREIGN_KEY_CHECKS=0;

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
