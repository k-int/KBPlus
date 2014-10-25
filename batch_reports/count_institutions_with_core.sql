
select @subscriber_role:=rdv_id from refdata_value where rdv_value='Subscriber';

select rdv_value, ie.ie_id, sub_org.org_id, sub.sub_id
from issue_entitlement ie,
     subscription sub,
     org_role subscriber_org_role,
     org sub_org,
     refdata_value
where ie_subscription_fk = sub.sub_id
  and subscriber_org_role.or_sub_fk = sub.sub_id
  and subscriber_org_role.or_roletype_fk = @subscriber_role
  and sub_org.org_id = subscriber_org_role.or_org_fk
  and rdv_id = core_status_id
limit 10;

select count(ie.ie_id), sub_org.org_name, rdv_value
from issue_entitlement ie,
     subscription sub,
     org_role subscriber_org_role,
     org sub_org,
     refdata_value
where ie_subscription_fk = sub.sub_id
  and subscriber_org_role.or_sub_fk = sub.sub_id
  and subscriber_org_role.or_roletype_fk = @subscriber_role
  and sub_org.org_id = subscriber_org_role.or_org_fk
  and rdv_id = IFNULL(core_status_id,229)
group by sub_org.org_name, rdv_value;



select "All subscriptions with an IE whos core status is Yes, Print, Electronic, Print+Electronic";

select count(sub.sub_id), max(sub_org.org_name), sub_org.org_id
from subscription sub,
     org_role subscriber_org_role,
     org sub_org
where subscriber_org_role.or_sub_fk = sub.sub_id
  and subscriber_org_role.or_roletype_fk = @subscriber_role
  and sub_org.org_id = subscriber_org_role.or_org_fk
  and exists ( select * from issue_entitlement ie where ie_subscription_fk = sub.sub_id and ie.core_status_id in (103, 105, 106, 107 ) )
group by sub_org.org_id


select "IE Status summary"

select count(ie_id), max(rdv_value), ie.core_status_id
from issue_entitlement ie, refdata_value
where rdv_id = core_status_id
group by ie.core_status_id;
