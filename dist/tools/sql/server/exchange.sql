CREATE TABLE IF NOT EXISTS `exchange` (
  `object_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `item_id` int(7) NOT NULL,
  `count` bigint(20) NOT NULL,
  `enchant_level` int(11) NOT NULL,  
  `augmentation_id` int(11) NOT NULL,
  `attribute_fire` int(11) NOT NULL,
  `attribute_water` int(11) NOT NULL,
  `attribute_wind` int(11) NOT NULL,
  `attribute_earth` int(11) NOT NULL,
  `attribute_holy` int(11) NOT NULL,
  `attribute_unholy` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  PRIMARY KEY  (`object_id`),
  KEY `owner_id` (`owner_id`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB;