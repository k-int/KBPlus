SET @issn_type_id = null, @eissn_type_id=null, @doi_type_id=null, @e_issn2_type_id = null, @jusp_type_id=null;

select @issn_type_id:=idns_id from identifier_namespace where idns_ns='ISSN';
select @eissn_type_id:=idns_id from identifier_namespace where idns_ns='eISSN';
select @doi_type_id:=idns_id from identifier_namespace where idns_ns='DOI';
select @e_issn2_type_id:=idns_id from identifier_namespace where idns_ns='e-issn';
select @jusp_type_id:=idns_id from identifier_namespace where idns_ns='jusp';
select @publisher:=rdv_id from refdata_value where rdv_value='Publisher';

select pkg.pkg_id,
       title.ti_id identifier_kbplus,
       title.ti_title title,
       max(issn_identifier.id_value) identifier_isbn,
       coalesce(max(eissn_identifier.id_value), max(eissn_2_identifier.id_value)) identifier_issn,
       max(jusp_identifier.id_value) identifier_jusp
from   title_instance_package_platform as tipp
         join package as pkg on pkg.pkg_id = tipp.tipp_pkg_fk
         join title_instance as title on title.ti_id = tipp.tipp_ti_fk
           left outer join identifier_occurrence as io on io.io_ti_fk = title.ti_id
             left outer join identifier as issn_identifier on io.io_canonical_id = issn_identifier.id_id and issn_identifier.id_ns_fk = @issn_type_id
             left outer join identifier as eissn_identifier on io.io_canonical_id = eissn_identifier.id_id  and eissn_identifier.id_ns_fk = @eissn_type_id
             left outer join identifier as doi_identifier on io.io_canonical_id =  doi_identifier.id_id and doi_identifier.id_ns_fk = @doi_type_id
             left outer join identifier as eissn_2_identifier on io.io_canonical_id =  eissn_2_identifier.id_id and eissn_2_identifier.id_ns_fk = @e_issn2_type_id
             left outer join identifier as jusp_identifier on io.io_canonical_id =  jusp_identifier.id_id and jusp_identifier.id_ns_fk = @jusp_type_id
           left outer join org_role as orl  on orl.or_title_fk = title.ti_id and or_roletype_fk = @publisher
             left outer join org as org  on org.org_id = orl.or_org_fk
where 
     pkg.pkg_id = 511
group by title.ti_id
