#
# Initial attempt at create .sql script for KB+ project.
# Adopted the tbl_prefix naming convention Paul Needham mentioned, as it makes it easier to build queries
# without recourse to lots of aliases in join clauses
#
# Changes
# 
# 04-Apr-2012 II  Created
# 11-Apr-2012 II  Added <prefix>_imp_id varchar(36) columns to org,pkg,plat and so tables, to optimise import. Allows guid lookup.
#




create database kbplus default charset UTF8 default collate UTF8_BIN;
grant all on kbplus.* to 'k-int'@'localhost';
grant all on kbplus.* to 'k-int'@'localhost.localdomain';

use kbplus;

drop table if exists refdata_category;
drop table if exists refdata_value;
drop table if exists document;
drop table if exists journal;
drop table if exists organisation;
drop table if exists package;
drop table if exists package_content;
drop table if exists platform;
drop table if exists subscription_offered;
drop table if exists package_content_platform;
drop table if exists document_organisation;
drop table if exists document_package;
drop table if exists document_subscription_offered;
drop table if exists title_instance;
drop table if exists title_instance_package_platform;


#
# Refdata category, currently only document type. Suggest using this style instead of enums in table
# as enums not well supported cross databases, and would tie us (Pretty needlessly) to MySQL.
#
create table refdata_category (
  rdc_id INT NOT NULL auto_increment,
  rdc_desc VARCHAR(255) NOT NULL,
  CONSTRAINT PRIMARY KEY (rdc_id)
);

#
# For general refdata - Maybe we need a type column or a disjoint type classification with int,varchar,etc fields
# For now, assume varchar is good enough.
#
create table refdata_value (
  rdv_id INT NOT NULL auto_increment,
  rdv_rdc_id INT NOT NULL,
  rdv_value VARCHAR(255),
  CONSTRAINT PRIMARY KEY (rdv_id),
  CONSTRAINT FOREIGN KEY(rdv_rdc_id) references refdata_category
);

insert into refdata_category VALUES (1,'Document Type');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 1, 'Model License');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 1, 'Subscription Offer Terms');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 1, 'Other');

insert into refdata_category VALUES (2,'Organisational Role');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Aggregator');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Consortium');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Gateway');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Host');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Licensee');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Licensor');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Provider');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Publisher');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Subscriber');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 2, 'Subscription Agent');

insert into refdata_category VALUES (3,'Platform Type');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 3, 'Host');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 3, 'Admin');
insert into refdata_value(rdv_rdc_id, rdv_value) VALUES ( 3, 'Software');

create table document (
  doc_id INT NOT NULL auto_increment,
  doc_title VARCHAR(255) NOT NULL,
  doc_file VARCHAR(255) NOT NULL,
  doc_type INT NOT NULL,
  doc_display varchar(255) NOT NULL,
  CONSTRAINT PRIMARY KEY (doc_id),
  CONSTRAINT FOREIGN KEY (doc_type) REFERENCES refdata_value
);

create table journal (
  jou_id INT NOT NULL auto_increment,
  jou_title VARCHAR(255) NOT NULL,
  jou_issn VARCHAR (9),
  jou_eissn VARCHAR (9),
  CONSTRAINT PRIMARY KEY (jou_id)
);

create table organisation (
  org_id INT NOT NULL auto_increment,
  org_name VARCHAR(255) NOT NULL,
  org_alt_name VARCHAR(255) NOT NULL,
  org_role INT NOT NULL,
  org_RinggoldID VARCHAR(255) NOT NULL,
  org_IngentaID VARCHAR(255) NOT NULL,
  org_JCID VARCHAR(255) NOT NULL,
  org_UKAMFIdP VARCHAR(255) NOT NULL,
  org_AthensID VARCHAR(255) NOT NULL,
  org_IPRange VARCHAR(255) NOT NULL,
  org_imp_id VARCHAR(36),
  CONSTRAINT PRIMARY KEY (org_id),
  CONSTRAINT FOREIGN KEY (org_role) REFERENCES refdata_value
);

create table package (
  pkg_id int NOT NULL auto_increment,
  pkg_org_id int NOT NULL,
  pkg_name VARCHAR(255),
  pkg_imp_id VARCHAR(36),
  CONSTRAINT PRIMARY KEY (pkg_id),
  CONSTRAINT FOREIGN KEY (pkg_org_id) REFERENCES organisation
);

