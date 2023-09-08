package com.logicalis.apisolver.model.servicenow;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SnChoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String element;
    private String language;
    private String name;
    private String label;
    private String sys_id;
    private String value;
    private boolean inactive;
    private boolean u_solver;
    private String u_solver_dependent_value;

    public boolean getU_Solver() {
        return u_solver;
    }

    public boolean isU_solver() {
        return u_solver;
    }

    public void setU_solver(boolean u_solver) {
        this.u_solver = u_solver;
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

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
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

    public String getU_solver_dependent_value() {
        return u_solver_dependent_value;
    }

    public void setU_solver_dependent_value(String u_solver_dependent_value) {
        this.u_solver_dependent_value = u_solver_dependent_value;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}