#
#
# SQL to list all subscriptions offered
#
#
# We join to package for package details
# We join to org_role for org name where the role is content provider


select sub.*, pkg_name
from subscription sub
      join refdata_value on ( rdv_value = 'Subscription Offered' and rdv_id = sub.sub_type_rv_fk )
      join subscription_package sp on ( sub.sub_id = sp.sp_sub_fk )
        join package p on ( sp.sp_pkg_fk = p.pkg_id )

## Sub offered v2

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

# Stage 2 : Add in consortia, start date and end date
select pkg_name, cp_role.rdv_value, vendor_org.org_name, consortia_org.org_name, tipp_start_date, tipp_end_date
from title_instance_package_platform tipp
       left outer join package pkg on (  tipp.tipp_pkg_fk = pkg.pkg_id )
       left outer join org_role vendor_role on ( vendor_role.or_pkg_fk = pkg.pkg_id )
         join refdata_value cp_role on ( or_roletype_fk = cp_role.rdv_id AND cp_role.rdv_value = 'Content Provider' )
       left outer join org vendor_org on (  vendor_role.or_org_fk = vendor_org.org_id )
       left outer join combo consortia_combo on ( vendor_org.org_id = consortia_combo.from_org_id )
         join refdata_value cons_combo_type on ( consortia_combo.combo_type_rv_fk = cons_combo_type.rdv_id AND  cons_combo_type.rdv_value = 'Consortium')
         join org consortia_org on ( consortia_combo.to_org_id = consortia_org.org_id );

# Stage 3 : Add in platform
select pkg_name, cp_role.rdv_value, vendor_org.org_name, consortia_org.org_name, tipp_start_date, tipp_end_date, plat.plat_name
from title_instance_package_platform tipp
       left outer join package pkg on (  tipp.tipp_pkg_fk = pkg.pkg_id )
       left outer join org_role vendor_role on ( vendor_role.or_pkg_fk = pkg.pkg_id )
         join refdata_value cp_role on ( or_roletype_fk = cp_role.rdv_id AND cp_role.rdv_value = 'Content Provider' )
       left outer join org vendor_org on (  vendor_role.or_org_fk = vendor_org.org_id )
       left outer join combo consortia_combo on ( vendor_org.org_id = consortia_combo.from_org_id )
         join refdata_value cons_combo_type on ( consortia_combo.combo_type_rv_fk = cons_combo_type.rdv_id AND  cons_combo_type.rdv_value = 'Consortium')
         join org consortia_org on ( consortia_combo.to_org_id = consortia_org.org_id )
       left outer join platform plat on ( tipp.tipp_plat_fk = plat.plat_id )
    

# Add publisher
select pkg_name, vendor_org.org_name vendor, consortia_org.org_name consortia, tipp_start_date, tipp_end_date, plat.plat_name, publisher.org_name publisher
from title_instance_package_platform tipp
       join title_instance ti on ( ti.ti_id = tipp.tipp_ti_fk )
       left outer join package pkg on (  tipp.tipp_pkg_fk = pkg.pkg_id )
       left outer join org_role vendor_role on ( vendor_role.or_pkg_fk = pkg.pkg_id )
         join refdata_value cp_role on ( or_roletype_fk = cp_role.rdv_id AND cp_role.rdv_value = 'Content Provider' )
       left outer join org vendor_org on (  vendor_role.or_org_fk = vendor_org.org_id )
       left outer join combo consortia_combo on ( vendor_org.org_id = consortia_combo.from_org_id )
         join refdata_value cons_combo_type on ( consortia_combo.combo_type_rv_fk = cons_combo_type.rdv_id AND  cons_combo_type.rdv_value = 'Consortium')
         join org consortia_org on ( consortia_combo.to_org_id = consortia_org.org_id )
       left outer join platform plat on ( tipp.tipp_plat_fk = plat.plat_id )
       left outer join org_role publisher_role_link on ( publisher_role_link.or_title_fk = ti.ti_id )
         join refdata_value publisher_role on ( publisher_role_link.or_roletype_fk = publisher_role.rdv_id AND publisher_role.rdv_value = 'Publisher' )
         join org publisher on ( publisher_role_link.or_org_fk = publisher.org_id )
where vendor_org.org_name = 'Berg'

