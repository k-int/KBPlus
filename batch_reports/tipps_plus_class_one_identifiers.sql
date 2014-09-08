SET @issn_type_id = null, @eissn_type_id=null, @doi_type_id=null, @e_issn2_type_id = null;
select @issn_type_id:=idns_id from identifier_namespace where idns_ns='ISSN';
select @eissn_type_id:=idns_id from identifier_namespace where idns_ns='eISSN';
select @doi_type_id:=idns_id from identifier_namespace where idns_ns='DOI';
select @e_issn2_type_id:=idns_id from identifier_namespace where idns_ns='e-issn';


select title.ti_id,
       tipp.tipp_coverage_note, 
       max(issn_identifier.id_value) ISBN, 
       max(eissn_identifier.id_value) EISSN, 
       max(doi_identifier.id_value) DOI, 
       max(eissn_2_identifier.id_value) EISSN2
from title_instance_package_platform as tipp
     join title_instance as title on title.ti_id = tipp.tipp_ti_fk
       left outer join identifier_occurrence as io on io.io_ti_fk = title.ti_id
         left outer join identifier as issn_identifier on io.io_canonical_id = issn_identifier.id_id and issn_identifier.id_ns_fk = @issn_type_id
         left outer join identifier as eissn_identifier on io.io_canonical_id = eissn_identifier.id_id  and eissn_identifier.id_ns_fk = @eiisn_type_id
         left outer join identifier as doi_identifier on io.io_canonical_id =  doi_identifier.id_id and doi_identifier.id_ns_fk = @doi_type_id
         left outer join identifier as eissn_2_identifier on io.io_canonical_id =  eissn_2_identifier.id_id and eissn_2_identifier.id_ns_fk = @e_issn2_type_id
where tipp_coverage_note is not null
      and ( 
         tipp.tipp_coverage_note like 'Formerly%'
      or tipp.tipp_coverage_note like '%nclude%'
      or tipp.tipp_coverage_note like 'Now%'
      or tipp.tipp_coverage_note like 'now%'
      or tipp.tipp_coverage_note like '%split%'
      or tipp.tipp_coverage_note like 'Merged%'
      or tipp.tipp_coverage_note like 'merged%'
      or tipp.tipp_coverage_note like 'Absorbed%'
      or tipp.tipp_coverage_note like 'Original title of%'
      or tipp.tipp_coverage_note like 'incorporated%' )
group by title.ti_id
limit 10




select tipp_id, 
       tipp_coverage_note,
       issn_identifier.id_value ISSN,
       eissn_identifier.id_value eISSN,
       doi_identifier.id_value DOI
from title_instance_package_platform as tipp
     join title_instance as title on title.ti_id = tipp.tipp_ti_fk
       left join identifier_occurrence as issn_io on issn_io.io_ti_fk = title.ti_id
         left join identifier as issn_identifier on issn_io.io_canonical_id = issn_identifier.id_id and issn_identifier.id_ns_fk = @issn_type_id
       left join identifier_occurrence as eissn_io  on eissn_io.io_ti_fk = title.ti_id
         left join identifier as eissn_identifier on eissn_io.io_canonical_id = eissn_identifier.id_id  and eissn_identifier.id_ns_fk = @eiisn_type_id
       left join identifier_occurrence as doi_io on doi_io.io_ti_fk = title.ti_id
         left join identifier as doi_identifier on doi_io.io_canonical_id =  doi_identifier.id_id and doi_identifier.id_ns_fk = @doi_type_id
where tipp_coverage_note is not null 
      and tipp.tipp_coverage_note <> 'OA'
      and not tipp.tipp_coverage_note like ''
      and not tipp.tipp_coverage_note like 'Issues not yet available' and not tipp.tipp_coverage_note like 'start year not confirmed' and not tipp.tipp_coverage_note like '3%'
      and not tipp.tipp_coverage_note like '1/1/%' and not tipp.tipp_coverage_note like '2011%'
      and not tipp.tipp_coverage_note like '%Years' and not tipp.tipp_coverage_note like '%years' and not tipp.tipp_coverage_note like '%upgrade%' and not tipp.tipp_coverage_note like 'Subscribers%'
      and not tipp.tipp_coverage_note like '%year' and not tipp.tipp_coverage_note like 'only%' and not tipp.tipp_coverage_note like 'new for%'
      and not tipp.tipp_coverage_note like 'This is a test%'
      and not tipp.tipp_coverage_note like 'open access%' and not tipp.tipp_coverage_note like 'Open access%'
      and not tipp.tipp_coverage_note like 'Open Access%' and not tipp.tipp_coverage_note like 'Fully Open Access'
      and not tipp.tipp_coverage_note like 'full text'
      and not tipp.tipp_coverage_note like 'Access under the terms%'
      and not tipp.tipp_coverage_note like 'This title was in%'
      and not tipp.tipp_coverage_note like 'New for%'
      and not tipp.tipp_coverage_note like 'archival%' and not tipp.tipp_coverage_note like 'no content%' and not tipp.tipp_coverage_note like 'New to%' and not tipp.tipp_coverage_note like 'Includ%'
      and not tipp.tipp_coverage_note like '%take-over%' and not tipp.tipp_coverage_note like '%akeover%' and not tipp.tipp_coverage_note like 'Content missing%'
      and not tipp.tipp_coverage_note like 'free back issues%'
      and not tipp.tipp_coverage_note like 'previous%' and not tipp.tipp_coverage_note like 'until 2013 in BioOne%'
      and not tipp.tipp_coverage_note regexp '[tT]ransfer.*'
      and not tipp.tipp_coverage_note like '%JSTOR.'
      and not tipp.tipp_coverage_note like '%also includes%'
      and not tipp.tipp_coverage_note like 'Beginning%'
      and not tipp.tipp_coverage_note like 'Supplement%'
      and not tipp.tipp_coverage_note like 'Continues%'
      and not tipp.tipp_coverage_note like 'Currently%'
      and not tipp.tipp_coverage_note like 'Incorporates%'
      and not tipp.tipp_coverage_note like 'From%'
      and not tipp.tipp_coverage_note regexp '[Cc]eased.*'
      and not tipp.tipp_coverage_note like 'was%'
      and not tipp.tipp_coverage_note regexp '[Pp]reviou.*'
      and not tipp.tipp_coverage_note like 'Formerly%'
      and not tipp.tipp_coverage_note like '%nclude%'
      and not tipp.tipp_coverage_note like 'Now%'
      and not tipp.tipp_coverage_note like 'now%'
      and not tipp.tipp_coverage_note like '%split%'
      and not tipp.tipp_coverage_note like 'Merged%'
      and not tipp.tipp_coverage_note like 'merged%'
      and not tipp.tipp_coverage_note like 'Absorbed%'
      and not tipp.tipp_coverage_note like 'Original title of%'
      and not tipp.tipp_coverage_note like 'incorporated%'
      and not tipp.tipp_coverage_note like '%come as a package%'
limit 10



and  ( tipp_coverage_note like ('%previously%') or 
       tipp_coverage_note like ('%later%') )
       tipp_coverage_note like ('%Including%') )
limit 10;

