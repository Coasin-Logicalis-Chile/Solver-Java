package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ScRequestItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String integrationId;
    @Column(unique = true)
    private String number;
    private String timeWorked;
    @Column(length = 40000)
    private String shortDescription;
    @Column(length = 80000)
    private String description;
    private String state;
    private String approval;
    private String stage;
    private String priority;
    private String urgency;
    private String escalation;
    private String openedAt;
    private String contactType;
    private String expectedStart;
    private String estimatedDelivery;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private boolean active;
    private String externalTicket;
    @Column(length = 40000)
    private String closeNotes;
    private String closedAt;
    private String reassignmentCount;
    private String incidentParent;
    private String source;
    private String PartnersOcurrences;
    private String correlationId;
    private String correlationDisplay;

    @Column(columnDefinition = "varchar(255) default 'default'")
    private String scRequestIntegrationId;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requested_for", referencedColumnName = "id")
    private SysUser requestedFor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_for", referencedColumnName = "id")
    private SysUser taskFor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_category_item", referencedColumnName = "id")
    private ScCategoryItem scCategoryItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_item", referencedColumnName = "id")
    private ConfigurationItem configurationItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ci_service", referencedColumnName = "id")
    private CiService businessService;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract", referencedColumnName = "id")
    private Contract contract;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private SysUser assignedTo;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opened_by", referencedColumnName = "id")
    private SysUser openedBy;
    @OneToOne(cascade = CascadeType.ALL)

    @JoinColumn(name = "closed_by", referencedColumnName = "id")
    private SysUser closedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_request", referencedColumnName = "id")
    private ScRequest scRequest;

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

    public String getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(String timeWorked) {
        this.timeWorked = timeWorked;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getEscalation() {
        return escalation;
    }

    public void setEscalation(String escalation) {
        this.escalation = escalation;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(String expectedStart) {
        this.expectedStart = expectedStart;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
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

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public SysUser getTaskFor() {
        return taskFor;
    }

    public void setTaskFor(SysUser taskFor) {
        this.taskFor = taskFor;
    }

    public ScCategoryItem getScCategoryItem() {
        return scCategoryItem;
    }

    public void setScCategoryItem(ScCategoryItem scCategoryItem) {
        this.scCategoryItem = scCategoryItem;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public SysGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(SysGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public SysUser getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(SysUser assignedTo) {
        this.assignedTo = assignedTo;
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

    public String getExternalTicket() {
        return externalTicket;
    }

    public void setExternalTicket(String externalTicket) {
        this.externalTicket = externalTicket;
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

    public String getReassignmentCount() {
        return reassignmentCount;
    }

    public void setReassignmentCount(String reassignmentCount) {
        this.reassignmentCount = reassignmentCount;
    }

    public SysUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(SysUser closedBy) {
        this.closedBy = closedBy;
    }

    public String getIncidentParent() {
        return incidentParent;
    }

    public void setIncidentParent(String incidentParent) {
        this.incidentParent = incidentParent;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPartnersOcurrences() {
        return PartnersOcurrences;
    }

    public void setPartnersOcurrences(String partnersOcurrences) {
        PartnersOcurrences = partnersOcurrences;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCorrelationDisplay() {
        return correlationDisplay;
    }

    public void setCorrelationDisplay(String correlationDisplay) {
        this.correlationDisplay = correlationDisplay;
    }

    public String getScRequestIntegrationId() {
        return scRequestIntegrationId;
    }

    public void setScRequestIntegrationId(String scRequestIntegrationId) {
        this.scRequestIntegrationId = scRequestIntegrationId;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}