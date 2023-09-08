package com.logicalis.apisolver.view;

public class IncidentRequest {

    private String sys_id = "";
    private String number = "";
    private String state = "";
    private String impact = "";
    private boolean active = false;
    private String priority = "";
    private String business_duration = "";
    private String short_description = "";
    private String correlation_display = "";
    private String notify = "";
    private String reassignment_count;
    private String upon_approval;
    private String correlation_id;
    private String made_sla;
    private String sys_updated_by;
    private String sys_created_on;
    private String sys_updated_on;
    private String sys_created_by;
    private String closed_at;
    private String opened_at;
    private String reopened_time;
    private String resolved_at;
    private String subcategory;
    private String close_code;
    private String description;
    private String calendar_duration;
    private String close_notes;
    private String integrationId;
    private String contact_type;
    private String incident_state;
    private String urgency;
    private String severity;
    private String approval;
    private String sys_mod_count;
    private String reopen_count;
    private String category;
    private String u_service;
    private String u_ambite;
    private String u_platform;
    private String u_specification;
    private String domain;
    private String sys_domain;
    private String company;
    private String caused_by;
    private String closed_by;
    private String reopened_by;
    private String assigned_to;
    private String resolved_by;
    private String opened_by;
    private String task_for;
    private String business_service;
    private String caller_id;
    private String configuration_item;
    private String assignment_group;
    private String parent;
    private String incident_parent;
    private String parent_incident;
    private boolean u_solver_flag_assigned_to;
    private String u_solver_assigned_to;
    private boolean u_solver_flag_resolved_by;
    private String u_solver_resolved_by;
    private String location;
    private boolean knowledge = false;
    private String reason_pending;

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBusiness_duration() {
        return business_duration;
    }

