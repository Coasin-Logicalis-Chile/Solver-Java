package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class TaskSla implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pauseDuration;
    private String pauseTime;
    private String timezone;
    private String sysUpdatedOn;
    private String uInvalidReason;
    private String businessTimeLeft;
    private String duration;
    private String timeLeft;
    private String sysUpdatedBy;
    private String sysCreatedOn;
    private String percentage;
    private String originalBreachTime;
    private String sysCreatedBy;
    private String uInvalidCode;
    private String businessPercentage;
    private String uTriggerGroup;
    private String uMeasurable;
    private String endTime;
    private String sysModCount;
    private boolean active;
    private String businessPauseDuration;
    private String uInvalidBreach;
    private String startTime;
    private String businessDuration;
    private String stage;
    private String plannedEndTime;
    private String type;

    @Column(unique = true)
    private String integrationId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "incident", referencedColumnName = "id")
    private Incident incident;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sla", referencedColumnName = "id")
    private ContractSla sla;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule", referencedColumnName = "id")
    private CmnSchedule schedule;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_request_item", referencedColumnName = "id")
    private ScRequestItem scRequestItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_task", referencedColumnName = "id")
    private ScTask scTask;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SysGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(SysGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public String getPauseDuration() {
        return pauseDuration;
    }

    public void setPauseDuration(String pauseDuration) {
        this.pauseDuration = pauseDuration;
    }

    public String getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(String pauseTime) {
        this.pauseTime = pauseTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getSysUpdatedOn() {
        return sysUpdatedOn;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public String getuInvalidReason() {
        return uInvalidReason;
    }

    public void setuInvalidReason(String uInvalidReason) {
        this.uInvalidReason = uInvalidReason;
    }

    public String getBusinessTimeLeft() {
        return businessTimeLeft;
    }

    public void setBusinessTimeLeft(String businessTimeLeft) {
        this.businessTimeLeft = businessTimeLeft;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getSysUpdatedBy() {
        return sysUpdatedBy;
    }

    public void setSysUpdatedBy(String sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    public String getSysCreatedOn() {
        return sysCreatedOn;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getOriginalBreachTime() {
        return originalBreachTime;
    }

    public void setOriginalBreachTime(String originalBreachTime) {
        this.originalBreachTime = originalBreachTime;
    }

    public String getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public String getuInvalidCode() {
        return uInvalidCode;
    }

    public void setuInvalidCode(String uInvalidCode) {
        this.uInvalidCode = uInvalidCode;
    }

    public String getBusinessPercentage() {
        return businessPercentage;
    }

    public void setBusinessPercentage(String businessPercentage) {
        this.businessPercentage = businessPercentage;
    }

    public String getuTriggerGroup() {
        return uTriggerGroup;
    }

    public void setuTriggerGroup(String uTriggerGroup) {
        this.uTriggerGroup = uTriggerGroup;
    }

    public String getuMeasurable() {
        return uMeasurable;
    }

    public void setuMeasurable(String uMeasurable) {
        this.uMeasurable = uMeasurable;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSysModCount() {
        return sysModCount;
    }

    public void setSysModCount(String sysModCount) {
        this.sysModCount = sysModCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getBusinessPauseDuration() {
        return businessPauseDuration;
    }

    public void setBusinessPauseDuration(String businessPauseDuration) {
        this.businessPauseDuration = businessPauseDuration;
    }

    public String getuInvalidBreach() {
        return uInvalidBreach;
    }

    public void setuInvalidBreach(String uInvalidBreach) {
        this.uInvalidBreach = uInvalidBreach;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBusinessDuration() {
        return businessDuration;
    }

    public void setBusinessDuration(String businessDuration) {
        this.businessDuration = businessDuration;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(String plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
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

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public ContractSla getSla() {
        return sla;
    }

    public void setSla(ContractSla sla) {
        this.sla = sla;
    }

    public CmnSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(CmnSchedule schedule) {
        this.schedule = schedule;
    }

    public ScRequestItem getScRequestItem() {
        return scRequestItem;
    }

    public void setScRequestItem(ScRequestItem scRequestItem) {
        this.scRequestItem = scRequestItem;
    }

    public ScTask getScTask() {
        return scTask;
    }

    public void setScTask(ScTask scTask) {
        this.scTask = scTask;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}