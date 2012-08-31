#
# Refdata
#

insert into refdata_category(rdc_id, rdc_description) VALUES (1,'Document Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 1, 'Model License');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 1, 'Subscription Offer Terms');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 1, 'Other');

insert into refdata_category(rdc_id, rdc_description) VALUES (2,'Organisational Role');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Aggregator');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Consortium');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Gateway');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Host');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Licensee');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Licensor');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Provider');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Publisher');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Subscriber');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 2, 'Subscription Agent');

insert into refdata_category(rdc_id, rdc_description) VALUES (3,'Platform Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 3, 'Host');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 3, 'Admininstrative');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 3, 'Software');

insert into refdata_category(rdc_id, rdc_description) VALUES (4,'Platform Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 4, 'Unknown');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 4, 'Expected');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 4, 'Current');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 4, 'Expired');

insert into refdata_category(rdc_id, rdc_description) VALUES (5,'Package Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 5, 'Unknown');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 5, 'Selective');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 5, 'Inclusive');

insert into refdata_category(rdc_id, rdc_description) VALUES (6,'Package Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 6, 'Unknown');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 6, 'Expired');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 6, 'Expected');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 6, 'Current');

insert into refdata_category(rdc_id, rdc_description) VALUES (7,'TIPP Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 7, 'Unknown');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 7, 'Current');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 7, 'Transferred');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 7, 'Expected');

insert into refdata_category(rdc_id, rdc_description) VALUES (8,'Title Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 8, 'Unknown');

insert into refdata_category(rdc_id, rdc_description) VALUES (9,'Title Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 9, 'Unknown');

insert into refdata_category(rdc_id, rdc_description) VALUES (10,'License Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 10, 'Template');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 10, 'Actual');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 10, 'Unknown');

insert into refdata_category(rdc_id, rdc_description) VALUES (11,'License Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Current');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Unknown');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Deleted');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Expired');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Under Negotiation');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Under Consideration');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 11, 'Awaiting Signature');

insert into refdata_category(rdc_id, rdc_description) VALUES (12,'Subscription Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 12, 'Subscription Taken');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 12, 'Subscription Offered');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 12, 'Unknown Subscription Type');

insert into refdata_category(rdc_id, rdc_description) VALUES (13,'Subscription Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Deleted');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Current');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Expired');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Terminated');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Under Negotiation');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Under Consideration');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 13, 'Pending');

insert into refdata_category(rdc_id, rdc_description) VALUES (14,'Entitlement Issue Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 14, 'Unknown Entitlement');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 14, 'Live');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 14, 'Deleted');

insert into refdata_category(rdc_id, rdc_description) VALUES (15,'Doc Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 17, 'Unknown Doc Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 17, 'Note');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 17, 'License Attachment');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 17, 'Addendum');

insert into refdata_category(rdc_id, rdc_description) VALUES (16,'Doc Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 16, 'Unknown Doc Status');

insert into refdata_category(rdc_id, rdc_description) VALUES (17,'TIPP Option');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 17, 'Unknown TIPP Option');

insert into refdata_category(rdc_id, rdc_description) VALUES (18,'Combo Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 28, 'Unknown Combo Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 18, 'MemberOfConsortium');

insert into refdata_category(rdc_id, rdc_description) VALUES (19,'Combo Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 19, 'Unknown Combo Status');

insert into refdata_category(rdc_id, rdc_description) VALUES (20,'YNO');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 20, 'Yes', 'greenTick');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 20, 'No', 'redCross');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 20, 'Other', 'purpleQuestion');


insert into refdata_category(rdc_id, rdc_description) VALUES (21,'Concurrent Access');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 21, 'Specified', 'greenTick');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 21, 'Not Specified', 'purpleQuestion');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 21, 'No limit', 'redCross');

insert into refdata_category(rdc_id, rdc_description) VALUES (22,'isCoreTitle');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 22, 'true', 'greenTick');
insert into refdata_value(rdv_owner, rdv_value, rdv_icon) VALUES ( 22, 'false', 'redCross');

insert into refdata_category(rdc_id, rdc_description) VALUES (23,'Link Types');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 23, 'Parent');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 23, 'Child');

insert into refdata_category(rdc_id, rdc_description) VALUES (24,'TIPP Type');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 24, 'Electronic');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 24, 'Print');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 24, 'Digitized');

insert into refdata_category(rdc_id, rdc_description) VALUES (25,'Package List Status');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 25, 'Checked');
insert into refdata_value(rdv_owner, rdv_value) VALUES ( 25, 'In progress');
