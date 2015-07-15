select 'Set up some MySQL Variables to hold the IDs of our custom property definitions';
SET @InstitutionTypeId = null,
    @AdmRoleId = null,
    @BorsetshireUniId = null,
    @JCNamespace = null,
    @FakeUniId = null;

select 'Get IDs of known custom properties';

select @InstitutionTypeId := rdv_id from refdata_value where rdv_value = 'Institution';
select @AdmRoleId := id from role where authority= 'INST_ADM';
select @JCNamespace := idns_id from identifier_namespace where idns_ns= 'JC';


insert into org(org_name,sector,org_shortcode,org_type_rv_fk,date_created,last_updated) values ( 'University of Borsetshire', 'Higher Education', 'uoborsetshire', @InstitutionTypeId, now(), now() );

select @BorsetshireUniId := org_id from org where org_name = 'University of Borsetshire';

insert into user_org(date_actioned,date_requested,org_id,role,status,user_id,formal_role_id)
select now(),now(),@BorsetshireUniId,'deprecated',1,id,@AdmRoleId from user
where id not in ( select user_id from user_org where org_id = @BorsetshireUniId);

insert into identifier(id_ns_fk,id_value) values(@JCNamespace,'__FakeUniJCID');

select @FakeUniId := id_id from identifier where id_value = '__FakeUniJCID' and id_ns_fk = @JCNamespace;

insert into identifier_occurrence ( io_canonical_id, io_org_fk ) values ( @FakeUniId, @BorsetshireUniId );
