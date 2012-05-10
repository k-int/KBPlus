#
#
# SQL to list all subscriptions offered
#
#
# We join to package for package details
# We join to org_role for org name where the role is content provider

# Building query up.. 1. Join to package and vendor
select pkg_name
from title_instance_package_platform tipp
       left outer join package pkg on (  tipp.tipp_pkg_fk = pkg.pkg_id )
       left outer join org_role vor on ( vor.or_pkg_fk = pkg.pkg_id )
       left outer join org vo on (  vor.or_org_fk = vo.org_id )


# Building query up.. 1. Join to package and vendor, restrict to just content providers
select pkg_name, cp_role.rdv_value, vo.org_name
from title_instance_package_platform tipp
       left outer join package pkg on (  tipp.tipp_pkg_fk = pkg.pkg_id )
       left outer join org_role vor on ( vor.or_pkg_fk = pkg.pkg_id )
         join refdata_value cp_role on ( or_roletype_fk = cp_role.rdv_id )
       left outer join org vo on (  vor.or_org_fk = vo.org_id )
where cp_role.rdv_value = 'Content Provider'

# Stage 2 : Add in consortia
