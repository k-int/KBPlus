select 'List licenses, concurrentUsers value and any associated note'

select lic_id, lic_concurrent_users_rdv_fk, doc_content, dc_doc_fk, dc_id
from license 
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'concurrentUsers' )
         left outer join doc on doc_id = dc_doc_fk
limit 10;

