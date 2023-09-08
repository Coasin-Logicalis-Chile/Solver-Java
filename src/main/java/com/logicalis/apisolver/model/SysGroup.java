package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SysGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String integrationId;
    private String name;
    private boolean active;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;
    @Column(columnDefinition = "boolean default false")
    private boolean solver;

    public boolean getSolver() {
        return solver;
    }

    public boolean isSolver() {
        return solver;
    }

    public void setSolver(boolean solver) {
        this.solver = solver;
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

    public boolean getActive() {
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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}