create table package_content (
  pc_id int NOT NULL auto_increment,
  pc_jid int NOT NULL,
  pc_date_first_issue DATE,
  pc_num_first_vol varchar(25),
  pc_num_first_iss varchar(25),
  pc_date_last_issue DATE,
  pc_num_last_vol varchar(25),
  pc_num_last_iss varchar(25),
  pc_embargo_info varchar(25),
  pc_coverage_depth varchar(25),
  pc_coverage_notes longtext,
  pc_publisher_name varchar(255),
  CONSTRAINT PRIMARY KEY (pc_id),
  CONSTRAINT FOREIGN KEY (pc_jid) REFERENCES journal
);

create table platform (
  plat_id int NOT NULL auto_increment,
  plat_name varchar(255),
  plat_url varchar(255),
  plat_type int,
  plat_admin int,  
  plat_imp_id VARCHAR(36),
  CONSTRAINT PRIMARY KEY (plat_id),
  CONSTRAINT FOREIGN KEY (plat_type) REFERENCES refdata_value 
);

create table subscription_offered (
  so_id int NOT NULL auto_increment,
  so_org_id_provider int not null,
  so_plat_id int not null,
  so_start int,
  so_end int,
  so_org_id_consortium int,
  so_imp_id VARCHAR(36),
  CONSTRAINT PRIMARY KEY (so_id),
  CONSTRAINT FOREIGN KEY (so_org_id_provider) REFERENCES organisation,
  CONSTRAINT FOREIGN KEY (so_plat_id) REFERENCES platform,
  CONSTRAINT FOREIGN KEY (so_org_id_consortium) REFERENCES organisation
);

# There is probably a pretty sound argument for this table to have its own generated key instead of the
# compound relationship
create table package_content_platform (
  pcp_pkg_id int NOT NULL,
  pcp_jou_id int NOT NULL,
  pcp_so_id int NOT NULL,
  pcp_plat_id int NOT NULL,
  pcp_titleurl varchar(255),
  pcp_proprietary_id varchar(128),
  pcp_proprietary_id_type varchar(25),
  CONSTRAINT PRIMARY KEY (pcp_pkg_id, pcp_jou_id, pcp_so_id, pcp_plat_id),
  CONSTRAINT FOREIGN KEY (pcp_pkg_id) REFERENCES package, 
  CONSTRAINT FOREIGN KEY (pcp_jou_id) REFERENCES journal,
  CONSTRAINT FOREIGN KEY (pcp_so_id) REFERENCES subscription_offered,
  CONSTRAINT FOREIGN KEY (pcp_plat_id) REFERENCES platform
);

create table document_organisation (
  do_doc_id int NOT NULL,
  do_org_id int NOT NULL,
  CONSTRAINT FOREIGN KEY (do_doc_id) REFERENCES document,
  CONSTRAINT FOREIGN KEY (do_org_id) REFERENCES organisation
);

create table document_package (
  do_doc_id int not null,
  do_pkg_id int not null,
  CONSTRAINT FOREIGN KEY (do_doc_id) REFERENCES document,
  CONSTRAINT FOREIGN KEY (do_pkg_id) REFERENCES package
);

create table document_subscription_offered (
  dso_doc_id int not null,
  dso_so_id int not null,
  CONSTRAINT FOREIGN KEY (dso_doc_id) REFERENCES document,
  CONSTRAINT FOREIGN KEY (dso_so_id) REFERENCES subscription_offered
);

create table title_instance (
  ti_id int NOT NULL auto_increment,
  ti_title varchar(255),
  ti_imp_id VARCHAR(36),
  CONSTRAINT PRIMARY KEY (ti_id)
);

create table title_instance_package_platform (
  tipp_title_id int NOT NULL,
  tipp_pkg_id int NOT NULL,
  tipp_plat_id int NOT NULL,
  tipp_imp_id VARCHAR(36),
  tipp_start_date VARCHAR(128),
  tipp_start_volume VARCHAR(128),
  tipp_start_issue VARCHAR(128),
  tipp_end_date VARCHAR(128),
  tipp_end_volume VARCHAR(128),
  tipp_end_issue VARCHAR(128),
  tipp_embargo VARCHAR(128),
  tipp_coverage_depth VARCHAR(128),
  tipp_coverage_note VARCHAR(128),
  CONSTRAINT PRIMARY KEY (tipp_title_id,ti_pkg_id,ti_plat_id),
  CONSTRAINT FOREIGN KEY (tipp_title_id) references title_instance,
  CONSTRAINT FOREIGN KEY (tipp_plat_id) references platform,
  CONSTRAINT FOREIGN KEY (tipp_pkg_id) references package
);

