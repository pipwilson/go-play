CREATE TABLE IF NOT EXISTS `UrlAlias` (
  `id` varchar(255) NOT NULL,
  `created` datetime default NULL,
  `creatorApiKey` varchar(255) default NULL,
  `creatorUsername` varchar(255) default NULL,
  `isCustomAlias` bit(1) NOT NULL,
  `target` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
)