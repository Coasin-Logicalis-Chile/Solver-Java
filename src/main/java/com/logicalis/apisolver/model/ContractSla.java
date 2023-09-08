package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ContractSla implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean active;
    //private String cancelCondition;
    private String collection;
    //private String conditionClass;
    private String duration;
    private String durationType;
    private String enableLogging;
    private String name;
    //private String pauseCondition;
    private String relativeDurationWorksOn;
    private String resetAction;
    //private String resetCondition;
    //private String resumeCondition;
    private String retroactive;
    private String retroactivePause;
    private String scheduleSource;
    private String scheduleSourceField;
    //private String startCondition;
    //private String stopCondition;
    private String sysClassName;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysModCount;
    private String sysName;
    private String sysPolicy;
    private String sysUpdateName;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private String target;
    private String timezone;
    private String timezoneSource;
    private String type;
    private String vendor;
    private String whenToCancel;
    private String whenToResume;
    @Column(unique = true)
    private String integrationId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
/*
    public String getConditionClass() {
        return conditionClass;
    }

    public void setConditionClass(String conditionClass) {
        this.conditionClass = conditionClass;
    }
*/
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(String enableLogging) {
        this.enableLogging = enableLogging;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelativeDurationWorksOn() {
        return relativeDurationWorksOn;
    }

    public void setRelativeDurationWorksOn(String relativeDurationWorksOn) {
        this.relativeDurationWorksOn = relativeDurationWorksOn;
    }

    public String getResetAction() {
        return resetAction;
    }

    public void setResetAction(String resetAction) {
        this.resetAction = resetAction;
    }

    public String getRetroactive() {
        return retroactive;
    }

    public void setRetroactive(String retroactive) {
        this.retroactive = retroactive;
    }

    public String getRetroactivePause() {
        return retroactivePause;
    }

    public void setRetroactivePause(String retroactivePause) {
        this.retroactivePause = retroactivePause;
    }

    public String getScheduleSource() {
        return scheduleSource;
    }

    public void setScheduleSource(String scheduleSource) {
        this.scheduleSource = scheduleSource;
    }

    public String getScheduleSourceField() {
        return scheduleSourceField;
    }

    public void setScheduleSourceField(String scheduleSourceField) {
        this.scheduleSourceField = scheduleSourceField;
    }

    public String getSysClassName() {
        return sysClassName;
    }

    public void setSysClassName(String sysClassName) {
        this.sysClassName = sysClassName;
    }

    public String getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public String getSysCreatedOn() {
        return sysCreatedOn;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public String getSysModCount() {
        return sysModCount;
    }

    public void setSysModCount(String sysModCount) {
        this.sysModCount = sysModCount;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysPolicy() {
        return sysPolicy;
    }

    public void setSysPolicy(String sysPolicy) {
        this.sysPolicy = sysPolicy;
    }

    public String getSysUpdateName() {
        return sysUpdateName;
    }

    public void setSysUpdateName(String sysUpdateName) {
        this.sysUpdateName = sysUpdateName;
    }

    public String getSysUpdatedBy() {
        return sysUpdatedBy;
    }

    public void setSysUpdatedBy(String sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    public String getSysUpdatedOn() {
        return sysUpdatedOn;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezoneSource() {
        return timezoneSource;
    }

    public void setTimezoneSource(String timezoneSource) {
        this.timezoneSource = timezoneSource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWhenToCancel() {
        return whenToCancel;
    }

    public void setWhenToCancel(String whenToCancel) {
        this.whenToCancel = whenToCancel;
    }

    public String getWhenToResume() {
        return whenToResume;
    }

    public void setWhenToResume(String whenToResume) {
        this.whenToResume = whenToResume;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}