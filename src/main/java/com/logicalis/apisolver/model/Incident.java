package com.logicalis.apisolver.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Incident implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String integrationId;
    //private String parent;

    @Column(unique = true)
    private String number;
    private String state;
    private String impact;
    private boolean active;
    private String priority;
    private String businessDuration;
    @Column(length = 40000)
    private String shortDescription;
    @Column(length = 80000)
    private String description;
    private String correlationDisplay;
    private String notify;
    private String reassignmentCount;
    private String uponApproval;
    private String correlationId;
    private boolean madeSla;
    @Column(columnDefinition = "boolean default false")
    private boolean knowledge;
    private String closedAt;
    private LocalDateTime closed;
    private String openedAt;
    private LocalDateTime opened;
    private String reopenedTime;
    private String resolvedAt;
    private LocalDateTime resolved;
    private String subcategory;
    private String closeCode;
    private String calendarDuration;
    @Column(length = 40000)
    private String closeNotes;
    private String contactType;
    private String incidentState;
    private String urgency;
    private String severity;
    private String approval;
    private String sysModCount;
    private String reopenCount;
    private String category;
    private String serviceLevel;
    private String ambiteLevel;
    private String platformLevel;
    private String specificationLevel;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private LocalDateTime createdOn;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private LocalDateTime updatedOn;
    private String incidentParent;
    private String scRequestParent;
    private String scRequestItemParent;
    private String reasonPending;

    @Column(columnDefinition = "boolean default false")
    private boolean solverFlagAssignedTo;

    @Column(columnDefinition = "boolean default false")
    private boolean solverFlagResolvedBy;


    @Column(columnDefinition = "boolean default false")
    private boolean delete;

    @Column(columnDefinition = "boolean default false")
    private boolean master;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caused_by", referencedColumnName = "id")
    private SysUser causedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_item", referencedColumnName = "id")
    private ConfigurationItem configurationItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "closed_by", referencedColumnName = "id")
    private SysUser closedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reopened_by", referencedColumnName = "id")
    private SysUser reopenedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private SysUser assignedTo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solver_assigned_to", referencedColumnName = "id")
    private SysUser solverAssignedTo;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resolved_by", referencedColumnName = "id")
    private SysUser resolvedBy;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solver_resolved_by", referencedColumnName = "id")
    private SysUser solverResolvedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opened_by", referencedColumnName = "id")
    private SysUser openedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_for", referencedColumnName = "id")
    private SysUser taskFor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ci_service", referencedColumnName = "id")
    private CiService businessService;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caller", referencedColumnName = "id")
    private SysUser caller;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;

    @Column(columnDefinition = "int default 0")
    private int countParent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getSysUpdatedOn() {
        return sysUpdatedOn;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
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

    public String getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBusinessDuration() {
        return businessDuration;
    }

    public void setBusinessDuration(String businessDuration) {
        this.businessDuration = businessDuration;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getCorrelationDisplay() {
        return correlationDisplay;
    }

    public void setCorrelationDisplay(String correlationDisplay) {
        this.correlationDisplay = correlationDisplay;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getReassignmentCount() {
        return reassignmentCount;
    }

    public void setReassignmentCount(String reassignmentCount) {
        this.reassignmentCount = reassignmentCount;
    }

    public String getUponApproval() {
        return uponApproval;
    }

    public void setUponApproval(String uponApproval) {
        this.uponApproval = uponApproval;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public boolean isMadeSla() {
        return madeSla;
    }

    public void setMadeSla(boolean madeSla) {
        this.madeSla = madeSla;
    }

    public boolean getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(boolean knowledge) {
        this.knowledge = knowledge;
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

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getReopenedTime() {
        return reopenedTime;
    }

    public void setReopenedTime(String reopenedTime) {
        this.reopenedTime = reopenedTime;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getCloseCode() {
        return closeCode;
    }

    public void setCloseCode(String closeCode) {
        this.closeCode = closeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCalendarDuration() {
        return calendarDuration;
    }

    public void setCalendarDuration(String calendarDuration) {
        this.calendarDuration = calendarDuration;
    }

    public String getCloseNotes() {
        return closeNotes;
    }

    public void setCloseNotes(String closeNotes) {
        this.closeNotes = closeNotes;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getIncidentState() {
        return incidentState;
    }

    public void setIncidentState(String incidentState) {
        this.incidentState = incidentState;
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

    public String getSysModCount() {
        return sysModCount;
    }

    public void setSysModCount(String sysModCount) {
        this.sysModCount = sysModCount;
    }

    public String getReopenCount() {
        return reopenCount;
    }

    public void setReopenCount(String reopenCount) {
        this.reopenCount = reopenCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SysUser getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(SysUser causedBy) {
        this.causedBy = causedBy;
    }

    public ConfigurationItem getConfigurationItem() {
        return configurationItem;
    }

    public void setConfigurationItem(ConfigurationItem configurationItem) {
        this.configurationItem = configurationItem;
    }

    public SysUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(SysUser closedBy) {
        this.closedBy = closedBy;
    }

    public SysUser getReopenedBy() {
        return reopenedBy;
    }

    public void setReopenedBy(SysUser reopenedBy) {
        this.reopenedBy = reopenedBy;
    }

    public SysUser getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(SysUser assignedTo) {
        this.assignedTo = assignedTo;
    }

    public SysUser getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(SysUser resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public SysUser getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(SysUser openedBy) {
        this.openedBy = openedBy;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
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

    public SysUser getCaller() {
        return caller;
    }

    public void setCaller(SysUser caller) {
        this.caller = caller;
    }

    public SysGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(SysGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public String getAmbiteLevel() {
        return ambiteLevel;
    }

    public void setAmbiteLevel(String ambiteLevel) {
        this.ambiteLevel = ambiteLevel;
    }

    public String getPlatformLevel() {
        return platformLevel;
    }

    public void setPlatformLevel(String platformLevel) {
        this.platformLevel = platformLevel;
    }

    public String getSpecificationLevel() {
        return specificationLevel;
    }

    public void setSpecificationLevel(String specificationLevel) {
        this.specificationLevel = specificationLevel;
    }


    public LocalDateTime getClosed() {
        return closed;
    }

    public void setClosed(LocalDateTime closed) {
        this.closed = closed;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getOpened() {
        return opened;
    }

    public void setOpened(LocalDateTime opened) {
        this.opened = opened;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getResolved() {
        return resolved;
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

    public boolean isKnowledge() {
        return knowledge;
    }

    public void setResolved(LocalDateTime resolved) {
        this.resolved = resolved;
    }

    public String getIncidentParent() {
        return incidentParent;
    }

    public void setIncidentParent(String incidentParent) {
        this.incidentParent = incidentParent;
    }

    public String getScRequestParent() {
        return scRequestParent;
    }

    public void setScRequestParent(String scRequestParent) {
        this.scRequestParent = scRequestParent;
    }

    public String getScRequestItemParent() {
        return scRequestItemParent;
    }

    public void setScRequestItemParent(String scRequestItemParent) {
        this.scRequestItemParent = scRequestItemParent;
    }

    public boolean getMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }


    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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

    public boolean isSolverFlagResolvedBy() {
        return solverFlagResolvedBy;
    }

    public boolean getSolverFlagResolvedBy() {
        return solverFlagResolvedBy;
    }

    public void setSolverFlagResolvedBy(boolean solverFlagResolvedBy) {
        this.solverFlagResolvedBy = solverFlagResolvedBy;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isMaster() {
        return master;
    }

    public SysUser getSolverAssignedTo() {
        return solverAssignedTo;
    }

    public void setSolverAssignedTo(SysUser solverAssignedTo) {
        this.solverAssignedTo = solverAssignedTo;
    }

    public SysUser getSolverResolvedBy() {
        return solverResolvedBy;
    }

    public void setSolverResolvedBy(SysUser solverResolvedBy) {
        this.solverResolvedBy = solverResolvedBy;
    }

    public String getReasonPending() {
        return reasonPending;
    }

    public void setReasonPending(String reasonPending) {
        this.reasonPending = reasonPending;
    }

    public int getCountParent() {
        return countParent;
    }

    public void setCountParent(int countParent) {
        this.countParent = countParent;
    }

    private static final long serialVersionUID = 1L;

}