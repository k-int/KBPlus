select 'Set up some MySQL Variables to hold the IDs of our custom property definitions';
SET @AlumniAccessPropDefId = null,
    @ConcurrentAccessPropDefId = null,
    @ConcurrentUsersPropDefId = null,
    @EnterpriseAccessPropDefId = null,
    @ILLPropDefId = null,
    @IncludeInCoursepacksPropDefId = null,
    @IncludeinVLEPropDefId = null,
    @MultiSiteAccessPropDefId = null,
    @PartnersAccessPropDefId = null,
    @PostCancellationAccessEntitlementPropDefId = null,
    @RemoteAccessPropDefId = null,
    @SignedPropDefId = null,
    @WalkInAccessPropDefId = null;

select 'Get IDs of known custom properties';

select @AlumniAccessPropDefId := pd_id from property_definition where pd_name = 'Alumni Access';
select @ConcurrentAccessPropDefId := pd_id from property_definition where pd_name = 'Concurrent Access';
select @ConcurrentUsersPropDefId := pd_id from property_definition where pd_name = 'Concurrent Users';
select @EnterpriseAccessPropDefId := pd_id from property_definition where pd_name = 'Enterprise Access';
select @ILLPropDefId := pd_id from property_definition where pd_name = 'ILL - InterLibraryLoans';
select @IncludeInCoursepacksPropDefId := pd_id from property_definition where pd_name = 'Include In Coursepacks';
select @IncludeinVLEPropDefId := pd_id from property_definition where pd_name = 'Include in VLE';
select @MultiSiteAccessPropDefId := pd_id from property_definition where pd_name = 'Multi Site Access';
select @PartnersAccessPropDefId := pd_id from property_definition where pd_name = 'Partners Access';
select @PostCancellationAccessEntitlementPropDefId := pd_id from property_definition where pd_name = 'Post Cancellation Access Entitlement';
select @RemoteAccessPropDefId := pd_id from property_definition where pd_name = 'Remote Access';
select @SignedPropDefId := pd_id from property_definition where pd_name = 'Signed';
select @WalkInAccessPropDefId := pd_id from property_definition where pd_name = 'Walk In Access';


select 'Select the current concurrent access property and create new custom propertie values for each license';

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_concurrent_users_rdv_fk, @ConcurrentAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'concurrentUsers' )
         left outer join doc on doc_id = dc_doc_fk;


insert into license_custom_property(version,owner_id, note, int_value, type_id) 
 select 0, lic_id, doc_content, lic_concurrent_user_count, @ConcurrentUsersPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'concurrentUsers' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_alumni_access_rdv_fk, @AlumniAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'alumniAccess' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_enterprise_rdv_fk, @EnterpriseAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'enterprise' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_ill_rdv_fk, @ILLPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'ill' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_coursepack_rdv_fk, @IncludeInCoursepacksPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'coursepack' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_vle_rdv_fk, @IncludeinVLEPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'vle' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_multisite_access_rdv_fk, @MultiSiteAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'multisiteAccess' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_partners_access_rdv_fk, @PartnersAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'partnersAccess' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_pca_rdv_fk, @PostCancellationAccessEntitlementPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'pca' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_remote_access_rdv_fk, @RemoteAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'remoteAccess' )
         left outer join doc on doc_id = dc_doc_fk;

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
 select 0, lic_id, doc_content, lic_walkin_access_rdv_fk, @WalkInAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'walkinAccess' )
         left outer join doc on doc_id = dc_doc_fk;
