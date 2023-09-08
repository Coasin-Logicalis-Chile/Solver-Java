package com.logicalis.apisolver.model.enums;


public class EndPointSN {
    App app = new App();

    public String Api() {
        return app.SNInstance().concat("/api/now/table/");
    }

    public String ApiSolver() {
        return app.SNInstance().concat("/api/loch/solver/");
    }

    public String ApiAttachment() {
        return app.SNInstance().concat("/api/now/attachment");
    }

    public String SrcAttachment() {
        return "/sys_attachment.do?sys_id=";
    }

    public String Attachment() {
        return ApiAttachment().concat("?sysparm_query=table_name=sc_req_item^ORtable_name=incident^ORtable_name=sc_task^table_sys_idSTARTSWITH");
    }

    public String AttachmentByQuery() {
        return ApiAttachment().concat("?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String UploadAttachment() {
        return ApiAttachment().concat("/upload");
    }

    public String AttachmentBySysId() {
        return ApiAttachment().concat("?sys_id=");
    }

    public String Catalog() {
        return Api().concat("sc_catalog?");
    }

    public String CmnSchedule() {
        return Api().concat("cmn_schedule?");
    }

    public String ContractSla() {
        return Api().concat("contract_sla?");
    }

    public String ContractSlaByQuery() {
        return Api().concat("contract_sla?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String CatalogLine() {
        return Api().concat("u_catalog_line?sysparm_query=u_company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String Domain() {
        return Api().concat("domain?");
    }

    public String Company() {
        return Api().concat("core_company?sysparm_query=u_solver=true");
    }

    public String Incident() {
        return Api().concat("incident?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String IncidentByCompany() {
        return Api().concat("incident?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String IncidentByQuery() {
        return Api().concat("incident?sysparm_query=QUERY&sysparm_limit=2000&sysparm_offset=");
    }

    public String ChoiceByQuery() {
        return Api().concat("sys_choice?sysparm_query=QUERY&sysparm_limit=2000&sysparm_offset=");
    }

    public String ScRequest() {
        return Api().concat("sc_request?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScRequestByQuery() {
        return Api().concat("sc_request?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScRequestScheduleByQuery() {
        return Api().concat("sc_request?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=0");
    }

    public String ScRequestByCompany() {
        return Api().concat("sc_request?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScTaskByCompany() {
        return Api().concat("sc_task?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScTaskByQuery() {
        return Api().concat("sc_task?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }
    public String CiServiceByQuery() {
        return Api().concat("cmdb_ci_service?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String ConfigurationItemByQuery() {
        return Api().concat("cmdb_ci?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }




    public String GeneralQuery() {
        return Api().concat("TABLE?sysparm_query=sys_created_onONLast 15 minutes@javascript:gs.beginningOfLast15Minutes()@javascript:gs.endOfLast15Minutes()^ORsys_updated_onONLast 15 minutes@javascript:gs.beginningOfLast15Minutes()@javascript:gs.endOfLast15Minutes()^company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScRequestItem() {
        return Api().concat("sc_req_item?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScRequestItemByQuery() {
        return Api().concat("sc_req_item?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScRequestItemByCompany() {
        return Api().concat("sc_req_item?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String Group() {
        return Api().concat("sys_user_group?sysparm_query=company.u_solver=true");
    }

    public String Department() {
        return Api().concat("cmn_department?sysparm_query=company.u_solver=true");
    }

    public String Location() {
        return Api().concat("cmn_location?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String LocationByQuery() {
        return Api().concat("cmn_location?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String SysUser() {
        return Api().concat("sys_user?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String SysUserByQuery() {
        return Api().concat("sys_user?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String SysUserGroup() {
        return Api().concat("sys_user_grmember?sysparm_query=group.company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String ScCategory() {
        return Api().concat("sc_category");
    }

    public String ScCategoryItem() {
        return Api().concat("sc_cat_item");
    }

    public String ChoiceIncident() {
        return Api().concat("sys_choice?sysparm_query=language=es^inactive=false^element=category^ORelement=subcategory^ORelement=u_incident_type^ORelement=state^ORelement=incident_state^ORelement=impact^ORelement=urgency^ORelement=priority^ORelement=contact_type^ORelement=close_code^ORelement=u_sbk_pendiente^sys_domain=global");//&sysparm_limit=5000&sysparm_limit=");
    }

    public String ChoiceScrequestItem() {
        return Api().concat("sys_choice?sysparm_query=language=es^inactive=false^element=category^ORelement=subcategory^ORelement=state^ORelement=impact^ORelement=urgency^ORelement=priority^ORelement=contact_type^ORelement=close_code^sys_domain=global");
    }

    public String ChoiceRequest() {
        return Api().concat("sys_choice?sysparm_query=language=es^inactive=false^nameLIKEsc_req^sys_domain=global");
    }

    public String ChoiceTaskSla() {
        return Api().concat("sys_choice?sysparm_query=elementSTARTSWITHstage^language=es^name=task_sla^sys_domain=global");
    }

    public String ChoiceTask() {
        return Api().concat("sys_choice?sysparm_query=name=task^sys_domain=global^language=es");
    }

    public String ConfigurationItem() {
        return Api().concat("cmdb_ci?sysparm_query=company.u_solver=true&sysparm_limit=5000&sysparm_offset=");
    }

    public String CiService() {
        return Api().concat("cmdb_ci_service?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String Journal() {
        // return Api().concat("sys_journal_field?sysparm_query=name=sc_req_item^ORname=incident^ORname=sc_request&sysparm_limit=3000&sysparm_offset=");
        return Api().concat("sys_journal_field?sysparm_query=name=incident^ORname=sc_req_item^ORname=sc_request^ORname=sc_task&sysparm_limit=3000&sysparm_offset=");
        //     return ApiSolver().concat("sys_journal_field");
    }

    public String JournalByQuery() {
        return Api().concat("sys_journal_field?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String JournalByElement() {
        return Api().concat("sys_journal_field?sysparm_query=element_id=");
    }

    public String SysAudit() {
        return Api().concat("sys_audit?sysparm_query=tablename=incident^ORtablename=sc_task^fieldname!=work_notes^fieldname!=comments&sysparm_limit=3000&sysparm_offset=");
    }

    public String SysDocumentation() {
        return Api().concat("sys_documentation?sysparm_query=name=incident^ORname=sc_task^ORname=sc_request^ORname=sc_req_item^ORname=task^language=es^element!=NULL&sysparm_limit=3000&sysparm_offset=");
    }

    public String TaskSlaByCompany() {
        return Api().concat("task_sla?sysparm_query=task.company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public String TaskSlaByQuery() {
        return Api().concat("task_sla?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public String PostJournal() {
        return ApiSolver().concat("journal_field");
    }

    public String PutIncident() {
        return ApiSolver().concat("incident");
    }

    public String PutSysUser() {
        return ApiSolver().concat("sys_user");
    }

    public String PutScRequestItem() {
        return ApiSolver().concat("sc_req_item");
    }

    public String PutScTask() {
        return ApiSolver().concat("sc_task");
    }
} 