
# The two ends of the path
select u.username from user u where u.id = 1;
select s.sub_name from subscription s where s.sub_id = 1;

# List all the organisations this user is affiliated with
select o.org_name from org o, user_org uo, user u where u.id = 1 and uo.user_id = u.id and uo.org_id = o.org_id;


# A role (Property of the link between a user and a role) can have a number of permissions attached to it. For example,
# The role of editor comes with view and edit permissions.
# List all the orgs a user is attached to, and the permissions that flow with that 


select o.org_name, pg.perm_id from org o, user_org uo, user u, perm_grant pg where u.id = 1 and uo.user_id = u.id and uo.org_id = o.org_id and pg.role_id = uo.formal_role_id;


# Add in perm name
select o.org_name, p.code
from org o, 
     user_org uo, 
     user u, 
     perm_grant pg,
     perm p
where u.id = 1 
  and uo.user_id = u.id
  and uo.org_id = o.org_id 
  and pg.role_id = uo.formal_role_id
  and p.id = pg.perm_id;



# Some separate SQL - List all the permissions that are inherited through organisational links (EG My institution is a member of JISC Collections, and that means I can read anything that is owned by JC)

# Sub sql - All orgs linked to the users orgs
select o2.org_name
from org o1, 
     user_org uo,
     user u,
     combo c,
     org o2
where u.id = 1 
  and uo.user_id = u.id 
  and uo.org_id = o1.org_id 
  and c.combo_from_org_fk = o1.org_id
  and c.combo_to_org_fk = o2.org_id

# Sub sql2 - add in any perms granted by that combo type
select o2.org_name, p.code
from org o1, 
     user_org uo,
     user u,
     combo c,
     org o2,
     refdata_value rdv,
     org_perm_share ops,
     perm p
where u.id = 1 
  and uo.user_id = u.id 
  and uo.org_id = o1.org_id 
  and c.combo_from_org_fk = o1.org_id
  and c.combo_to_org_fk = o2.org_id
  and rdv.rdv_id = c.combo_type_rv_fk
  and ops.rdv_id = rdv.rdv_id
  and p.id = ops.perm_id

