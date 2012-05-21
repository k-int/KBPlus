#
#
# SQL to list subscriptions taken
#
#


# Step 1 - to Find Kings college by its JC ID (506)

select org.*
from org
  join identifier_occurrence on ( org_id = io_org_fk )
    join identifier on ( io_canonical_id = id_id and id_value = 506 )
      join identifier_namespace on ( id_ns_fk = idns_id and idns_ns = 'JC' )


# Step 2 - List all the org_role links for cranfield

select org_role.*
from org
  join org_role on ( org_id = or_org_fk )
  join identifier_occurrence on ( org_id = io_org_fk )
    join identifier on ( io_canonical_id = id_id and id_value = 506 )
      join identifier_namespace on ( id_ns_fk = idns_id and idns_ns = 'JC' )

# Step 3 - List all the subscription records

select subscription.*
from subscription
  join org_role on ( sub_id = or_sub_fk )
    join org on ( or_org_fk = org_id )
      join identifier_occurrence on ( org_id = io_org_fk )
        join identifier on ( io_canonical_id = id_id and id_value = 506 )
          join identifier_namespace on ( id_ns_fk = idns_id and idns_ns = 'JC' )



# But please note, that currently, there are only the following ST records in the database

select org_name, sub_name
from org_role 
  join subscription on ( or_sub_fk = sub_id )
  join org on ( or_org_fk = org_id )
where or_org_fk is not null and or_sub_fk is not null

