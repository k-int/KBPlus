
SET @juspnamespace = null, @subscriber_role=null, @jusplogin=null;;
select @subscriber_role:=rdv_id from refdata_value where rdv_value='Subscriber';
select @jusplogin :=idns_id from identifier_namespace where idns_ns='jusplogin';
select @jusp:=idns_id from identifier_namespace where idns_ns='jusp';

select cs_rdv.rdv_value, ie.core_status_start, ie.core_status_end, title_identifier.id_value, org_identifier.id_value
from issue_entitlement ie,
     title_instance_package_platform tipp,
     refdata_value cs_rdv,
     subscription sub,
     org_role subscriber_org_role,
     org sub_org,
     identifier_occurrence org_identifiers,
     identifier_occurrence title_identifiers,
     identifier title_identifier,
     identifier org_identifier
where ie.core_status_id = cs_rdv.rdv_id
  and ie.ie_tipp_fk = tipp.tipp_id
  and ie_subscription_fk = sub.sub_id
  and subscriber_org_role.or_sub_fk = sub.sub_id
  and subscriber_org_role.or_roletype_fk = @subscriber_role
  and sub_org.org_id = subscriber_org_role.or_org_fk
  and org_identifiers.io_org_fk = sub_org.org_id
  and title_identifiers.io_ti_fk = tipp.tipp_ti_fk
  and title_identifier.id_id = title_identifiers.io_canonical_id
  and org_identifier.id_id = org_identifiers.io_canonical_id
  and title_identifier.id_ns_fk = @jusp 
  and org_identifier.id_ns_fk = @jusplogin;

