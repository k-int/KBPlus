SET @issn_type_id = null, @eissn_type_id=null, @doi_type_id=null;;
select @issn_type_id:=idns_id from identifier_namespace where idns_ns='ISSN';
select @eissn_type_id:=idns_id from identifier_namespace where idns_ns='eISSN';
select @doi_type_id:=idns_id from identifier_namespace where idns_ns='DOI';

select ti.ti_id, ti_title
from title_instance as ti
where not exists ( select io.io_id 
                     from identifier_occurrence as io, 
                          identifier as id 
                   where io_ti_fk = ti.ti_id
                     and io.io_canonical_id = id_id
                      and id.id_ns_fk in ( @issn_type_id, @eissn_type_id, @doi_type_id ) );
