CREATE TABLE `kbplus_fact` (
  `fact_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fact_version` bigint(20) NOT NULL,
  `fact_from` datetime NOT NULL,
  `fact_to` datetime NOT NULL,
  `fact_type_rdv_fk` bigint(20) NOT NULL,
  `fact_uid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `fact_value` varchar(255) COLLATE utf8_bin NOT NULL,
  `inst_id` bigint(20) DEFAULT NULL,
  `juspio_id` bigint(20) DEFAULT NULL,
  `related_title_id` bigint(20) DEFAULT NULL,
  `reporting_month` bigint(20) DEFAULT NULL,
  `reporting_year` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`fact_id`),
  UNIQUE KEY `fact_uid_idx` (`fact_uid`,`fact_id`),
  KEY `FK2FD66CD2A25EFB` (`inst_id`),
  KEY `FK2FD66C4CB39BA6` (`related_title_id`),
  KEY `FK2FD66C40C7D5B5` (`supplier_id`),
  KEY `FK2FD66C467CFA43` (`juspio_id`),
  KEY `FK2FD66C5CC2FB63` (`fact_type_rdv_fk`),
  KEY `fact_access_idx` (`inst_id`,`related_title_id`,`supplier_id`) USING BTREE,
  KEY `FK6784767AD2A25EFB` (`inst_id`),
  KEY `FK6784767A4CB39BA6` (`related_title_id`),
  KEY `FK6784767A40C7D5B5` (`supplier_id`),
  KEY `FK6784767A467CFA43` (`juspio_id`),
  KEY `FK6784767A5CC2FB63` (`fact_type_rdv_fk`)
) ENGINE=MyISAM AUTO_INCREMENT=113628605 DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/*!50100 PARTITION BY KEY (fact_id)
PARTITIONS 12 */

CREATE TABLE `empty_kbplus_fact` (
  `fact_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fact_version` bigint(20) NOT NULL,
  `fact_from` datetime NOT NULL,
  `fact_to` datetime NOT NULL,
  `fact_type_rdv_fk` bigint(20) NOT NULL,
  `fact_uid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `fact_value` varchar(255) COLLATE utf8_bin NOT NULL,
  `inst_id` bigint(20) DEFAULT NULL,
  `juspio_id` bigint(20) DEFAULT NULL,
  `related_title_id` bigint(20) DEFAULT NULL,
  `reporting_month` bigint(20) DEFAULT NULL,
  `reporting_year` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`fact_id`),
  UNIQUE KEY `fact_uid` (`fact_uid`),
  KEY `FK6784767AD2A25EFB` (`inst_id`),
  KEY `FK6784767A4CB39BA6` (`related_title_id`),
  KEY `FK6784767A40C7D5B5` (`supplier_id`),
  KEY `FK6784767A467CFA43` (`juspio_id`),
  KEY `FK6784767A5CC2FB63` (`fact_type_rdv_fk`),
  KEY `fact_access_idx` (`inst_id`,`related_title_id`,`supplier_id`),
  KEY `fact_uid_idx` (`fact_uid`),
  CONSTRAINT `FK6784767A40C7D5B5` FOREIGN KEY (`supplier_id`) REFERENCES `org` (`org_id`),
  CONSTRAINT `FK6784767A467CFA43` FOREIGN KEY (`juspio_id`) REFERENCES `identifier_occurrence` (`io_id`),
  CONSTRAINT `FK6784767A4CB39BA6` FOREIGN KEY (`related_title_id`) REFERENCES `title_instance` (`ti_id`),
  CONSTRAINT `FK6784767A5CC2FB63` FOREIGN KEY (`fact_type_rdv_fk`) REFERENCES `refdata_value` (`rdv_id`),
  CONSTRAINT `FK6784767AD2A25EFB` FOREIGN KEY (`inst_id`) REFERENCES `org` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
PARTITION BY KEY (fact_id) PARTITIONS 12;


CREATE TABLE `empty_kbplus_fact` (
  `fact_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fact_version` bigint(20) NOT NULL,
  `fact_from` datetime NOT NULL,
  `fact_to` datetime NOT NULL,
  `fact_type_rdv_fk` bigint(20) NOT NULL,
  `fact_uid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `fact_value` varchar(255) COLLATE utf8_bin NOT NULL,
  `inst_id` bigint(20) DEFAULT NULL,
  `juspio_id` bigint(20) DEFAULT NULL,
  `related_title_id` bigint(20) DEFAULT NULL,
  `reporting_month` bigint(20) DEFAULT NULL,
  `reporting_year` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`fact_id`),
  KEY `FK6784767A4CB39BA6` (`related_title_id`),
  KEY `fact_access_idx` (`inst_id`,`related_title_id`,`supplier_id`),
  KEY `fact_uid_idx` (`fact_uid`)
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
PARTITION BY KEY (fact_id) PARTITIONS 12 
;

