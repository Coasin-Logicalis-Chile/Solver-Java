package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CatalogLine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String integrationId;
    private String number;
    private String service;
    private String ambite;
    private String platform;
    private String specification;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_group", referencedColumnName = "id")
    private SysGroup assignmentGroup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_item", referencedColumnName = "id")
    private ConfigurationItem configurationItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ci_service", referencedColumnName = "id")
    private CiService businessService;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "approval_group", referencedColumnName = "id")
    private SysGroup approvalGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAmbite() {
        return ambite;
    }

    public void setAmbite(String ambite) {
        this.ambite = ambite;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
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

    public SysGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(SysGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public SysGroup getApprovalGroup() {
        return approvalGroup;
    }

    public void setApprovalGroup(SysGroup approvalGroup) {
        this.approvalGroup = approvalGroup;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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