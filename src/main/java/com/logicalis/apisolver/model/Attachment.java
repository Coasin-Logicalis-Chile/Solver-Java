package com.logicalis.apisolver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Attachment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean active;
    @Column(unique = true)
    private String integrationId;
    private String downloadLinkSN;
    private String downloadLink;
    private String fileName;
    private String number;
    private String element;
    private String origin;
    private String extension;
    private String contentType;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysUpdatedBy;
    private String sysUpdatedOn;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "incident", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Incident incident;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_request_item", referencedColumnName = "id")
    private ScRequestItem scRequestItem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_task", referencedColumnName = "id")
    private ScTask scTask;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

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

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getDownloadLinkSN() {
        return downloadLinkSN;
    }

    public void setDownloadLinkSN(String downloadLinkSN) {
        this.downloadLinkSN = downloadLinkSN;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Incident getIncident() {
        return incident;
    }

    public ScRequestItem getScRequestItem() {
        return scRequestItem;
    }

    public void setScRequestItem(ScRequestItem scRequestItem) {
        this.scRequestItem = scRequestItem;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ScTask getScTask() {
        return scTask;
    }

    public void setScTask(ScTask scTask) {
        this.scTask = scTask;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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
    /**
     *
     */
    private static final long serialVersionUID = 1L;

}