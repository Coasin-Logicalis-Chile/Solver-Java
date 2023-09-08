package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Choice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String element;
    private String language;
    private String name;
    private String label;
    @Column(unique = true)
    private String integrationId;
    private String value;
    private boolean inactive;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;
    @Column(columnDefinition = "boolean default false")
    private boolean solver;
    private String solverDependentValue;

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

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getSolverDependentValue() {
        return solverDependentValue;
    }

    public void setSolverDependentValue(String solverDependentValue) {
        this.solverDependentValue = solverDependentValue;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}