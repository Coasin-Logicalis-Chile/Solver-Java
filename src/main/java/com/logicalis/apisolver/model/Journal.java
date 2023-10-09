package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Journal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String origin;
    private String element;
    private String name;
    @Column(length = 1600000)
    private String value;
    @Column(columnDefinition = "boolean default false")
    private boolean parse;
    @Column(columnDefinition = "boolean default false")
    private boolean reviewed;
    private String createdOn;
    @Column(unique = true)
    private String integrationId;
    private String internalCheckpoint;
    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "incident", referencedColumnName = "id")
    private Incident incident;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scRequestItem", referencedColumnName = "id")
    private ScRequestItem scRequestItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scRequest", referencedColumnName = "id")
    private ScRequest scRequest;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private SysUser createBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null && !value.equals("") )
            if (value.toLowerCase().contains("<html>")) {
                value = "<html>".concat(value.split("<html>")[1]);
            }
        this.value = value;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public SysUser getCreateBy() {
        return createBy;
    }

    public void setCreateBy(SysUser createBy) {
        this.createBy = createBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public ScRequestItem getScRequestItem() {
        return scRequestItem;
    }

    public void setScRequestItem(ScRequestItem scRequestItem) {
        this.scRequestItem = scRequestItem;
    }

    public boolean getParse() {
        return parse;
    }

    public boolean isParse() {
        return parse;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public boolean getReviewed() {
        return reviewed;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public ScRequest getScRequest() {
        return scRequest;
    }

    public void setScRequest(ScRequest scRequest) {
        this.scRequest = scRequest;
    }

    public String getInternalCheckpoint() {
        return internalCheckpoint;
    }

    public void setInternalCheckpoint(String internalCheckpoint) {
        this.internalCheckpoint = internalCheckpoint;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}
