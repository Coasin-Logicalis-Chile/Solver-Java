package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ScCategoryItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String integrationId;
    private String name;
    private int sequence;
    @Column(length = 80000)
    private String description;
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "catalog", referencedColumnName = "id")
    private Catalog catalog;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sc_category", referencedColumnName = "id")
    private ScCategory scCategory;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public ScCategory getScCategory() {
        return scCategory;
    }

    public void setScCategory(ScCategory scCategory) {
        this.scCategory = scCategory;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}