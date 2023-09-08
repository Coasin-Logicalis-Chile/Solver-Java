package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SysUserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean active;
    @Column(unique=true)
    private String integrationId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sys_user", referencedColumnName = "id")
    private SysUser sysUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sys_group", referencedColumnName = "id")
    private SysGroup sysGroup;

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

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public SysGroup getSysGroup() {
        return sysGroup;
    }

    public void setSysGroup(SysGroup sysGroup) {
        this.sysGroup = sysGroup;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}