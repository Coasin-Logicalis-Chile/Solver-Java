package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class BusinessRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String label;
    private String stringValue;
    @Column(columnDefinition = "int default 0")
    private int intValue;
    private Long longValue;
    private boolean active;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type", referencedColumnName = "id")
    private TypeBusinessRule type;
    @Column(columnDefinition = "boolean default false")
    private boolean global;
    @Column(columnDefinition = "boolean default false")
    private boolean init;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "business_rule_company", joinColumns = @JoinColumn(name = "business_rule"), inverseJoinColumns = @JoinColumn(name = "company"), uniqueConstraints = {
            @UniqueConstraint(columnNames = {"business_rule", "company"})})
    private List<Company> companies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public TypeBusinessRule getType() {
        return type;
    }

    public void setType(TypeBusinessRule type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}