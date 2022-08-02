DROP TABLE IF EXISTS `character_academ`;
SET NAMES 'utf8';
CREATE TABLE `character_academ` (
  `clan_id` int NOT NULL DEFAULT 0,
  `char_academ_id` INT(11) NOT NULL,
  `itemId` INT(11) NOT NULL,
  `price` BIGINT DEFAULT NULL,
  `time` BIGINT DEFAULT NULL,
  PRIMARY KEY  (`clan_id`),
  KEY `char_academ_id` (`char_academ_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;