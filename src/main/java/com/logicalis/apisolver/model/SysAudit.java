package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SysAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldname;
    private String reason;
    private String integrationId;
    @Column(length = 800000)
    private String newvalue;
    private String sysCreatedOn;
    private String documentkey;
    private String internalCheckpoint;
    private String recordCheckpoint;
    private String tablename;
    private String userName;
    @Column(length = 800000)
    private String oldvalue;
    private String sysCreatedBy;
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(String newvalue) {
        this.newvalue = newvalue;
    }

    public String getSysCreatedOn() {
        return sysCreatedOn;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public String getDocumentkey() {
        return documentkey;
    }

    public void setDocumentkey(String documentkey) {
        this.documentkey = documentkey;
    }

    public String getInternalCheckpoint() {
        return internalCheckpoint;
    }

    public void setInternalCheckpoint(String internalCheckpoint) {
        this.internalCheckpoint = internalCheckpoint;
    }

    public String getRecordCheckpoint() {
        return recordCheckpoint;
    }

    public void setRecordCheckpoint(String recordCheckpoint) {
        this.recordCheckpoint = recordCheckpoint;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(String oldvalue) {
        this.oldvalue = oldvalue;
    }

    public String getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public boolean isActive() {
        return active;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private static final long serialVersionUID = 1L;
}
