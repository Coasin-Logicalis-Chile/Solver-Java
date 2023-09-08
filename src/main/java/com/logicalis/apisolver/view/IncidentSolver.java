package com.logicalis.apisolver.view;

import org.json.simple.JSONObject;

import java.io.Serializable;

public class IncidentSolver implements Serializable {
  //  private String parent;
    private String number;
    private String state;
    private String impact;
    private boolean active;
    private boolean knowledge;
    private String priority;
    private String business_duration;
    private String short_description;
    private String correlation_display;
    private String notify;
    private String reassignment_count;
    private String upon_approval;
    private String correlation_id;
    private boolean made_sla;
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
    private String sys_id;
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
    private boolean u_solver_flag_assigned_to;
    private boolean u_solver_flag_resolved_by;
    private String incident_parent;
    private String u_sbk_pendiente;

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

    public boolean isActive() {
        return active;
    }

    public boolean getActive() {
        return active;
    }

    public boolean getKnowledge() {
        return knowledge;
    }

    public boolean isKnowledge() {
        return knowledge;
    }

    public void setKnowledge(boolean knowledge) {
        this.knowledge = knowledge;
    }

    public boolean isMade_sla() {
        return made_sla;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public boolean getMade_sla() {
        return made_sla;
    }

    public void setMade_sla(boolean made_sla) {
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

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
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

    public boolean getU_solver_flag_assigned_to() {
        return u_solver_flag_assigned_to;
    }

    public void setU_solver_flag_assigned_to(boolean u_solver_flag_assigned_to) {
        this.u_solver_flag_assigned_to = u_solver_flag_assigned_to;
    }

    public boolean getU_solver_flag_resolved_by() {
        return u_solver_flag_resolved_by;
    }

    public void setU_solver_flag_resolved_by(boolean u_solver_flag_resolved_by) {
        this.u_solver_flag_resolved_by = u_solver_flag_resolved_by;
    }

    public void setU_specification(String u_specification) {
        this.u_specification = u_specification;
    }

   /* public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setParent(JSONObject parent) {
        this.parent = parent.toString();
    }*/

    public boolean isU_solver_flag_assigned_to() {
        return u_solver_flag_assigned_to;
    }

    public boolean isU_solver_flag_resolved_by() {
        return u_solver_flag_resolved_by;
    }

    public String getIncident_parent() {
        return incident_parent;
    }

    public void setIncident_parent(String incident_parent) {
        this.incident_parent = incident_parent;
    }

    public String getU_sbk_pendiente() {
        return u_sbk_pendiente;
    }

    public void setU_sbk_pendiente(String u_sbk_pendiente) {
        this.u_sbk_pendiente = u_sbk_pendiente;
    }
}
