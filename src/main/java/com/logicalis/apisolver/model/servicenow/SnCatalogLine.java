package com.logicalis.apisolver.model.servicenow;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SnCatalogLine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sys_id;
    private String u_number;
    private String u_service;
    private String u_ambite;
    private String u_platform;
    private String u_specification;
    private String sys_updated_by;
    private String sys_created_on;
    private String sys_updated_on;
    private String sys_created_by;
    private boolean u_active;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getU_number() {
        return u_number;
    }

    public void setU_number(String u_number) {
        this.u_number = u_number;
    }

    public String getU_service() {
        return u_service;
    }

    public void setU_service(String u_service) {
        this.u_service = u_service;
    }

    public String getU_ambite() {
        return u_ambite;
    }

    public void setU_ambite(String u_ambite) {
        this.u_ambite = u_ambite;
    }

    public String getU_platform() {
        return u_platform;
    }

    public void setU_platform(String u_platform) {
        this.u_platform = u_platform;
    }

    public String getU_specification() {
        return u_specification;
    }

    public void setU_specification(String u_specification) {
        this.u_specification = u_specification;
    }

    public String getSys_updated_by() {
        return sys_updated_by;
    }

    public void setSys_updated_by(String sys_updated_by) {
        this.sys_updated_by = sys_updated_by;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getSys_updated_on() {
        return sys_updated_on;
    }

    public void setSys_updated_on(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public boolean isU_active() {
        return u_active;
    }

    public void setU_active(boolean u_active) {
        this.u_active = u_active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}