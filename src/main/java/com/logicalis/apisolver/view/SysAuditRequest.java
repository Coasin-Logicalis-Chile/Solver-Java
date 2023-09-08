package com.logicalis.apisolver.view;

public class SysAuditRequest {
    private String fieldname;
    private String reason;
    private String sys_id;
    private String newvalue;
    private String sys_created_on;
    private String documentkey;
    private String internal_checkpoint;
    private String record_checkpoint;
    private String tablename;
    private String user;
    private String oldvalue;
    private String sys_created_by;

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(String newvalue) {
        this.newvalue = newvalue;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getDocumentkey() {
        return documentkey;
    }

    public void setDocumentkey(String documentkey) {
        this.documentkey = documentkey;
    }

    public String getInternal_checkpoint() {
        return internal_checkpoint;
    }

    public void setInternal_checkpoint(String internal_checkpoint) {
        this.internal_checkpoint = internal_checkpoint;
    }

    public String getRecord_checkpoint() {
        return record_checkpoint;
    }

    public void setRecord_checkpoint(String record_checkpoint) {
        this.record_checkpoint = record_checkpoint;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(String oldvalue) {
        this.oldvalue = oldvalue;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }
}
