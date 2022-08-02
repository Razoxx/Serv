SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbsbuffer_buffs_info`
-- ----------------------------
DROP TABLE IF EXISTS `bbsbuffer_buffs_info`;
CREATE TABLE `bbsbuffer_buffs_info` (
  `skill_id` int(4) NOT NULL,
  `skill_level` int(2) NOT NULL DEFAULT '1',
  `skill_name` varchar(40) DEFAULT NULL,
  `description` varchar(40) DEFAULT NULL,
  `same_effect_group` varchar(5) NOT NULL DEFAULT 'false',
  `group_id` int(4) NOT NULL DEFAULT '0' COMMENT 'group buffs by BD, PP, SWS, Other classes. All info in config.',
  PRIMARY KEY (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbsbuffer_buffs_info
-- ----------------------------
INSERT INTO `bbsbuffer_buffs_info` VALUES ('264', '1', 'Song of Earth', 'Increase P.Def', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('265', '1', 'Song of Life', 'Increase HP regeneration rate', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('266', '1', 'Song of Water', 'Increase evasion', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('267', '1', 'Song of Warding', 'Increase M.Def', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('268', '1', 'Song of Wind', 'Increase walk/run speed', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('269', '1', 'Song of Hunter', 'Increase critical atack rate', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('270', '1', 'Song of Invocation', 'Increase dark resistance', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('271', '1', 'Dance of Warrior', 'Increase P.Atk', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('272', '1', 'Dance of Inspiration', 'Increase accuracy', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('273', '1', 'Dance of Mystic', 'Increase M.Atk', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('274', '1', 'Dance of Fire', 'Increase critical atack power', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('275', '1', 'Dance of Fury', 'Increase Atk.Spd', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('276', '1', 'Dance of Concentration', 'Increase Cast.Spd', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('277', '1', 'Dance of Light', 'Add holy atribute to P.Atk', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('304', '1', 'Song of Vitality', 'Increase maximum HP', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('305', '1', 'Song of Vengeance', 'Reflect damage back upon enemy', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('306', '1', 'Song of Flame Guard', 'Increase fire resistance', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('307', '1', 'Dance of Aqua Guard', 'Increase water resistance', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('308', '1', 'Song of Storm Guard', 'Increase wind resistance', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('309', '1', 'Dance of Earth Guard', 'Increase earth resistance', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('310', '1', 'Dance of Vampire', 'Restore HP when hit with P.Atk', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('311', '1', 'Dance of Protection', 'Increase terrain damage resistance', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('349', '1', 'Song of Renewal', 'Decrease MP cost & reuse skills', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('363', '1', 'Song of Meditation', 'Decrease cons. rate for m.skills', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('364', '1', 'Song of Champion', 'Decrease MP cost & reuse p.skills', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('365', '1', 'Dance of Siren', 'Increase magic critical rate', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('529', '1', 'Song of Elemental', 'Increases element resistance', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('530', '1', 'Dance of Alignment', 'Increases resistance to H or D', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('825', '1', 'Sharp Edge', 'Sharpens a bladed weapon ', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('826', '1', 'Spike', 'Adds a spike to a blunt weapon ', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('827', '1', 'Restring', 'Enhances the bow or crossbow', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('828', '1', 'Case Harden', 'Enhances the armor surface ', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('829', '1', 'Hard Tanning', 'Tans armor to increase', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('830', '1', 'Embroider', 'Embroiders a robe to increase', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('915', '1', 'Dance of Berserker', 'Increase P/M.Atk, Atk/Cast.Spd', '0', '111');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('982', '3', 'Combat Aura', 'Increases P. Atk. and Atk. Spd', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('988', '1', 'Battle Whisper', 'Increase P.Atk.A.Speed', '0', '103');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1005', '3', 'Blessings of Pa\'agrio', 'Increase P.Def', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1006', '3', 'Chant of Fire', 'Increase M.Def', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1008', '3', 'The Glory of Pa\'agrio', 'Increase M.Def', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1009', '3', 'Chant of Shielding', 'Increase P.Def', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1032', '3', 'Invigor', 'Increase bleeding resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1033', '3', 'Resist Poison', 'Increase poison resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1035', '4', 'Mental Shield', 'Increase mental atacks resistance', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1036', '2', 'Magic Barrier', 'Increase M.Def', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1040', '3', 'Shield', 'Increase P.Def', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1043', '1', 'Holy Weapon', 'Add holy atribute to P.Atk', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1044', '3', 'Regeneration', 'Increase HP recovery', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1045', '6', 'Bless the Body', 'Increase maximum HP', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1048', '6', 'Bless the Soul', 'Increase maximum MP', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1059', '3', 'Empower', 'Increase M.Atk', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1062', '2', 'Berserker Spirit', 'Increase P/M.Atk, Atk/Cast.Spd', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1068', '3', 'Might', 'Increase P.Atk', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1073', '2', 'Kiss of Eva', 'Increase lung capacity', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1077', '3', 'Focus', 'Increase critical atack rate', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1078', '6', 'Concentration', 'Decrease magic cancel rate', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1085', '3', 'Acumen', 'Inrease Cast.Spd', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1086', '2', 'Haste', 'Increase Atk.Spd', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1087', '3', 'Agility', 'Increase evasion', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1182', '93', 'Resist Aqua', 'Increase water resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1189', '93', 'Resist Wind', 'Increase wind resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1191', '93', 'Resist Fire', 'Increase fire resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1204', '2', 'Wind Walk', 'Increase walk/run speed', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1232', '93', 'Blazing skin', 'Reflect damage back upon enemy', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1240', '3', 'Guidance', 'Increase accuracy', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1242', '3', 'Death Whisper', 'Increase critical atack power', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1243', '6', 'Bless Shield', 'Increase shield defense rate', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1250', '3', 'Under the Protection of Pa\'agrio', 'Increase shield defence rate', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1251', '2', 'Chant of Fury', 'Increase Atk.Spd', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1252', '3', 'Chant of Evasion', 'Increase evasion', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1253', '3', 'Chant of Rage', 'Increase critical atack damage', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1257', '3', 'Decrease Weight', 'Makes body lighter', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1259', '4', 'Resist Shock', 'Increase stun resistance', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1261', '2', 'The Rage of Pa\'agrio', 'Increase P/M.Atk, Atk/Cast.Spd', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1268', '4', 'Vampiric Rage', 'Restore HP when hit with P.Atk', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1282', '2', 'Pa\'agrian Haste', 'Increase walk/run speed', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1284', '3', 'Chant of Revenge', 'Reflect damage back upon enemy', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1303', '2', 'Wild Magic', 'Increase magic critical atack rate', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1307', '3', 'Prayer', 'Increase HP recovery power', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1308', '3', 'Chant of Predator', 'Increase critical atack rate', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1309', '3', 'Chant of Eagle', 'Increase accuracy', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1310', '4', 'Chant of Vampire', 'Restore HP when hit with P.Atk', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1352', '1', 'Elemental Protection', 'Increase elemental resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1353', '1', 'Divine Protection', 'Increase dark/holy resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1354', '1', 'Arcane Protection', 'Increase  cancel/debuff resistance', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1355', '46', 'Prophecy of Water', 'Increase Fighter/Mage abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1356', '46', 'Prophecy of Fire', 'Increase fighter abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1357', '46', 'Prophecy of Wind', 'Increase Fighter/Mage abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1362', '1', 'Chant of Spirit', 'Increase  cancel/debuff resistance', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1363', '46', 'Chant of Victory', 'Increase Fighter abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1364', '1', 'Eye of Paagrio', 'Resist Crt Atack', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1388', '3', 'Greater Might', 'Increase P.Atk', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1389', '3', 'Greater Shield', 'Increase P.Def', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1390', '3', 'War Chant', 'Increase P.Atk', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1391', '3', 'Earth Chant', 'Increase P.Def', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1392', '3', 'Holy Resistance', 'Increase holy resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1393', '3', 'Unholy Resistance', 'Increase dark resistance', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1397', '3', 'Clarity', 'Decrease MP consumption by skills', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1413', '31', 'Magnus\' Chant', 'Increase Mage abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1414', '1', 'Victories of Paagrio', 'Increase Fighter/Mage abilities', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1415', '1', 'Pa\'agrio\'s Emblem', 'Increase  cancel/debuff resistance', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1416', '1', 'Pa\'agrio\'s Fist', 'Increase maximum CP', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1442', '33', 'Protection from Darkness', 'Increases Resistance to Dark', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1443', '33', 'Dark Weapon', 'Increases Atack of Dark', '0', '777');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1461', '1', 'Chant of Protection', 'Decrease chance of gaining Crt Hit ', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1499', '1', 'Improved Combat', 'Combines P. Atk. and P. Def.', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1500', '1', 'Improved Magic', 'Increases both M. Atk. and M. Def.', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1501', '1', 'Improved Condition', 'Combines maximum HPand Max MP', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1502', '1', 'Improved Critical Attack', 'Combines Crt. Crt Damage', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1503', '1', 'Improved Shield Defense', 'Combines S. D.rate and S.Def', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1504', '1', 'Improved Movement', 'Combines Spd. and Evasion', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1517', '1', 'Chant of Combat', 'Combines P. Atk. and P. Def ', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1519', '1', 'Chant of Blood Awakening', 'Attack damage abs. and Atk. Spd.', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1542', '1', 'Counter Critical', 'Increases P. Def. against Critical ', '0', '98');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1548', '93', 'Resist Earth', 'Increases resistance to Earth', '0', '555');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('1562', '1', 'Chant of berserk', 'Increase P/M.Atk, Atk/Cast.Spd', '0', '116');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('4699', '8', 'Blessing of Queen', 'Increase critical atack rate', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('4700', '8', 'Gift of Queen', 'Increase P.Atk and Accuracy', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('4702', '8', 'Blessing of Seraphim', 'Increase MP regeneration rate', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('4703', '8', 'Gift of Seraphim', 'Decrease reuse time of skills', '0', '556');
INSERT INTO `bbsbuffer_buffs_info` VALUES ('5841', '1', 'Multi Defense', 'Increase P.Def, M.Def', '0', '103');
