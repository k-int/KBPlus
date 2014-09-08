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

select 'Get IDs of known custom properties'

select @AlumniAccessPropDefId := td_id from property_definition where td_name = 'Alumni Access';
select @ConcurrentAccessPropDefId := td_id from property_definition where td_name = 'Concurrent Access';
select @ConcurrentUsersPropDefId := td_id from property_definition where td_name = 'Concurrent Users';
select @EnterpriseAccessPropDefId := td_id from property_definition where td_name = 'Enterprise Access';
select @ILLPropDefId := td_id from property_definition where td_name = 'ILL - InterLibraryLoans';
select @IncludeInCoursepacksPropDefId := td_id from property_definition where td_name = 'Include In Coursepacks';
select @IncludeinVLEPropDefId := td_id from property_definition where td_name = 'Include in VLE';
select @MultiSiteAccessPropDefId := td_id from property_definition where td_name = 'Multi Site Access';
select @PartnersAccessPropDefId := td_id from property_definition where td_name = 'Partners Access';
select @PostCancellationAccessEntitlementPropDefId := td_id from property_definition where td_name = 'Post Cancellation Access Entitlement';
select @RemoteAccessPropDefId := td_id from property_definition where td_name = 'Remote Access';
select @SignedPropDefId := td_id from property_definition where td_name = 'Signed';
select @WalkInAccessPropDefId := td_id from property_definition where td_name = 'Walk In Access';


select 'Select the current concurrent access property and create new custom propertie values for each license';

insert into license_custom_property(version,owner_id, note, ref_value_id, type_id) 
as select 0, lic_id, doc_content, lic_concurrent_users_rdv_fk, @ConcurrentAccessPropDefId 
   from license
       left outer join doc_context on ( dc_lic_fk = lic_id and domain = 'concurrentUsers' )
         left outer join doc on doc_id = dc_doc_fk
