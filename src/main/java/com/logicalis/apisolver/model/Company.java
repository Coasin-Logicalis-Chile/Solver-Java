package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String integrationId;
    private String name;
    private boolean active;
    @Column(columnDefinition = "boolean default false")
    private boolean solver;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @Column(columnDefinition = "int8 default 0")
    private Long password_expiration_days;

    @Column(columnDefinition = "boolean default false")
    private boolean password_expiration;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean getSolver() {
        return solver;
    }

    public void setSolver(boolean solver) {
        this.solver = solver;
    }


    public Long getPasswordExpirationDays() {
        return password_expiration_days;
    }

    public void setPasswordExpirationDays(Long password_expiration_days) {
        this.password_expiration_days = password_expiration_days;
    }


    public boolean getPasswordExpiration() {
        return password_expiration;
    }

    public void setPasswordExpiration(boolean password_expiration) {
        this.password_expiration = password_expiration;
    }



    private static final long serialVersionUID = 1L;

}