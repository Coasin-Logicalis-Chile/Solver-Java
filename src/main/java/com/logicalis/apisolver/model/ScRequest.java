package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ScRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String integrationId;
    @Column(unique = true)
    private String number;
    private String state;
    private boolean active;
    @Column(length = 40000)
    private String shortDescription;
    private String correlationDisplay;
    private String correlationId;
    @Column(length = 80000)
    private String description;
    private String contactType;
    private String stage;
    private String approval;
    private String openedAt;
    private String escalation;
    private String dueDate;
    private String expectedStart;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysUpdatedBy;
    private String sysUpdatedOn;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requested_for", referencedColumnName = "id")
    private SysUser requestedFor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opened_by", referencedColumnName = "id")
    private SysUser openedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "closed_by", referencedColumnName = "id")
    private SysUser closedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_for", referencedColumnName = "id")
    private SysUser taskFor;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private SysUser assignedTo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_item", referencedColumnName = "id")
    private ConfigurationItem configurationItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ci_service", referencedColumnName = "id")
    private CiService businessService;

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

    public String getSysCreatedOn() {
        return sysCreatedOn;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
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

    public SysUser getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(SysUser openedBy) {
        this.openedBy = openedBy;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public SysUser getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(SysUser requestedFor) {
        this.requestedFor = requestedFor;
    }

    public SysUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(SysUser closedBy) {
        this.closedBy = closedBy;
    }

    public SysUser getTaskFor() {
        return taskFor;
    }

    public void setTaskFor(SysUser taskFor) {
        this.taskFor = taskFor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public ConfigurationItem getConfigurationItem() {
        return configurationItem;
    }

    public void setConfigurationItem(ConfigurationItem configurationItem) {
        this.configurationItem = configurationItem;
    }

    public CiService getBusinessService() {
        return businessService;
    }

    public void setBusinessService(CiService businessService) {
        this.businessService = businessService;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}