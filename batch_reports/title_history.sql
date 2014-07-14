SET @issn_type_id = null, @eissn_type_id=null, @doi_type_id=null;;
select @issn_type_id:=idns_id from identifier_namespace where idns_ns='ISSN';
select @eissn_type_id:=idns_id from identifier_namespace where idns_ns='eISSN';
select @doi_type_id:=idns_id from identifier_namespace where idns_ns='DOI';

select tipp_id, 
       tipp_coverage_note,
       issn_identifier.id_value ISSN,
       eissn_identifier.id_value eISSN,
       doi_identifier.id_value DOI
from title_instance_package_platform as tipp
     left join identifier_occurrence as issn_io on issn_io.io_tipp_fk = tipp.tipp_id
       left join identifier as issn_identifier on issn_io.io_canonical_id = issn_identifier.id_id and issn_identifier.id_ns_fk = @issn_type_id
     left join identifier_occurrence as eissn_io  on eissn_io.io_tipp_fk = tipp.tipp_id
       left join identifier as eissn_identifier on eissn_io.io_canonical_id = eissn_identifier.id_id  and eissn_identifier.id_ns_fk = @eiisn_type_id
     left join identifier_occurrence as doi_io on doi_io.io_tipp_fk = tipp.tipp_id
       left join identifier as doi_identifier on doi_io.io_canonical_id =  doi_identifier.id_id and doi_identifier.id_ns_fk = @doi_type_id
where tipp_coverage_note is not null 
limit 10;