    public void setBusiness_duration(String business_duration) {
        this.business_duration = business_duration;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getCorrelation_display() {
        return correlation_display;
    }

    public void setCorrelation_display(String correlation_display) {
        this.correlation_display = correlation_display;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getReassignment_count() {
        return reassignment_count;
    }

    public void setReassignment_count(String reassignment_count) {
        this.reassignment_count = reassignment_count;
    }

    public String getUpon_approval() {
        return upon_approval;
    }

    public void setUpon_approval(String upon_approval) {
        this.upon_approval = upon_approval;
    }

    public String getCorrelation_id() {
        return correlation_id;
    }

    public void setCorrelation_id(String correlation_id) {
        this.correlation_id = correlation_id;
    }

    public String getMade_sla() {
        return made_sla;
    }

    public void setMade_sla(String made_sla) {
        this.made_sla = made_sla;
    }

    public String getSys_updated_by() {
        return sys_updated_by;
    }

    public void setSys_updated_by(String sys_updated_by) {
        this.sys_updated_by = sys_updated_by;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getSys_updated_on() {
        return sys_updated_on;
    }

    public void setSys_updated_on(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }

    public String getOpened_at() {
        return opened_at;
    }

    public void setOpened_at(String opened_at) {
        this.opened_at = opened_at;
    }

    public String getReopened_time() {
        return reopened_time;
    }

    public void setReopened_time(String reopened_time) {
        this.reopened_time = reopened_time;
    }

    public String getResolved_at() {
        return resolved_at;
    }

    public void setResolved_at(String resolved_at) {
        this.resolved_at = resolved_at;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getClose_code() {
        return close_code;
    }

    public void setClose_code(String close_code) {
        this.close_code = close_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCalendar_duration() {
        return calendar_duration;
    }

    public void setCalendar_duration(String calendar_duration) {
        this.calendar_duration = calendar_duration;
    }

    public String getClose_notes() {
        return close_notes;
    }

    public void setClose_notes(String close_notes) {
        this.close_notes = close_notes;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public String getIncident_state() {
        return incident_state;
    }

    public void setIncident_state(String incident_state) {
        this.incident_state = incident_state;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getSys_mod_count() {
        return sys_mod_count;
    }

    public void setSys_mod_count(String sys_mod_count) {
        this.sys_mod_count = sys_mod_count;
    }

    public String getReopen_count() {
        return reopen_count;
    }

    public void setReopen_count(String reopen_count) {
        this.reopen_count = reopen_count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getU_service() {
        return u_service;
    }

    public void setU_service(String u_service) {
        this.u_service = u_service;
    }

    public String getU_ambite() {
        return u_ambite;
    }

    public void setU_ambite(String u_ambite) {
        this.u_ambite = u_ambite;
    }

    public String getU_platform() {
        return u_platform;
    }

    public void setU_platform(String u_platform) {
        this.u_platform = u_platform;
    }

    public String getU_specification() {
        return u_specification;
    }

    public void setU_specification(String u_specification) {
        this.u_specification = u_specification;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCaused_by() {
        return caused_by;
    }

    public void setCaused_by(String caused_by) {
        this.caused_by = caused_by;
    }

    public String getClosed_by() {
        return closed_by;
    }

    public void setClosed_by(String closed_by) {
        this.closed_by = closed_by;
    }

    public String getReopened_by() {
        return reopened_by;
    }

    public void setReopened_by(String reopened_by) {
        this.reopened_by = reopened_by;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getResolved_by() {
        return resolved_by;
    }

    public void setResolved_by(String resolved_by) {
        this.resolved_by = resolved_by;
    }

    public String getOpened_by() {
        return opened_by;
    }

    public void setOpened_by(String opened_by) {
        this.opened_by = opened_by;
    }

    public String getTask_for() {
        return task_for;
    }

    public void setTask_for(String task_for) {
        this.task_for = task_for;
    }

    public String getBusiness_service() {
        return business_service;
    }

    public void setBusiness_service(String business_service) {
        this.business_service = business_service;
    }

    public String getCaller_id() {
        return caller_id;
    }

    public void setCaller_id(String caller_id) {
        this.caller_id = caller_id;
    }

    public String getConfiguration_item() {
        return configuration_item;
    }

    public void setConfiguration_item(String configuration_item) {
        this.configuration_item = configuration_item;
    }

    public String getAssignment_group() {
        return assignment_group;
    }

    public void setAssignment_group(String assignment_group) {
        this.assignment_group = assignment_group;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isKnowledge() {
        return knowledge;
    }

    public boolean getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(boolean knowledge) {
        this.knowledge = knowledge;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent_incident() {
        return parent_incident;
    }

    public void setParent_incident(String parent_incident) {
        this.parent_incident = parent_incident;
    }

    public String getIncident_parent() {
        return incident_parent;
    }

    public void setIncident_parent(String incident_parent) {
        this.incident_parent = incident_parent;
    }

    public boolean getU_solver_flag_assigned_to() {
        return u_solver_flag_assigned_to;
    }

    public void setU_solver_flag_assigned_to(boolean u_solver_flag_assigned_to) {
        this.u_solver_flag_assigned_to = u_solver_flag_assigned_to;
    }

    public String getU_solver_assigned_to() {
        return u_solver_assigned_to;
    }

    public void setU_solver_assigned_to(String u_solver_assigned_to) {
        this.u_solver_assigned_to = u_solver_assigned_to;
    }

    public boolean getU_solver_flag_resolved_by() {
        return u_solver_flag_resolved_by;
    }

    public void setU_solver_flag_resolved_by(boolean u_solver_flag_resolved_by) {
        this.u_solver_flag_resolved_by = u_solver_flag_resolved_by;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getU_solver_resolved_by() {
        return u_solver_resolved_by;
    }

    public void setU_solver_resolved_by(String u_solver_resolved_by) {
        this.u_solver_resolved_by = u_solver_resolved_by;
    }

    public String getSys_domain() {
        return sys_domain;
    }

    public void setSys_domain(String sys_domain) {
        this.sys_domain = sys_domain;
    }

    public boolean isU_solver_flag_assigned_to() {
        return u_solver_flag_assigned_to;
    }

    public boolean isU_solver_flag_resolved_by() {
        return u_solver_flag_resolved_by;
    }

    public String getReason_pending() {
        return reason_pending;
    }

    public void setReason_pending(String reason_pending) {
        this.reason_pending = reason_pending;
    }
}
