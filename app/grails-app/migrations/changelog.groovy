databaseChangeLog = {

	changeSet(author: "ibbo (generated)", id: "1435152601456-1") {
		createTable(tableName: "alert") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "al_create_time", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "al_user_fk", type: "BIGINT")

			column(name: "al_org_fk", type: "BIGINT")

			column(name: "al_sharing_level", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-2") {
		createTable(tableName: "annotation") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "component_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "property_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "LONGTEXT")

			column(name: "view_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-3") {
		createTable(tableName: "audit_log") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "actor", type: "VARCHAR(255)")

			column(name: "class_name", type: "VARCHAR(255)")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "event_name", type: "VARCHAR(255)")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "new_value", type: "VARCHAR(255)")

			column(name: "old_value", type: "VARCHAR(255)")

			column(name: "persisted_object_id", type: "VARCHAR(255)")

			column(name: "persisted_object_version", type: "BIGINT")

			column(name: "property_name", type: "VARCHAR(255)")

			column(name: "uri", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-4") {
		createTable(tableName: "change_notification_queue_item") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "cnqi_change_document", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "cnqi_oid", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "cnqi_ts", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-5") {
		createTable(tableName: "combo") {
			column(autoIncrement: "true", name: "combo_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "combo_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "combo_from_org_fk", type: "BIGINT")

			column(name: "combo_status_rv_fk", type: "BIGINT")

			column(name: "combo_to_org_fk", type: "BIGINT")

			column(name: "combo_type_rv_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-6") {
		createTable(tableName: "comment") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-7") {
		createTable(tableName: "content_block") {
			column(autoIncrement: "true", name: "cb_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "cb_content", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "cb_blk_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "locale", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-8") {
		createTable(tableName: "content_item") {
			column(autoIncrement: "true", name: "ci_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ci_content", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "ci_key", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "ci_locale", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-9") {
		createTable(tableName: "core_assertion") {
			column(autoIncrement: "true", name: "ca_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "ca_ver", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ca_end_date", type: "DATETIME")

			column(name: "ca_start_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ca_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-10") {
		createTable(tableName: "cost_item") {
			column(autoIncrement: "true", name: "ci_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "ci_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ci_billing_currency_rv_fk", type: "BIGINT")

			column(name: "ci_cost_description", type: "VARCHAR(255)")

			column(name: "ci_cost_in_billing_currency", type: "DOUBLE")

			column(name: "ci_cost_in_local_currency", type: "DOUBLE")

			column(name: "ci_cat_rv_fk", type: "BIGINT")

			column(name: "ci_element_rv_fk", type: "BIGINT")

			column(name: "ci_status_rv_fk", type: "BIGINT")

			column(name: "ci_type_rv_fk", type: "BIGINT")

			column(name: "ci_date_paid", type: "DATETIME")

			column(name: "ci_cig_fk", type: "BIGINT")

			column(name: "ci_include_in_subscr", type: "BIT")

			column(name: "ci_inv_fk", type: "BIGINT")

			column(name: "ci_e_fk", type: "BIGINT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ci_local_fund_code", type: "VARCHAR(255)")

			column(name: "ci_ord_fk", type: "BIGINT")

			column(name: "ci_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ci_reference", type: "VARCHAR(255)")

			column(name: "ci_sub_fk", type: "BIGINT")

			column(name: "ci_subPkg_fk", type: "BIGINT")

			column(name: "ci_tax_code", type: "BIGINT")

			column(name: "ci_end_date", type: "DATETIME")

			column(name: "ci_start_date", type: "DATETIME")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-11") {
		createTable(tableName: "cost_item_group") {
			column(autoIncrement: "true", name: "cig_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "cig_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "cig_budgetcode_fk", type: "BIGINT")

			column(name: "cig_costItem_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-12") {
		createTable(tableName: "dataload_file_instance") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "upload_timestamp", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-13") {
		createTable(tableName: "dataload_file_type") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "fl_ft_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-14") {
		createTable(tableName: "doc") {
			column(autoIncrement: "true", name: "doc_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "doc_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "doc_alert_fk", type: "BIGINT")

			column(name: "doc_content", type: "LONGTEXT")

			column(name: "doc_content_type", type: "INT")

			column(name: "doc_creator", type: "VARCHAR(255)")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "doc_filename", type: "VARCHAR(255)")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "doc_mimeType", type: "VARCHAR(255)")

			column(name: "doc_status_rv_fk", type: "BIGINT")

			column(name: "doc_title", type: "VARCHAR(255)")

			column(name: "doc_type_rv_fk", type: "BIGINT")

			column(name: "doc_user_fk", type: "BIGINT")

			column(name: "doc_docstore_uuid", type: "VARCHAR(255)")

			column(name: "doc_blob_content", type: "LONGBLOB")

			column(name: "migrated", type: "VARCHAR(1)")

			column(name: "doc_migrated", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-15") {
		createTable(tableName: "doc_context") {
			column(autoIncrement: "true", name: "dc_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "dc_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "dc_alert_fk", type: "BIGINT")

			column(name: "dc_rv_doctype_fk", type: "BIGINT")

			column(name: "domain", type: "VARCHAR(255)")

			column(name: "dc_lic_fk", type: "BIGINT")

			column(name: "dc_doc_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "dc_status_fk", type: "BIGINT")

			column(name: "dc_sub_fk", type: "BIGINT")

			column(name: "dc_pkg_fk", type: "BIGINT")

			column(name: "dc_is_global", type: "BIT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-16") {
		createTable(tableName: "event_log") {
			column(autoIncrement: "true", name: "el_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "el_event", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "el_msg", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "el_tstp", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-17") {
		createTable(tableName: "folder_item") {
			column(autoIncrement: "true", name: "fi_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "fi_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "folder_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "fi_ref_oid", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "items_idx", type: "INT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-18") {
		createTable(tableName: "ftcontrol") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "activity", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "domain_class_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_timestamp", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-19") {
		createTable(tableName: "global_record_info") {
			column(autoIncrement: "true", name: "gri_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "gri_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "gri_desc", type: "VARCHAR(255)")

			column(name: "gri_identifier", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "gri_kbplus_compliant", type: "BIGINT")

			column(name: "gri_name", type: "VARCHAR(255)")

			column(name: "gri_record", type: "LONGBLOB") {
				constraints(nullable: "false")
			}

			column(name: "gri_rectype", type: "BIGINT")

			column(name: "gri_source_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "gri_timestamp", type: "DATETIME")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-20") {
		createTable(tableName: "global_record_source") {
			column(autoIncrement: "true", name: "grs_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "grs_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "grs_active", type: "BIT")

			column(name: "grs_creds", type: "VARCHAR(255)")

			column(name: "grs_full_prefix", type: "VARCHAR(255)")

			column(name: "grs_have_up_to", type: "DATETIME")

			column(name: "grs_identifier", type: "VARCHAR(255)")

			column(name: "grs_list_prefix", type: "VARCHAR(255)")

			column(name: "grs_name", type: "VARCHAR(255)")

			column(name: "grs_principal", type: "VARCHAR(255)")

			column(name: "grs_rectype", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "grs_type", type: "VARCHAR(255)")

			column(name: "grs_uri", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-21") {
		createTable(tableName: "global_record_tracker") {
			column(autoIncrement: "true", name: "grt_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "grt_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "grt_auto_pkg_update", type: "BIT")

			column(name: "grt_auto_tipp_add", type: "BIT")

			column(name: "grt_auto_tipp_del", type: "BIT")

			column(name: "grt_auto_tipp_update", type: "BIT")

			column(name: "grt_identifier", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "grt_local_oid", type: "VARCHAR(255)")

			column(name: "grt_name", type: "VARCHAR(255)")

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-22") {
		createTable(tableName: "identifier") {
			column(autoIncrement: "true", name: "id_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "id_ns_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "id_value", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-23") {
		createTable(tableName: "identifier_namespace") {
			column(autoIncrement: "true", name: "idns_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "idns_ns", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "idns_hide", type: "BIT")

			column(name: "idns_type_fl", type: "BIGINT")

			column(name: "idns_val_regex", type: "VARCHAR(255)")

			column(name: "idns_family", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-24") {
		createTable(tableName: "identifier_occurrence") {
			column(autoIncrement: "true", name: "io_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "io_canonical_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "io_org_fk", type: "BIGINT")

			column(name: "io_ti_fk", type: "BIGINT")

			column(name: "io_tipp_fk", type: "BIGINT")

			column(name: "io_pkg_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-25") {
		createTable(tableName: "identifier_relation") {
			column(autoIncrement: "true", name: "ir_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ir_from_id_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ir_rel_rdv_id_fk", type: "BIGINT")

			column(name: "ir_to_id_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-26") {
		createTable(tableName: "invoice") {
			column(autoIncrement: "true", name: "inv_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "inv_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "inv_date_of_invoice", type: "DATETIME")

			column(name: "inv_date_of_payment", type: "DATETIME")

			column(name: "inv_date_passed_to_finance", type: "DATETIME")

			column(name: "inv_number", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "inv_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-27") {
		createTable(tableName: "issue_entitlement") {
			column(autoIncrement: "true", name: "ie_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "ie_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ie_core_title", type: "BIT")

			column(name: "ie_coverage_depth", type: "VARCHAR(255)")

			column(name: "ie_coverage_note", type: "LONGTEXT")

			column(name: "ie_embargo", type: "VARCHAR(255)")

			column(name: "ie_end_date", type: "DATETIME")

			column(name: "ie_end_issue", type: "VARCHAR(255)")

			column(name: "ie_end_volume", type: "VARCHAR(255)")

			column(name: "ie_reason", type: "VARCHAR(255)")

			column(name: "ie_start_date", type: "DATETIME")

			column(name: "ie_start_issue", type: "VARCHAR(255)")

			column(name: "ie_start_volume", type: "VARCHAR(255)")

			column(name: "ie_status_rv_fk", type: "BIGINT")

			column(name: "ie_subscription_fk", type: "BIGINT")

			column(name: "ie_tipp_fk", type: "BIGINT")

			column(name: "core_status_id", type: "BIGINT")

			column(name: "core_status_end", type: "DATETIME")

			column(name: "core_status_start", type: "DATETIME")

			column(name: "ie_access_end_date", type: "DATETIME")

			column(name: "ie_access_start_date", type: "DATETIME")

			column(name: "ie_medium_rv_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-28") {
		createTable(tableName: "jasper_report_file") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "report_file", type: "MEDIUMBLOB") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-29") {
		createTable(tableName: "jusp_triple_cursor") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "have_up_to", type: "VARCHAR(32)") {
				constraints(nullable: "false")
			}

			column(name: "jusp_login_id", type: "VARCHAR(32)") {
				constraints(nullable: "false")
			}

			column(name: "jusp_supplier_id", type: "VARCHAR(32)") {
				constraints(nullable: "false")
			}

			column(name: "jusp_title_id", type: "VARCHAR(32)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-30") {
		createTable(tableName: "kb_comment") {
			column(autoIncrement: "true", name: "comm_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comm_alert_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comm_by_user_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comm_text", type: "TEXT")

			column(name: "comm_date", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-31") {
		createTable(tableName: "kbplus_fact") {
			column(autoIncrement: "true", name: "fact_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "fact_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "fact_from", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "fact_to", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "fact_type_rdv_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "fact_uid", type: "VARCHAR(255)")

			column(name: "fact_value", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "inst_id", type: "BIGINT")

			column(name: "juspio_id", type: "BIGINT")

			column(name: "related_title_id", type: "BIGINT")

			column(name: "reporting_month", type: "BIGINT")

			column(name: "reporting_year", type: "BIGINT")

			column(name: "supplier_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-32") {
		createTable(tableName: "kbplus_ord") {
			column(autoIncrement: "true", name: "ord_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "ord_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ord_number", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "ord_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-33") {
		createTable(tableName: "license") {
			column(autoIncrement: "true", name: "lic_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "lic_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "lic_alumni_access_rdv_fk", type: "BIGINT")

			column(name: "lic_concurrent_user_count", type: "BIGINT")

			column(name: "lic_concurrent_users_rdv_fk", type: "BIGINT")

			column(name: "lic_coursepack_rdv_fk", type: "BIGINT")

			column(name: "lic_enterprise_rdv_fk", type: "BIGINT")

			column(name: "lic_ill_rdv_fk", type: "BIGINT")

			column(name: "lic_lastmod", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "lic_license_status_str", type: "VARCHAR(255)")

			column(name: "lic_license_type_str", type: "VARCHAR(255)")

			column(name: "lic_license_url", type: "VARCHAR(255)")

			column(name: "lic_licensee_ref", type: "VARCHAR(255)")

			column(name: "lic_licensor_ref", type: "VARCHAR(255)")

			column(name: "lic_multisite_access_rdv_fk", type: "BIGINT")

			column(name: "lic_notice_period", type: "VARCHAR(255)")

			column(name: "lic_partners_access_rdv_fk", type: "BIGINT")

			column(name: "lic_pca_rdv_fk", type: "BIGINT")

			column(name: "lic_ref", type: "VARCHAR(255)")

			column(name: "lic_remote_access_rdv_fk", type: "BIGINT")

			column(name: "lic_status_rv_fk", type: "BIGINT")

			column(name: "lic_type_rv_fk", type: "BIGINT")

			column(name: "lic_vle_rdv_fk", type: "BIGINT")

			column(name: "lic_walkin_access_rdv_fk", type: "BIGINT")

			column(name: "lic_is_public_rdv_fk", type: "BIGINT")

			column(name: "lic_opl_fk", type: "BIGINT")

			column(name: "lic_category_rdv_fk", type: "BIGINT")

			column(name: "lic_end_date", type: "DATETIME")

			column(name: "lic_start_date", type: "DATETIME")

			column(name: "lic_sortable_ref", type: "VARCHAR(255)")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "imp_id", type: "VARCHAR(255)")

			column(name: "last_updated", type: "DATETIME")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-34") {
		createTable(tableName: "license_custom_property") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "dec_value", type: "DECIMAL(19,2)")

			column(name: "int_value", type: "INT")

			column(name: "note", type: "LONGTEXT")

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ref_value_id", type: "BIGINT")

			column(name: "string_value", type: "VARCHAR(255)")

			column(name: "type_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-35") {
		createTable(tableName: "link") {
			column(autoIncrement: "true", name: "link_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "link_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "link_from_lic_fk", type: "BIGINT")

			column(name: "link_status_rv_fk", type: "BIGINT")

			column(name: "link_to_lic_fk", type: "BIGINT")

			column(name: "link_type_rv_fk", type: "BIGINT")

			column(name: "link_is_slaved", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-36") {
		createTable(tableName: "object_definition") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-37") {
		createTable(tableName: "object_property") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "prop_type", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-38") {
		createTable(tableName: "onixpl_license") {
			column(autoIncrement: "true", name: "opl_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "opl_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "opl_doc_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "opl_lastmod", type: "DATETIME")

			column(name: "opl_title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-39") {
		createTable(tableName: "onixpl_license_text") {
			column(autoIncrement: "true", name: "oplt_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "oplt_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "oplt_display_num", type: "VARCHAR(255)")

			column(name: "oplt_el_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "oplt_opl_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "oplt_text", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "term_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-40") {
		createTable(tableName: "onixpl_usage_term") {
			column(autoIncrement: "true", name: "oput_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "oput_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "oput_opl_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "oput_usage_status_rv_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "oput_usage_type_rv_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-41") {
		createTable(tableName: "onixpl_usage_term_license_text") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "opul_oplt_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "opul_oput_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-42") {
		createTable(tableName: "onixpl_usage_term_refdata_value") {
			column(name: "onixpl_usage_term_used_resource_id", type: "BIGINT")

			column(name: "refdata_value_id", type: "BIGINT")

			column(name: "onixpl_usage_term_user_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-43") {
		createTable(tableName: "org") {
			column(autoIncrement: "true", name: "org_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "org_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "org_address", type: "VARCHAR(256)")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "org_imp_id", type: "VARCHAR(255)")

			column(name: "org_ip_range", type: "VARCHAR(1024)")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "org_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "org_scope", type: "VARCHAR(128)")

			column(name: "sector", type: "VARCHAR(128)")

			column(name: "org_shortcode", type: "VARCHAR(128)")

			column(name: "org_cat", type: "VARCHAR(128)")

			column(name: "org_type_rv_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-44") {
		createTable(tableName: "org_perm_share") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "perm_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "rdv_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-45") {
		createTable(tableName: "org_role") {
			column(autoIncrement: "true", name: "or_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "or_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "or_lic_fk", type: "BIGINT")

			column(name: "or_org_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "or_pkg_fk", type: "BIGINT")

			column(name: "or_roletype_fk", type: "BIGINT")

			column(name: "or_sub_fk", type: "BIGINT")

			column(name: "or_title_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-46") {
		createTable(tableName: "org_roles_to_delete") {
			column(name: "or_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-47") {
		createTable(tableName: "org_title_instance") {
			column(autoIncrement: "true", name: "orgtitle_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "orgtitle_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "is_core", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "orgtitle_org", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "orgtitle_title", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-48") {
		createTable(tableName: "org_title_stats") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "last_retrieved_timestamp", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "org_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-49") {
		createTable(tableName: "package") {
			column(autoIncrement: "true", name: "pkg_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "pkg_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "content_provider_id", type: "BIGINT")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "pkg_identifier", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "pkg_imp_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "pkg_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "pkg_nominal_platform_fk", type: "BIGINT")

			column(name: "pkg_list_status_rv_fk", type: "BIGINT")

			column(name: "pkg_status_rv_fk", type: "BIGINT")

			column(name: "pkg_type_rv_fk", type: "BIGINT")

			column(name: "pkg_end_date", type: "DATETIME")

			column(name: "pkg_is_public", type: "BIGINT")

			column(name: "pkg_license_fk", type: "BIGINT")

			column(name: "pkg_start_date", type: "DATETIME")

			column(name: "pkg_forum_id", type: "VARCHAR(255)")

			column(name: "pkg_breakable_rv_fk", type: "BIGINT")

			column(name: "pkg_consistent_rv_fk", type: "BIGINT")

			column(name: "pkg_fixed_rv_fk", type: "BIGINT")

			column(name: "pkg_scope_rv_fk", type: "BIGINT")

			column(name: "auto_accept", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "pkg_cancellation_allowances", type: "LONGTEXT")

			column(name: "pkg_sort_name", type: "VARCHAR(255)")

			column(name: "pkg_vendor_url", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-50") {
		createTable(tableName: "pending_change") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "pc_change_doc", type: "LONGTEXT")

			column(name: "pc_desc", type: "LONGTEXT")

			column(name: "pc_lic_fk", type: "BIGINT")

			column(name: "pc_oid", type: "VARCHAR(255)")

			column(name: "pc_owner", type: "BIGINT")

			column(name: "pc_sub_fk", type: "BIGINT")

			column(name: "pc_sys_obj", type: "BIGINT")

			column(name: "pc_ts", type: "DATETIME")

			column(name: "pc_action_date", type: "DATETIME")

			column(name: "pc_status_rdv_fk", type: "BIGINT")

			column(name: "pc_action_user_fk", type: "BIGINT")

			column(name: "pc_pkg_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-51") {
		createTable(tableName: "perm") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-52") {
		createTable(tableName: "perm_grant") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "perm_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-53") {
		createTable(tableName: "platform") {
			column(autoIncrement: "true", name: "plat_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "plat_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "plat_imp_id", type: "VARCHAR(255)")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "plat_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "plat_normalised_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "plat_primary_url", type: "VARCHAR(255)")

			column(name: "plat_data_provenance", type: "VARCHAR(255)")

			column(name: "plat_status_rv_fk", type: "BIGINT")

			column(name: "plat_type_rv_fk", type: "BIGINT")

			column(name: "plat_servprov_rv_fk", type: "BIGINT")

			column(name: "plat_softprov_rv_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-54") {
		createTable(tableName: "platformtipp") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "platform_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "rel", type: "VARCHAR(255)")

			column(name: "tipp_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title_url", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-55") {
		createTable(tableName: "property_definition") {
			column(autoIncrement: "true", name: "pd_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "pd_description", type: "VARCHAR(255)")

			column(name: "pd_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "pd_rdc", type: "VARCHAR(255)")

			column(name: "pd_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-56") {
		createTable(tableName: "property_value") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-57") {
		createTable(tableName: "refdata_category") {
			column(autoIncrement: "true", name: "rdc_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "rdc_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "rdc_description", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-58") {
		createTable(tableName: "refdata_value") {
			column(autoIncrement: "true", name: "rdv_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "rdv_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "rdv_icon", type: "VARCHAR(255)")

			column(name: "rdv_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "rdv_value", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "rdv_group", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-59") {
		createTable(tableName: "registration_code") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-60") {
		createTable(tableName: "role") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "role_type", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-61") {
		createTable(tableName: "role_info_110712") {
			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-62") {
		createTable(tableName: "s2") {
			column(defaultValueNumeric: "0", name: "sub_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "sub_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "sub_end_date", type: "DATETIME")

			column(name: "sub_identifier", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_imp_id", type: "VARCHAR(255)")

			column(name: "sub_parent_sub_fk", type: "BIGINT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "sub_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_notice_period", type: "VARCHAR(255)")

			column(name: "sub_owner_license_fk", type: "BIGINT")

			column(name: "sub_start_date", type: "DATETIME")

			column(name: "sub_status_rv_fk", type: "BIGINT")

			column(name: "sub_type_rv_fk", type: "BIGINT")

			column(name: "sub_is_public", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-63") {
		createTable(tableName: "safe_core_assertion") {
			column(defaultValueNumeric: "0", name: "ca_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ca_ver", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ca_end_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ca_start_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ca_owner", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-64") {
		createTable(tableName: "setting") {
			column(autoIncrement: "true", name: "set_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "set_defvalue", type: "VARCHAR(1024)")

			column(name: "set_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "set_type", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "set_value", type: "VARCHAR(1024)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-65") {
		createTable(tableName: "site_page") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "alias", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "controller", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "rectype", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-66") {
		createTable(tableName: "site_page_user") {
			column(name: "site_page_users_id", type: "BIGINT")

			column(name: "user_id", type: "BIGINT")

			column(name: "users_idx", type: "INT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-67") {
		createTable(tableName: "subscription") {
			column(autoIncrement: "true", name: "sub_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "sub_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "sub_end_date", type: "DATETIME")

			column(name: "sub_identifier", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_imp_id", type: "VARCHAR(255)")

			column(name: "sub_parent_sub_fk", type: "BIGINT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "sub_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_notice_period", type: "VARCHAR(255)")

			column(name: "sub_owner_license_fk", type: "BIGINT")

			column(name: "sub_start_date", type: "DATETIME")

			column(name: "sub_status_rv_fk", type: "BIGINT")

			column(name: "sub_type_rv_fk", type: "BIGINT")

			column(name: "sub_is_public", type: "BIGINT")

			column(name: "cancellation_allowances", type: "VARCHAR(255)")

			column(name: "sub_is_slaved", type: "BIGINT")

			column(name: "sub_manual_renewal_date", type: "DATETIME")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-68") {
		createTable(tableName: "subscription_custom_property") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "dec_value", type: "DECIMAL(19,2)")

			column(name: "int_value", type: "INT")

			column(name: "note", type: "LONGTEXT")

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ref_value_id", type: "BIGINT")

			column(name: "string_value", type: "VARCHAR(255)")

			column(name: "type_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-69") {
		createTable(tableName: "subscription_package") {
			column(autoIncrement: "true", name: "sp_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "sp_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "sp_pkg_fk", type: "BIGINT")

			column(name: "sp_sub_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-70") {
		createTable(tableName: "system_object") {
			column(autoIncrement: "true", name: "sys_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "sys_ann_forum_id", type: "VARCHAR(255)")

			column(name: "sys_id_str", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-71") {
		createTable(tableName: "title_history_event") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "event_date", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-72") {
		createTable(tableName: "title_history_event_participant") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "event_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "participant_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "participant_role", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-73") {
		createTable(tableName: "title_instance") {
			column(autoIncrement: "true", name: "ti_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "ti_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ti_imp_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "ti_status_rv_fk", type: "BIGINT")

			column(name: "ti_title", type: "VARCHAR(1024)")

			column(name: "ti_type_rv_fk", type: "BIGINT")

			column(name: "ti_key_title", type: "VARCHAR(1024)")

			column(name: "ti_norm_title", type: "VARCHAR(1024)")

			column(name: "sort_title", type: "VARCHAR(1024)")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-74") {
		createTable(tableName: "title_instance_package_platform") {
			column(autoIncrement: "true", name: "tipp_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "tipp_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tipp_coverage_depth", type: "VARCHAR(255)")

			column(name: "tipp_coverage_note", type: "LONGTEXT")

			column(name: "tipp_derived_from", type: "BIGINT")

			column(name: "tipp_embargo", type: "VARCHAR(255)")

			column(name: "tipp_end_date", type: "DATETIME")

			column(name: "tipp_end_issue", type: "VARCHAR(255)")

			column(name: "tipp_end_volume", type: "VARCHAR(255)")

			column(name: "tipp_host_platform_url", type: "VARCHAR(255)")

			column(name: "tipp_imp_id", type: "VARCHAR(255)")

			column(name: "tipp_option_rv_fk", type: "BIGINT")

			column(name: "tipp_pkg_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tipp_plat_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tipp_rectype", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "tipp_start_date", type: "DATETIME")

			column(name: "tipp_start_issue", type: "VARCHAR(255)")

			column(name: "tipp_start_volume", type: "VARCHAR(255)")

			column(name: "tipp_status_rv_fk", type: "BIGINT")

			column(name: "tipp_sub_fk", type: "BIGINT")

			column(name: "tipp_ti_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tipp_core_status_end_date", type: "DATETIME")

			column(name: "tipp_core_status_start_date", type: "DATETIME")

			column(name: "tipp_delayedoa_rv_fk", type: "BIGINT")

			column(name: "tipp_hybridoa_rv_fk", type: "BIGINT")

			column(name: "tipp_payment_rv_fk", type: "BIGINT")

			column(name: "tipp_status_reason_rv_fk", type: "BIGINT")

			column(name: "tipp_access_end_date", type: "DATETIME")

			column(name: "tipp_access_start_date", type: "DATETIME")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-75") {
		createTable(tableName: "title_institution_provider") {
			column(autoIncrement: "true", name: "tiinp_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "title_inst_prov_ver", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tttnp_inst_org_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tttnp_prov_org_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tttnp_title", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-76") {
		createTable(tableName: "titles_to_remove") {
			column(defaultValueNumeric: "0", name: "ti_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-77") {
		createTable(tableName: "transformer") {
			column(name: "tfmr_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tfmr_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "tfmr_url", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-78") {
		createTable(tableName: "transforms") {
			column(name: "tr_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tr_accepts_format_rv_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tr_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "tr_path_to_stylesheet", type: "VARCHAR(255)")

			column(name: "tr_return_file_extention", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "tr_return_mime", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "tr_transformer_fk", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-79") {
		createTable(tableName: "transforms_refdata_value") {
			column(name: "transforms_accepts_types_id", type: "BIGINT")

			column(name: "refdata_value_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-80") {
		createTable(tableName: "type_definition") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "type_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-81") {
		createTable(tableName: "user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "display", type: "VARCHAR(255)")

			column(name: "email", type: "VARCHAR(255)")

			column(name: "enabled", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "instcode", type: "VARCHAR(255)")

			column(name: "instname", type: "VARCHAR(255)")

			column(name: "password", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "shibb_scope", type: "VARCHAR(255)")

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "default_dash_id", type: "BIGINT")

			column(name: "default_page_size", type: "BIGINT")

			column(name: "apikey", type: "VARCHAR(255)")

			column(name: "apisecret", type: "VARCHAR(255)")

			column(name: "show_info_icon_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-82") {
		createTable(tableName: "user_folder") {
			column(autoIncrement: "true", name: "uf_id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "uf_version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "uf_name", type: "VARCHAR(255)")

			column(name: "uf_shortcode", type: "VARCHAR(255)")

			column(name: "uf_owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-83") {
		createTable(tableName: "user_info_110712") {
			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "org_shortcode", type: "VARCHAR(128)")

			column(name: "role", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-84") {
		createTable(tableName: "user_org") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_actioned", type: "BIGINT")

			column(name: "date_requested", type: "BIGINT")

			column(name: "org_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "formal_role_id", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-85") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-86") {
		createTable(tableName: "user_transforms") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "ut_transforms_fk", type: "BIGINT")

			column(name: "ut_user_fk", type: "BIGINT")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-87") {
		createTable(tableName: "webhook") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "attempts", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "format", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_modified", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "service", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "url", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-88") {
		addPrimaryKey(columnNames: "role_id, user_id", tableName: "user_role")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-258") {
		createIndex(indexName: "FK18538F206CBF4", tableName: "doc", unique: "false") {
			column(name: "doc_migrated")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-259") {
		createIndex(indexName: "doc_uuid_idx", tableName: "doc", unique: "false") {
			column(name: "doc_docstore_uuid")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-260") {
		createIndex(indexName: "id_value_idx", tableName: "identifier", unique: "false") {
			column(name: "id_ns_fk")

			column(name: "id_value")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-261") {
		createIndex(indexName: "jusp_cursor_idx", tableName: "jusp_triple_cursor", unique: "false") {
			column(name: "jusp_login_id")

			column(name: "jusp_supplier_id")

			column(name: "jusp_title_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-262") {
		createIndex(indexName: "FK6784767A4CB39BA6", tableName: "kbplus_fact", unique: "false") {
			column(name: "related_title_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-263") {
		createIndex(indexName: "fact_access_idx", tableName: "kbplus_fact", unique: "false") {
			column(name: "inst_id")

			column(name: "related_title_id")

			column(name: "supplier_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-264") {
		createIndex(indexName: "fact_uid_idx", tableName: "kbplus_fact", unique: "false") {
			column(name: "fact_uid")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-265") {
		createIndex(indexName: "FKF1E88AC0EF8C4AB4", tableName: "onixpl_license_text", unique: "false") {
			column(name: "term_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-266") {
		createIndex(indexName: "oplt_el_id_idx", tableName: "onixpl_license_text", unique: "false") {
			column(name: "oplt_el_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-267") {
		createIndex(indexName: "oput_entry_idx", tableName: "onixpl_usage_term", unique: "false") {
			column(name: "oput_opl_fk")

			column(name: "oput_usage_status_rv_fk")

			column(name: "oput_usage_type_rv_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-268") {
		createIndex(indexName: "opul_entry_idx", tableName: "onixpl_usage_term_license_text", unique: "false") {
			column(name: "opul_oplt_fk")

			column(name: "opul_oput_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-269") {
		createIndex(indexName: "org_imp_id_idx", tableName: "org", unique: "false") {
			column(name: "org_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-270") {
		createIndex(indexName: "org_name_idx", tableName: "org", unique: "false") {
			column(name: "org_name")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-271") {
		createIndex(indexName: "org_shortcode_idx", tableName: "org", unique: "false") {
			column(name: "org_shortcode")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-272") {
		createIndex(indexName: "or_org_rt_idx", tableName: "org_role", unique: "false") {
			column(name: "or_org_fk")

			column(name: "or_roletype_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-273") {
		createIndex(indexName: "FKCFE53446EB68C0AA", tableName: "package", unique: "false") {
			column(name: "content_provider_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-274") {
		createIndex(indexName: "pkg_imp_id_idx", tableName: "package", unique: "false") {
			column(name: "pkg_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-275") {
		createIndex(indexName: "pending_change_oid_idx", tableName: "pending_change", unique: "false") {
			column(name: "pc_oid")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-276") {
		createIndex(indexName: "code", tableName: "perm", unique: "true") {
			column(name: "code")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-277") {
		createIndex(indexName: "plat_imp_id_idx", tableName: "platform", unique: "false") {
			column(name: "plat_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-278") {
		createIndex(indexName: "td_name_idx", tableName: "property_definition", unique: "false") {
			column(name: "pd_name")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-279") {
		createIndex(indexName: "td_type_idx", tableName: "property_definition", unique: "false") {
			column(name: "pd_rdc")

			column(name: "pd_type")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-280") {
		createIndex(indexName: "rdc_description_idx", tableName: "refdata_category", unique: "false") {
			column(name: "rdc_description")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-281") {
		createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value", unique: "false") {
			column(name: "rdv_owner")

			column(name: "rdv_value")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-282") {
		createIndex(indexName: "authority", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-283") {
		createIndex(indexName: "alias", tableName: "site_page", unique: "true") {
			column(name: "alias")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-284") {
		createIndex(indexName: "sub_imp_id_idx", tableName: "subscription", unique: "false") {
			column(name: "sub_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-285") {
		createIndex(indexName: "ti_imp_id_idx", tableName: "title_instance", unique: "false") {
			column(name: "ti_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-286") {
		createIndex(indexName: "tipp_idx", tableName: "title_instance_package_platform", unique: "false") {
			column(name: "tipp_pkg_fk")

			column(name: "tipp_plat_fk")

			column(name: "tipp_ti_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-287") {
		createIndex(indexName: "tipp_imp_id_idx", tableName: "title_instance_package_platform", unique: "false") {
			column(name: "tipp_imp_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-288") {
		createIndex(indexName: "tiinp_idx", tableName: "title_institution_provider", unique: "false") {
			column(name: "tttnp_inst_org_fk")

			column(name: "tttnp_prov_org_fk")

			column(name: "tttnp_title")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-289") {
		createIndex(indexName: "username", tableName: "user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-290") {
		createIndex(indexName: "FK143BF46A3761CEC3", tableName: "user_role", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-291") {
		createIndex(indexName: "FKE8A3AF7B988474CD", tableName: "user_transforms", unique: "false") {
			column(name: "ut_user_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-292") {
		createIndex(indexName: "FKE8A3AF7BD8A576A1", tableName: "user_transforms", unique: "false") {
			column(name: "ut_transforms_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-293") {
		createIndex(indexName: "ut_transforms_id__idxfk", tableName: "user_transforms", unique: "false") {
			column(name: "ut_transforms_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-294") {
		createIndex(indexName: "ut_user_id_idxfk_2", tableName: "user_transforms", unique: "false") {
			column(name: "ut_user_fk")
		}
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-89") {
		addForeignKeyConstraint(baseColumnNames: "al_org_fk", baseTableName: "alert", baseTableSchemaName: "KBPlus", constraintName: "FK589895CC431E3DB", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-90") {
		addForeignKeyConstraint(baseColumnNames: "al_user_fk", baseTableName: "alert", baseTableSchemaName: "KBPlus", constraintName: "FK589895CE0A42659", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-91") {
		addForeignKeyConstraint(baseColumnNames: "combo_from_org_fk", baseTableName: "combo", baseTableSchemaName: "KBPlus", constraintName: "FK5A7318E62A4664B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-92") {
		addForeignKeyConstraint(baseColumnNames: "combo_status_rv_fk", baseTableName: "combo", baseTableSchemaName: "KBPlus", constraintName: "FK5A7318E69CBC6D5", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-93") {
		addForeignKeyConstraint(baseColumnNames: "combo_to_org_fk", baseTableName: "combo", baseTableSchemaName: "KBPlus", constraintName: "FK5A7318E6223BA1A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-94") {
		addForeignKeyConstraint(baseColumnNames: "combo_type_rv_fk", baseTableName: "combo", baseTableSchemaName: "KBPlus", constraintName: "FK5A7318EF93C805D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-95") {
		addForeignKeyConstraint(baseColumnNames: "ca_owner", baseTableName: "core_assertion", baseTableSchemaName: "KBPlus", constraintName: "FKE48406625AD1EB60", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tiinp_id", referencedTableName: "title_institution_provider", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-96") {
		addForeignKeyConstraint(baseColumnNames: "ci_billing_currency_rv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C4585DC4AE0", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-97") {
		addForeignKeyConstraint(baseColumnNames: "ci_cat_rv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45FE188C0F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-98") {
		addForeignKeyConstraint(baseColumnNames: "ci_cig_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C457934977E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "cig_id", referencedTableName: "cost_item_group", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-99") {
		addForeignKeyConstraint(baseColumnNames: "ci_e_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C455C9F1829", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ie_id", referencedTableName: "issue_entitlement", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-100") {
		addForeignKeyConstraint(baseColumnNames: "ci_element_rv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45BC27AB35", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-101") {
		addForeignKeyConstraint(baseColumnNames: "ci_inv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45E55745DC", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "inv_id", referencedTableName: "invoice", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-102") {
		addForeignKeyConstraint(baseColumnNames: "ci_ord_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C4590AA9CD", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ord_id", referencedTableName: "kbplus_ord", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-103") {
		addForeignKeyConstraint(baseColumnNames: "ci_owner", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45E4E73EE1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-104") {
		addForeignKeyConstraint(baseColumnNames: "ci_status_rv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C456F474AFD", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-105") {
		addForeignKeyConstraint(baseColumnNames: "ci_sub_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45FFAD2337", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-106") {
		addForeignKeyConstraint(baseColumnNames: "ci_subPkg_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45D820FE8B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sp_id", referencedTableName: "subscription_package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-107") {
		addForeignKeyConstraint(baseColumnNames: "ci_tax_code", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C45B30B076B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-108") {
		addForeignKeyConstraint(baseColumnNames: "ci_type_rv_fk", baseTableName: "cost_item", baseTableSchemaName: "KBPlus", constraintName: "FKEFE45C455C9AEE85", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-109") {
		addForeignKeyConstraint(baseColumnNames: "cig_budgetcode_fk", baseTableName: "cost_item_group", baseTableSchemaName: "KBPlus", constraintName: "FK1C3AAE05B522EC25", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-110") {
		addForeignKeyConstraint(baseColumnNames: "cig_costItem_fk", baseTableName: "cost_item_group", baseTableSchemaName: "KBPlus", constraintName: "FK1C3AAE051D1B4283", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ci_id", referencedTableName: "cost_item", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-111") {
		addForeignKeyConstraint(baseColumnNames: "doc_alert_fk", baseTableName: "doc", baseTableSchemaName: "KBPlus", constraintName: "FK185381FC434AE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "alert", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-112") {
		addForeignKeyConstraint(baseColumnNames: "doc_status_rv_fk", baseTableName: "doc", baseTableSchemaName: "KBPlus", constraintName: "FK185387758376B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-113") {
		addForeignKeyConstraint(baseColumnNames: "doc_type_rv_fk", baseTableName: "doc", baseTableSchemaName: "KBPlus", constraintName: "FK1853897381E73", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-114") {
		addForeignKeyConstraint(baseColumnNames: "doc_user_fk", baseTableName: "doc", baseTableSchemaName: "KBPlus", constraintName: "FK185382F964266", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-115") {
		addForeignKeyConstraint(baseColumnNames: "dc_alert_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A8B88BED47", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "alert", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-116") {
		addForeignKeyConstraint(baseColumnNames: "dc_doc_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A8C7230C87", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "doc_id", referencedTableName: "doc", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-117") {
		addForeignKeyConstraint(baseColumnNames: "dc_lic_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A8A43E5A02", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-118") {
		addForeignKeyConstraint(baseColumnNames: "dc_pkg_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A871246D01", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-119") {
		addForeignKeyConstraint(baseColumnNames: "dc_rv_doctype_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A858752A7E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-120") {
		addForeignKeyConstraint(baseColumnNames: "dc_status_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A8B9538E23", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-121") {
		addForeignKeyConstraint(baseColumnNames: "dc_sub_fk", baseTableName: "doc_context", baseTableSchemaName: "KBPlus", constraintName: "FK30EBA9A824AA84FE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-122") {
		addForeignKeyConstraint(baseColumnNames: "folder_id", baseTableName: "folder_item", baseTableSchemaName: "KBPlus", constraintName: "FK695AC446D580EE2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "uf_id", referencedTableName: "user_folder", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-123") {
		addForeignKeyConstraint(baseColumnNames: "gri_kbplus_compliant", baseTableName: "global_record_info", baseTableSchemaName: "KBPlus", constraintName: "FKB057C1402753393F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-124") {
		addForeignKeyConstraint(baseColumnNames: "gri_source_fk", baseTableName: "global_record_info", baseTableSchemaName: "KBPlus", constraintName: "FKB057C140E1AE5394", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "grs_id", referencedTableName: "global_record_source", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-125") {
		addForeignKeyConstraint(baseColumnNames: "owner_id", baseTableName: "global_record_tracker", baseTableSchemaName: "KBPlus", constraintName: "FK808F5966D92AE946", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "gri_id", referencedTableName: "global_record_info", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-126") {
		addForeignKeyConstraint(baseColumnNames: "id_ns_fk", baseTableName: "identifier", baseTableSchemaName: "KBPlus", constraintName: "FK9F88ACA9F1F42470", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-127") {
		addForeignKeyConstraint(baseColumnNames: "idns_type_fl", baseTableName: "identifier_namespace", baseTableSchemaName: "KBPlus", constraintName: "FKBA7FFD4534D73C7D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-128") {
		addForeignKeyConstraint(baseColumnNames: "io_canonical_id", baseTableName: "identifier_occurrence", baseTableSchemaName: "KBPlus", constraintName: "FKF0533F279B08D7A5", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id_id", referencedTableName: "identifier", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-129") {
		addForeignKeyConstraint(baseColumnNames: "io_org_fk", baseTableName: "identifier_occurrence", baseTableSchemaName: "KBPlus", constraintName: "FKF0533F279DF8E280", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-130") {
		addForeignKeyConstraint(baseColumnNames: "io_pkg_fk", baseTableName: "identifier_occurrence", baseTableSchemaName: "KBPlus", constraintName: "FKF0533F273508B27A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-131") {
		addForeignKeyConstraint(baseColumnNames: "io_ti_fk", baseTableName: "identifier_occurrence", baseTableSchemaName: "KBPlus", constraintName: "FKF0533F27B37DC426", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-132") {
		addForeignKeyConstraint(baseColumnNames: "io_tipp_fk", baseTableName: "identifier_occurrence", baseTableSchemaName: "KBPlus", constraintName: "FKF0533F27CDDD0AFF", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-133") {
		addForeignKeyConstraint(baseColumnNames: "ir_from_id_fk", baseTableName: "identifier_relation", baseTableSchemaName: "KBPlus", constraintName: "FKDA4DACD2BB32F750", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id_id", referencedTableName: "identifier", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-134") {
		addForeignKeyConstraint(baseColumnNames: "ir_rel_rdv_id_fk", baseTableName: "identifier_relation", baseTableSchemaName: "KBPlus", constraintName: "FKDA4DACD2AD4573E3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-135") {
		addForeignKeyConstraint(baseColumnNames: "ir_to_id_fk", baseTableName: "identifier_relation", baseTableSchemaName: "KBPlus", constraintName: "FKDA4DACD22ED80AE1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id_id", referencedTableName: "identifier", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-136") {
		addForeignKeyConstraint(baseColumnNames: "inv_owner", baseTableName: "invoice", baseTableSchemaName: "KBPlus", constraintName: "FK74D6432DDB56B62C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-137") {
		addForeignKeyConstraint(baseColumnNames: "core_status_id", baseTableName: "issue_entitlement", baseTableSchemaName: "KBPlus", constraintName: "FK2D45F6C71268C999", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-138") {
		addForeignKeyConstraint(baseColumnNames: "ie_medium_rv_fk", baseTableName: "issue_entitlement", baseTableSchemaName: "KBPlus", constraintName: "FK2D45F6C75BEA734A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-139") {
		addForeignKeyConstraint(baseColumnNames: "ie_status_rv_fk", baseTableName: "issue_entitlement", baseTableSchemaName: "KBPlus", constraintName: "FK2D45F6C72F4A207", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-140") {
		addForeignKeyConstraint(baseColumnNames: "ie_subscription_fk", baseTableName: "issue_entitlement", baseTableSchemaName: "KBPlus", constraintName: "FK2D45F6C775F8181E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-141") {
		addForeignKeyConstraint(baseColumnNames: "ie_tipp_fk", baseTableName: "issue_entitlement", baseTableSchemaName: "KBPlus", constraintName: "FK2D45F6C7330B4F5", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-142") {
		addForeignKeyConstraint(baseColumnNames: "comm_alert_fk", baseTableName: "kb_comment", baseTableSchemaName: "KBPlus", constraintName: "FKA21A5B771226D95A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "alert", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-143") {
		addForeignKeyConstraint(baseColumnNames: "comm_by_user_fk", baseTableName: "kb_comment", baseTableSchemaName: "KBPlus", constraintName: "FKA21A5B7720B74198", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-144") {
		addForeignKeyConstraint(baseColumnNames: "ord_owner", baseTableName: "kbplus_ord", baseTableSchemaName: "KBPlus", constraintName: "FK6EB1D5133C45D91C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-145") {
		addForeignKeyConstraint(baseColumnNames: "lic_alumni_access_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441B6A1F9E4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-146") {
		addForeignKeyConstraint(baseColumnNames: "lic_category_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F0844110F7C2B9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-147") {
		addForeignKeyConstraint(baseColumnNames: "lic_concurrent_users_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441595820F7", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-148") {
		addForeignKeyConstraint(baseColumnNames: "lic_coursepack_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F0844177A5D483", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-149") {
		addForeignKeyConstraint(baseColumnNames: "lic_enterprise_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F084416C7CF136", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-150") {
		addForeignKeyConstraint(baseColumnNames: "lic_ill_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F084414B2F78C0", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-151") {
		addForeignKeyConstraint(baseColumnNames: "lic_is_public_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F084413D2ACEB", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-152") {
		addForeignKeyConstraint(baseColumnNames: "lic_multisite_access_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F0844119B9B694", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-153") {
		addForeignKeyConstraint(baseColumnNames: "lic_opl_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441F5A55C6C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "opl_id", referencedTableName: "onixpl_license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-154") {
		addForeignKeyConstraint(baseColumnNames: "lic_partners_access_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441D4BECC91", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-155") {
		addForeignKeyConstraint(baseColumnNames: "lic_pca_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F084414C1CBBFB", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-156") {
		addForeignKeyConstraint(baseColumnNames: "lic_remote_access_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441D77ABF2C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-157") {
		addForeignKeyConstraint(baseColumnNames: "lic_status_rv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F0844168C2A8DD", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-158") {
		addForeignKeyConstraint(baseColumnNames: "lic_type_rv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441F144465", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-159") {
		addForeignKeyConstraint(baseColumnNames: "lic_vle_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F08441DBC1FD3A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-160") {
		addForeignKeyConstraint(baseColumnNames: "lic_walkin_access_rdv_fk", baseTableName: "license", baseTableSchemaName: "KBPlus", constraintName: "FK9F0844197334194", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-161") {
		addForeignKeyConstraint(baseColumnNames: "owner_id", baseTableName: "license_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FKE8DF0AE590DECB4B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-162") {
		addForeignKeyConstraint(baseColumnNames: "ref_value_id", baseTableName: "license_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FKE8DF0AE52992A286", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-163") {
		addForeignKeyConstraint(baseColumnNames: "type_id", baseTableName: "license_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FKE8DF0AE5D4223E79", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pd_id", referencedTableName: "property_definition", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-164") {
		addForeignKeyConstraint(baseColumnNames: "link_from_lic_fk", baseTableName: "link", baseTableSchemaName: "KBPlus", constraintName: "FK32AFFACB535FB2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-165") {
		addForeignKeyConstraint(baseColumnNames: "link_is_slaved", baseTableName: "link", baseTableSchemaName: "KBPlus", constraintName: "FK32AFFA2C22D5CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-166") {
		addForeignKeyConstraint(baseColumnNames: "link_status_rv_fk", baseTableName: "link", baseTableSchemaName: "KBPlus", constraintName: "FK32AFFA9BEE17E9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-167") {
		addForeignKeyConstraint(baseColumnNames: "link_to_lic_fk", baseTableName: "link", baseTableSchemaName: "KBPlus", constraintName: "FK32AFFA158EDE81", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-168") {
		addForeignKeyConstraint(baseColumnNames: "link_type_rv_fk", baseTableName: "link", baseTableSchemaName: "KBPlus", constraintName: "FK32AFFA38280671", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-169") {
		addForeignKeyConstraint(baseColumnNames: "opl_doc_fk", baseTableName: "onixpl_license", baseTableSchemaName: "KBPlus", constraintName: "FK620852CC4D1AE0DB", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "doc_id", referencedTableName: "doc", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-170") {
		addForeignKeyConstraint(baseColumnNames: "oplt_opl_fk", baseTableName: "onixpl_license_text", baseTableSchemaName: "KBPlus", constraintName: "FKF1E88AC0F8B5EA69", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "opl_id", referencedTableName: "onixpl_license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-171") {
		addForeignKeyConstraint(baseColumnNames: "oput_opl_fk", baseTableName: "onixpl_usage_term", baseTableSchemaName: "KBPlus", constraintName: "FK11797DDF2F1DD172", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "opl_id", referencedTableName: "onixpl_license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-172") {
		addForeignKeyConstraint(baseColumnNames: "oput_usage_status_rv_fk", baseTableName: "onixpl_usage_term", baseTableSchemaName: "KBPlus", constraintName: "FK11797DDFE9F8A801", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-173") {
		addForeignKeyConstraint(baseColumnNames: "oput_usage_type_rv_fk", baseTableName: "onixpl_usage_term", baseTableSchemaName: "KBPlus", constraintName: "FK11797DDFCF47BC89", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-174") {
		addForeignKeyConstraint(baseColumnNames: "opul_oplt_fk", baseTableName: "onixpl_usage_term_license_text", baseTableSchemaName: "KBPlus", constraintName: "FKC989A8CB55D5F69B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "oplt_id", referencedTableName: "onixpl_license_text", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-175") {
		addForeignKeyConstraint(baseColumnNames: "opul_oput_fk", baseTableName: "onixpl_usage_term_license_text", baseTableSchemaName: "KBPlus", constraintName: "FKC989A8CB3313FD03", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "oput_id", referencedTableName: "onixpl_usage_term", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-176") {
		addForeignKeyConstraint(baseColumnNames: "onixpl_usage_term_used_resource_id", baseTableName: "onixpl_usage_term_refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKB744770F7B0024B0", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "oput_id", referencedTableName: "onixpl_usage_term", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-177") {
		addForeignKeyConstraint(baseColumnNames: "onixpl_usage_term_user_id", baseTableName: "onixpl_usage_term_refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKB744770FE27A4895", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "oput_id", referencedTableName: "onixpl_usage_term", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-178") {
		addForeignKeyConstraint(baseColumnNames: "refdata_value_id", baseTableName: "onixpl_usage_term_refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKB744770FAAD0839C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-179") {
		addForeignKeyConstraint(baseColumnNames: "org_type_rv_fk", baseTableName: "org", baseTableSchemaName: "KBPlus", constraintName: "FK1AEE4360F7147", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-180") {
		addForeignKeyConstraint(baseColumnNames: "perm_id", baseTableName: "org_perm_share", baseTableSchemaName: "KBPlus", constraintName: "FK4EEB620B17B140A3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "perm", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-181") {
		addForeignKeyConstraint(baseColumnNames: "rdv_id", baseTableName: "org_perm_share", baseTableSchemaName: "KBPlus", constraintName: "FK4EEB620BBFE25067", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-182") {
		addForeignKeyConstraint(baseColumnNames: "or_lic_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F11960C01E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-183") {
		addForeignKeyConstraint(baseColumnNames: "or_org_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F14F370323", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-184") {
		addForeignKeyConstraint(baseColumnNames: "or_pkg_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F1E646D31D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-185") {
		addForeignKeyConstraint(baseColumnNames: "or_roletype_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F1879D5409", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-186") {
		addForeignKeyConstraint(baseColumnNames: "or_sub_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F199CCEB1A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-187") {
		addForeignKeyConstraint(baseColumnNames: "or_title_fk", baseTableName: "org_role", baseTableSchemaName: "KBPlus", constraintName: "FK4E5C38F16D6B9898", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-188") {
		addForeignKeyConstraint(baseColumnNames: "orgtitle_org", baseTableName: "org_title_instance", baseTableSchemaName: "KBPlus", constraintName: "FK41AF6157CC172760", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-189") {
		addForeignKeyConstraint(baseColumnNames: "orgtitle_title", baseTableName: "org_title_instance", baseTableSchemaName: "KBPlus", constraintName: "FK41AF6157F0E2D5FD", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-190") {
		addForeignKeyConstraint(baseColumnNames: "org_id", baseTableName: "org_title_stats", baseTableSchemaName: "KBPlus", constraintName: "FK8FC8FF1D21D4E99D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-191") {
		addForeignKeyConstraint(baseColumnNames: "title_id", baseTableName: "org_title_stats", baseTableSchemaName: "KBPlus", constraintName: "FK8FC8FF1D10288612", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-192") {
		addForeignKeyConstraint(baseColumnNames: "pkg_breakable_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE534462B6E84F8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-193") {
		addForeignKeyConstraint(baseColumnNames: "pkg_consistent_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE534462CA44477", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-194") {
		addForeignKeyConstraint(baseColumnNames: "pkg_fixed_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE53446D4A9C3D3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-195") {
		addForeignKeyConstraint(baseColumnNames: "pkg_is_public", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE53446F8DFD21C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-196") {
		addForeignKeyConstraint(baseColumnNames: "pkg_license_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE53446B510F2FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-197") {
		addForeignKeyConstraint(baseColumnNames: "pkg_list_status_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE5344653438212", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-198") {
		addForeignKeyConstraint(baseColumnNames: "pkg_nominal_platform_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE53446E9794A2B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "plat_id", referencedTableName: "platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-199") {
		addForeignKeyConstraint(baseColumnNames: "pkg_scope_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE534461FB972B3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-200") {
		addForeignKeyConstraint(baseColumnNames: "pkg_status_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE534462A381B57", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-201") {
		addForeignKeyConstraint(baseColumnNames: "pkg_type_rv_fk", baseTableName: "package", baseTableSchemaName: "KBPlus", constraintName: "FKCFE5344692580D5F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-202") {
		addForeignKeyConstraint(baseColumnNames: "pc_action_user_fk", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF58A358B930", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-203") {
		addForeignKeyConstraint(baseColumnNames: "pc_lic_fk", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF5897738E0E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-204") {
		addForeignKeyConstraint(baseColumnNames: "pc_owner", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF58EDF122AE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-205") {
		addForeignKeyConstraint(baseColumnNames: "pc_pkg_fk", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF586459A10D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-206") {
		addForeignKeyConstraint(baseColumnNames: "pc_status_rdv_fk", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF58CD0303B2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-207") {
		addForeignKeyConstraint(baseColumnNames: "pc_sub_fk", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF5817DFB90A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-208") {
		addForeignKeyConstraint(baseColumnNames: "pc_sys_obj", baseTableName: "pending_change", baseTableSchemaName: "KBPlus", constraintName: "FK65CBDF58B7F24D84", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sys_id", referencedTableName: "system_object", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-209") {
		addForeignKeyConstraint(baseColumnNames: "perm_id", baseTableName: "perm_grant", baseTableSchemaName: "KBPlus", constraintName: "FKCF6BA30D17B140A3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "perm", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-210") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "perm_grant", baseTableSchemaName: "KBPlus", constraintName: "FKCF6BA30D92370AE3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-211") {
		addForeignKeyConstraint(baseColumnNames: "plat_servprov_rv_fk", baseTableName: "platform", baseTableSchemaName: "KBPlus", constraintName: "FK6FBD68739969A1A1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-212") {
		addForeignKeyConstraint(baseColumnNames: "plat_softprov_rv_fk", baseTableName: "platform", baseTableSchemaName: "KBPlus", constraintName: "FK6FBD6873646ABCB5", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-213") {
		addForeignKeyConstraint(baseColumnNames: "plat_status_rv_fk", baseTableName: "platform", baseTableSchemaName: "KBPlus", constraintName: "FK6FBD6873E3F2DAD4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-214") {
		addForeignKeyConstraint(baseColumnNames: "plat_type_rv_fk", baseTableName: "platform", baseTableSchemaName: "KBPlus", constraintName: "FK6FBD68736DC6881C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-215") {
		addForeignKeyConstraint(baseColumnNames: "platform_id", baseTableName: "platformtipp", baseTableSchemaName: "KBPlus", constraintName: "FK9544A2810252C57", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "plat_id", referencedTableName: "platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-216") {
		addForeignKeyConstraint(baseColumnNames: "tipp_id", baseTableName: "platformtipp", baseTableSchemaName: "KBPlus", constraintName: "FK9544A28C581DD6E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-217") {
		addForeignKeyConstraint(baseColumnNames: "rdv_owner", baseTableName: "refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKF33A596F18DAEBF6", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdc_id", referencedTableName: "refdata_category", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-218") {
		addForeignKeyConstraint(baseColumnNames: "sub_is_public", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591D28E1DD90", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-219") {
		addForeignKeyConstraint(baseColumnNames: "sub_is_slaved", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591D2D814494", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-220") {
		addForeignKeyConstraint(baseColumnNames: "sub_owner_license_fk", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591D7D96D7D2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "lic_id", referencedTableName: "license", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-221") {
		addForeignKeyConstraint(baseColumnNames: "sub_parent_sub_fk", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591D530432B4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-222") {
		addForeignKeyConstraint(baseColumnNames: "sub_status_rv_fk", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591DE82AEB63", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-223") {
		addForeignKeyConstraint(baseColumnNames: "sub_type_rv_fk", baseTableName: "subscription", baseTableSchemaName: "KBPlus", constraintName: "FK1456591D6297706B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-224") {
		addForeignKeyConstraint(baseColumnNames: "owner_id", baseTableName: "subscription_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FK8717A7C14B06441", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-225") {
		addForeignKeyConstraint(baseColumnNames: "ref_value_id", baseTableName: "subscription_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FK8717A7C12992A286", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-226") {
		addForeignKeyConstraint(baseColumnNames: "type_id", baseTableName: "subscription_custom_property", baseTableSchemaName: "KBPlus", constraintName: "FK8717A7C1D4223E79", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pd_id", referencedTableName: "property_definition", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-227") {
		addForeignKeyConstraint(baseColumnNames: "sp_pkg_fk", baseTableName: "subscription_package", baseTableSchemaName: "KBPlus", constraintName: "FK5122C72467963563", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-228") {
		addForeignKeyConstraint(baseColumnNames: "sp_sub_fk", baseTableName: "subscription_package", baseTableSchemaName: "KBPlus", constraintName: "FK5122C7241B1C4D60", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-229") {
		addForeignKeyConstraint(baseColumnNames: "event_id", baseTableName: "title_history_event_participant", baseTableSchemaName: "KBPlus", constraintName: "FKE3AB36FCC15EF6E1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "title_history_event", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-230") {
		addForeignKeyConstraint(baseColumnNames: "participant_id", baseTableName: "title_history_event_participant", baseTableSchemaName: "KBPlus", constraintName: "FKE3AB36FC897EC757", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-231") {
		addForeignKeyConstraint(baseColumnNames: "ti_status_rv_fk", baseTableName: "title_instance", baseTableSchemaName: "KBPlus", constraintName: "FKACC69C66D9594E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-232") {
		addForeignKeyConstraint(baseColumnNames: "ti_type_rv_fk", baseTableName: "title_instance", baseTableSchemaName: "KBPlus", constraintName: "FKACC69C334E5D16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-233") {
		addForeignKeyConstraint(baseColumnNames: "tipp_delayedoa_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F7130FBFC", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-234") {
		addForeignKeyConstraint(baseColumnNames: "tipp_derived_from", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F922AC05F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-235") {
		addForeignKeyConstraint(baseColumnNames: "tipp_hybridoa_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8FB46EBDEA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-236") {
		addForeignKeyConstraint(baseColumnNames: "tipp_option_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F16ABD6D1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-237") {
		addForeignKeyConstraint(baseColumnNames: "tipp_payment_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F5A337F4E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-238") {
		addForeignKeyConstraint(baseColumnNames: "tipp_pkg_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F54894D8B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "pkg_id", referencedTableName: "package", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-239") {
		addForeignKeyConstraint(baseColumnNames: "tipp_plat_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F810634BB", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "plat_id", referencedTableName: "platform", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-240") {
		addForeignKeyConstraint(baseColumnNames: "tipp_status_reason_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8FF3DE1BB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-241") {
		addForeignKeyConstraint(baseColumnNames: "tipp_status_rv_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F3E48CC8E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-242") {
		addForeignKeyConstraint(baseColumnNames: "tipp_sub_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F80F6588", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "sub_id", referencedTableName: "subscription", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-243") {
		addForeignKeyConstraint(baseColumnNames: "tipp_ti_fk", baseTableName: "title_instance_package_platform", baseTableSchemaName: "KBPlus", constraintName: "FKE793FB8F40E502F5", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-244") {
		addForeignKeyConstraint(baseColumnNames: "tttnp_inst_org_fk", baseTableName: "title_institution_provider", baseTableSchemaName: "KBPlus", constraintName: "FK89A2E01F35702557", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-245") {
		addForeignKeyConstraint(baseColumnNames: "tttnp_prov_org_fk", baseTableName: "title_institution_provider", baseTableSchemaName: "KBPlus", constraintName: "FK89A2E01F97876AD4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-246") {
		addForeignKeyConstraint(baseColumnNames: "tttnp_title", baseTableName: "title_institution_provider", baseTableSchemaName: "KBPlus", constraintName: "FK89A2E01F47B4BD3F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "ti_id", referencedTableName: "title_instance", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-247") {
		addForeignKeyConstraint(baseColumnNames: "tr_accepts_format_rv_fk", baseTableName: "transforms", baseTableSchemaName: "KBPlus", constraintName: "FK990F02873696527E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-248") {
		addForeignKeyConstraint(baseColumnNames: "tr_transformer_fk", baseTableName: "transforms", baseTableSchemaName: "KBPlus", constraintName: "FK990F0287277F0208", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tfmr_id", referencedTableName: "transformer", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-249") {
		addForeignKeyConstraint(baseColumnNames: "refdata_value_id", baseTableName: "transforms_refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKF0E0B5B7AAD0839C", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-250") {
		addForeignKeyConstraint(baseColumnNames: "transforms_accepts_types_id", baseTableName: "transforms_refdata_value", baseTableSchemaName: "KBPlus", constraintName: "FKF0E0B5B73B259171", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "tr_id", referencedTableName: "transforms", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-251") {
		addForeignKeyConstraint(baseColumnNames: "default_dash_id", baseTableName: "user", baseTableSchemaName: "KBPlus", constraintName: "FK36EBCBB3BC6E31", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-252") {
		addForeignKeyConstraint(baseColumnNames: "show_info_icon_id", baseTableName: "user", baseTableSchemaName: "KBPlus", constraintName: "FK36EBCBFD5BDBC3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-253") {
		addForeignKeyConstraint(baseColumnNames: "uf_owner_id", baseTableName: "user_folder", baseTableSchemaName: "KBPlus", constraintName: "FKE0966362A7674C9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-254") {
		addForeignKeyConstraint(baseColumnNames: "formal_role_id", baseTableName: "user_org", baseTableSchemaName: "KBPlus", constraintName: "FKF022EC7077234A93", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-255") {
		addForeignKeyConstraint(baseColumnNames: "org_id", baseTableName: "user_org", baseTableSchemaName: "KBPlus", constraintName: "FKF022EC7021D4E99D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "org_id", referencedTableName: "org", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-256") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_org", baseTableSchemaName: "KBPlus", constraintName: "FKF022EC703761CEC3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}

	changeSet(author: "ibbo (generated)", id: "1435152601456-257") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", baseTableSchemaName: "KBPlus", constraintName: "FK143BF46A92370AE3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "KBPlus", referencesUniqueColumn: "false")
	}
}
