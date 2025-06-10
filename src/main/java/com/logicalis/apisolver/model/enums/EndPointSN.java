package com.logicalis.apisolver.model.enums;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EndPointSN {
    public static String Api() {
        return App.SNInstance().concat("/api/now/table/");
    }

    public static String ApiSolver() {
        return App.SNInstance().concat("/api/loch/solver/");
    }

    public static String ApiAttachment() {
        return App.SNInstance().concat("/api/now/attachment");
    }

    public static String SrcAttachment() {
        return "/sys_attachment.do?sys_id=";
    }

    public static String Attachment() {
        return ApiAttachment().concat(
                "?sysparm_query=table_name=sc_req_item^ORtable_name=incident^ORtable_name=sc_task^table_sys_idSTARTSWITH");
    }

    public static String AttachmentByQuery() {
        return ApiAttachment().concat("?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String UploadAttachment() {
        return ApiAttachment().concat("/upload");
    }

    private RestTemplate restTemplate = new RestTemplate();

    
    public static String AttachmentBySysId() {
        return ApiAttachment().concat("?sys_id=");
    }

    public static String Catalog() {
        return Api().concat("sc_catalog?");
    }

    public static String CmnSchedule() {
        return Api().concat("cmn_schedule?");
    }

    public static String ContractSla() {
        return Api().concat("contract_sla?");
    }

    public static String ContractSlaByQuery() {
        return Api().concat("contract_sla?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String CatalogLine() {
        return Api().concat("u_catalog_line?sysparm_query=u_company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String Domain() {
        return Api().concat("domain?");
    }

    public static String Company() {
        return Api().concat("core_company?sysparm_query=u_solver=true");
    }

    public static String Incident() {
        return Api().concat("incident?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String IncidentByCompany() {
        return Api().concat("incident?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String IncidentByQuery() {
        return Api().concat("incident?sysparm_query=QUERY&sysparm_limit=2000&sysparm_offset=");
    }

    public static String ChoiceByQuery() {
        return Api().concat("sys_choice?sysparm_query=QUERY&sysparm_limit=2000&sysparm_offset=");
    }

    public static String ScRequest() {
        return Api().concat("sc_request?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScRequestByQuery() {
        return Api().concat("sc_request?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScRequestScheduleByQuery() {
        return Api().concat("sc_request?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=0");
    }

    public static String ScRequestByCompany() {
        return Api().concat("sc_request?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScTaskByCompany() {
        return Api().concat("sc_task?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScTaskByQuery() {
        return Api().concat("sc_task?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String CiServiceByQuery() {
        return Api().concat("cmdb_ci_service?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ConfigurationItemByQuery() {
        return Api().concat("cmdb_ci?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String GeneralQuery() {
        return Api().concat(
                "TABLE?sysparm_query=sys_created_onONLast 15 minutes@javascript:gs.beginningOfLast15Minutes()@javascript:gs.endOfLast15Minutes()^ORsys_updated_onONLast 15 minutes@javascript:gs.beginningOfLast15Minutes()@javascript:gs.endOfLast15Minutes()^company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScRequestItem() {
        return Api().concat("sc_req_item?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScRequestItemByQuery() {
        return Api().concat("sc_req_item?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScRequestItemByCompany() {
        return Api().concat("sc_req_item?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }
 
    public static String DeleteAttachment(String sysId) {
        return ApiSolver().concat("attachment/").concat(sysId);
    }

    public static String Group() {
        return Api().concat("sys_user_group?sysparm_query=company.u_solver=true");
    }

    public static String Department() {
        return Api().concat("cmn_department?sysparm_query=company.u_solver=true");
    }

    public static String Location() {
        return Api().concat("cmn_location?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String LocationByQuery() {
        return Api().concat("cmn_location?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String SysUser() {
        return Api().concat("sys_user?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String SysUserByQuery() {
        return Api().concat("sys_user?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String SysUserGroup() {
        return Api().concat(
                "sys_user_grmember?sysparm_query=group.company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String ScCategory() {
        return Api().concat("sc_category");
    }

    public static String ScCategoryItem() {
        return Api().concat("sc_cat_item");
    }

    public static String ChoiceIncident() {
        return Api().concat(
                "sys_choice?sysparm_query=language=es^inactive=false^element=category^ORelement=subcategory^ORelement=u_incident_type^ORelement=state^ORelement=incident_state^ORelement=impact^ORelement=urgency^ORelement=priority^ORelement=contact_type^ORelement=close_code^ORelement=u_sbk_pendiente^sys_domain=global");// &sysparm_limit=5000&sysparm_limit=");
    }

    public static String ChoiceScrequestItem() {
        return Api().concat(
                "sys_choice?sysparm_query=language=es^inactive=false^element=category^ORelement=subcategory^ORelement=state^ORelement=impact^ORelement=urgency^ORelement=priority^ORelement=contact_type^ORelement=close_code^sys_domain=global");
    }

    public static String ChoiceRequest() {
        return Api().concat("sys_choice?sysparm_query=language=es^inactive=false^nameLIKEsc_req^sys_domain=global");
    }

    public static String ChoiceTaskSla() {
        return Api()
                .concat("sys_choice?sysparm_query=elementSTARTSWITHstage^language=es^name=task_sla^sys_domain=global");
    }

    public static String ChoiceTask() {
        return Api().concat("sys_choice?sysparm_query=name=task^sys_domain=global^language=es");
    }

    public static String ConfigurationItem() {
        return Api().concat("cmdb_ci?sysparm_query=company.u_solver=true&sysparm_limit=5000&sysparm_offset=");
    }

    public static String CiService() {
        return Api().concat("cmdb_ci_service?sysparm_query=company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String Journal() {
        return Api().concat(
                "sys_journal_field?sysparm_query=name=incident^ORname=sc_req_item^ORname=sc_request^ORname=sc_task&sysparm_limit=3000&sysparm_offset=");
    }

    public static String JournalByQuery() {
        return Api().concat("sys_journal_field?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String JournalByElement() {
        return Api().concat("sys_journal_field?sysparm_query=element_id=");
    }

    public static String SysAudit() {
        return Api().concat(
                "sys_audit?sysparm_query=tablename=incident^ORtablename=sc_task^fieldname!=work_notes^fieldname!=comments&sysparm_limit=3000&sysparm_offset=");
    }

    public static String SysDocumentation() {
        return Api().concat(
                "sys_documentation?sysparm_query=name=incident^ORname=sc_task^ORname=sc_request^ORname=sc_req_item^ORname=task^language=es^element!=NULL&sysparm_limit=3000&sysparm_offset=");
    }

    public static String TaskSlaByCompany() {
        return Api().concat("task_sla?sysparm_query=task.company.u_solver=true&sysparm_limit=3000&sysparm_offset=");
    }

    public static String TaskSlaByQuery() {
        return Api().concat("task_sla?sysparm_query=QUERY&sysparm_limit=3000&sysparm_offset=");
    }

    public static String PostJournal() {
        return ApiSolver().concat("journal_field");
    }

    public static String PutIncident() {
        return ApiSolver().concat("incident");
    }

    public static String PutSysUser() {
        return ApiSolver().concat("sys_user");
    }

    public static String PutScRequestItem() {
        return ApiSolver().concat("sc_req_item");
    }

    public static String PutScTask() {
        return ApiSolver().concat("sc_task");
    }
}
