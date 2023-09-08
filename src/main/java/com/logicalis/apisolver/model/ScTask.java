package com.logicalis.apisolver.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class ScTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String actionStatus;
    private boolean active;
    private String activityDue;
    private String additionalAssigneeList;
    private String approval;
    private String approvalHistory;
    private String approvalSet;
    private String businessDuration;
    private String calendarDuration;
    private String calendarStc;
    @Column(length = 40000)
    private String closeNotes;
    private String closedAt;
    private String comments;
    private String commentsAndWorkNotes;
    private String contactType;
    private String contract;
    private String correlationDisplay;
    private String correlationId;
    private String deliveryPlan;
    private String deliveryTask;
    @Column(length = 80000)
    private String description;
    private String dueDate;
    private String escalation;
    private String expectedStart;
    private String followUp;
    private String groupList;
    private String impact;
    @Column(unique=true)
    private String integrationId;
    private String knowledge;
    private String madeSla;
    private String needsAttention;
    @Column(unique = true)
    private String number;
    private String openedAt;
    private String priority;
    private String reassignmentCount;
    private String routeReason;
    private String serviceOffering;
    @Column(length = 40000)
    private String shortDescription;
    private String skills;
    private String slaDue;
    private String state;
    private String sysClassName;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysModCount;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private String taskEffectiveNumber;
    private String timeWorked;
    private String universalRequest;
    private String uponApproval;
    private String uponReject;
    private String urgency;
    private String userInput;
    private String workEnd;
    private String workStart;
    @Column(columnDefinition = "boolean default false")
    private boolean scaling;
    @Column(columnDefinition = "boolean default false")
    private boolean solverFlagAssignedTo;
    @Column(columnDefinition = "boolean default false")
    private boolean solverFlagClosedBy;
    private String reasonPending;
    @Column(columnDefinition = "varchar(255) default 'default'")
    private String scRequestIntegrationId;
    @Column(columnDefinition = "varchar(255) default 'default'")
    private String scRequestItemIntegrationId;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private SysUser assignedTo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solver_assigned_to", referencedColumnName = "id")
    private SysUser solverAssignedTo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scaling_assigned_to", referencedColumnName = "id")
    private SysUser scalingAssignedTo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scaling_assignment_group", referencedColumnName = "id")
    private SysGroup scalingAssignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "closed_by", referencedColumnName = "id")
    private SysUser closedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solver_closed_by", referencedColumnName = "id")
    private SysUser solverClosedBy;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_item", referencedColumnName = "id")
    private ConfigurationItem configurationItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opened_by", referencedColumnName = "id")
    private SysUser openedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_request", referencedColumnName = "id")
    private ScRequest scRequest;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_request_item", referencedColumnName = "id")
    private ScRequestItem scRequestItem;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_for", referencedColumnName = "id")
    private SysUser taskFor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ci_service", referencedColumnName = "id")
    private CiService businessService;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "catalog", referencedColumnName = "id")
    private Catalog catalog;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requested_for", referencedColumnName = "id")
    private SysUser requestedFor;


    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
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

    public String getActivityDue() {
        return activityDue;
    }

    public void setActivityDue(String activityDue) {
        this.activityDue = activityDue;
    }

    public String getAdditionalAssigneeList() {
        return additionalAssigneeList;
    }

    public void setAdditionalAssigneeList(String additionalAssigneeList) {
        this.additionalAssigneeList = additionalAssigneeList;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(String approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public String getApprovalSet() {
        return approvalSet;
    }

    public void setApprovalSet(String approvalSet) {
        this.approvalSet = approvalSet;
    }

    public String getBusinessDuration() {
        return businessDuration;
    }

    public void setBusinessDuration(String businessDuration) {
        this.businessDuration = businessDuration;
    }

    public String getCalendarDuration() {
        return calendarDuration;
    }

    public void setCalendarDuration(String calendarDuration) {
        this.calendarDuration = calendarDuration;
    }

    public String getCalendarStc() {
        return calendarStc;
    }

    public void setCalendarStc(String calendarStc) {
        this.calendarStc = calendarStc;
    }

    public String getCloseNotes() {
        return closeNotes;
    }

    public void setCloseNotes(String closeNotes) {
        this.closeNotes = closeNotes;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCommentsAndWorkNotes() {
        return commentsAndWorkNotes;
    }

    public void setCommentsAndWorkNotes(String commentsAndWorkNotes) {
        this.commentsAndWorkNotes = commentsAndWorkNotes;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getCorrelationDisplay() {
        return correlationDisplay;
    }

    public void setCorrelationDisplay(String correlationDisplay) {
        this.correlationDisplay = correlationDisplay;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDeliveryPlan() {
        return deliveryPlan;
    }

    public void setDeliveryPlan(String deliveryPlan) {
        this.deliveryPlan = deliveryPlan;
    }

    public String getDeliveryTask() {
        return deliveryTask;
    }

    public void setDeliveryTask(String deliveryTask) {
        this.deliveryTask = deliveryTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getEscalation() {
        return escalation;
    }

    public void setEscalation(String escalation) {
        this.escalation = escalation;
    }

    public String getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(String expectedStart) {
        this.expectedStart = expectedStart;
    }

    public String getFollowUp() {
        return followUp;
    }

    public void setFollowUp(String followUp) {
        this.followUp = followUp;
    }

    public String getGroupList() {
        return groupList;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getMadeSla() {
        return madeSla;
    }

    public void setMadeSla(String madeSla) {
        this.madeSla = madeSla;
    }

    public String getNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(String needsAttention) {
        this.needsAttention = needsAttention;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReassignmentCount() {
        return reassignmentCount;
    }

    public void setReassignmentCount(String reassignmentCount) {
        this.reassignmentCount = reassignmentCount;
    }

    public String getRouteReason() {
        return routeReason;
    }

    public void setRouteReason(String routeReason) {
        this.routeReason = routeReason;
    }

    public String getServiceOffering() {
        return serviceOffering;
    }

    public void setServiceOffering(String serviceOffering) {
        this.serviceOffering = serviceOffering;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSlaDue() {
        return slaDue;
    }

    public void setSlaDue(String slaDue) {
        this.slaDue = slaDue;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getTaskEffectiveNumber() {
        return taskEffectiveNumber;
    }

    public void setTaskEffectiveNumber(String taskEffectiveNumber) {
        this.taskEffectiveNumber = taskEffectiveNumber;
    }

    public String getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(String timeWorked) {
        this.timeWorked = timeWorked;
    }

    public String getUniversalRequest() {
        return universalRequest;
    }

    public void setUniversalRequest(String universalRequest) {
        this.universalRequest = universalRequest;
    }

    public String getUponApproval() {
        return uponApproval;
    }

    public void setUponApproval(String uponApproval) {
        this.uponApproval = uponApproval;
    }

    public String getUponReject() {
        return uponReject;
    }

    public void setUponReject(String uponReject) {
        this.uponReject = uponReject;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(String workEnd) {
        this.workEnd = workEnd;
    }

    public String getWorkStart() {
        return workStart;
    }

    public void setWorkStart(String workStart) {
        this.workStart = workStart;
    }

    public SysUser getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(SysUser assignedTo) {
        this.assignedTo = assignedTo;
    }

    public SysGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(SysGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public SysUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(SysUser closedBy) {
        this.closedBy = closedBy;
    }

    public ConfigurationItem getConfigurationItem() {
        return configurationItem;
    }

    public void setConfigurationItem(ConfigurationItem configurationItem) {
        this.configurationItem = configurationItem;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public SysUser getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(SysUser openedBy) {
        this.openedBy = openedBy;
    }

    public ScRequest getScRequest() {
        return scRequest;
    }

    public void setScRequest(ScRequest scRequest) {
        this.scRequest = scRequest;
    }

    public ScRequestItem getScRequestItem() {
        return scRequestItem;
    }

    public void setScRequestItem(ScRequestItem scRequestItem) {
        this.scRequestItem = scRequestItem;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public SysUser getTaskFor() {
        return taskFor;
    }

    public void setTaskFor(SysUser taskFor) {
        this.taskFor = taskFor;
    }

    public CiService getBusinessService() {
        return businessService;
    }

    public void setBusinessService(CiService businessService) {
        this.businessService = businessService;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public SysUser getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(SysUser requestedFor) {
        this.requestedFor = requestedFor;
    }
    public boolean getScaling() {
        return scaling;
    }

    public void setScaling(boolean scaling) {
        this.scaling = scaling;
    }

    public SysUser getScalingAssignedTo() {
        return scalingAssignedTo;
    }

    public void setScalingAssignedTo(SysUser scalingAssignedTo) {
        this.scalingAssignedTo = scalingAssignedTo;
    }

    public SysGroup getScalingAssignmentGroup() {
        return scalingAssignmentGroup;
    }

    public void setScalingAssignmentGroup(SysGroup scalingAssignmentGroup) {
        this.scalingAssignmentGroup = scalingAssignmentGroup;
    }

    public boolean isScaling() {
        return scaling;
    }

    public boolean isSolverFlagAssignedTo() {
        return solverFlagAssignedTo;
    }

    public boolean getSolverFlagAssignedTo() {
        return solverFlagAssignedTo;
    }

    public void setSolverFlagAssignedTo(boolean solverFlagAssignedTo) {
        this.solverFlagAssignedTo = solverFlagAssignedTo;
    }

    public boolean isSolverFlagClosedBy() {
        return solverFlagClosedBy;
    }

    public boolean getSolverFlagClosedBy() {
        return solverFlagClosedBy;
    }

    public void setSolverFlagClosedBy(boolean solverFlagClosedBy) {
        this.solverFlagClosedBy = solverFlagClosedBy;
    }

    public SysUser getSolverAssignedTo() {
        return solverAssignedTo;
    }

    public void setSolverAssignedTo(SysUser solverAssignedTo) {
        this.solverAssignedTo = solverAssignedTo;
    }

    public SysUser getSolverClosedBy() {
        return solverClosedBy;
    }

    public void setSolverClosedBy(SysUser solverClosedBy) {
        this.solverClosedBy = solverClosedBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getReasonPending() {
        return reasonPending;
    }

    public void setReasonPending(String reasonPending) {
        this.reasonPending = reasonPending;
    }

    public String getScRequestIntegrationId() {
        return scRequestIntegrationId;
    }

    public void setScRequestIntegrationId(String scRequestIntegrationId) {
        this.scRequestIntegrationId = scRequestIntegrationId;
    }

    public String getScRequestItemIntegrationId() {
        return scRequestItemIntegrationId;
    }

    public void setScRequestItemIntegrationId(String scRequestItemIntegrationId) {
        this.scRequestItemIntegrationId = scRequestItemIntegrationId;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}