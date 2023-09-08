package com.logicalis.apisolver.model.servicenow;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SnSysUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private String sys_id;
    private String name;
    private boolean active;
    private boolean vip;
    private String employee_number;
    private String email;
    private String first_name;
    private String last_name;
    private String user_name;
    private String mobile_phone;
    private String sys_created_by;
    private String sys_updated_on;
    private boolean u_solver;
    private String u_user_type;

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
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

    public boolean getVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getSys_updated_on() {
        return sys_updated_on;
    }

    public void setSys_updated_on(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public boolean isVip() {
        return vip;
    }
    public boolean getU_solver() {
        return u_solver;
    }
    public boolean isU_solver() {
        return u_solver;
    }

    public void setU_solver(boolean u_solver) {
        this.u_solver = u_solver;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getU_user_type() {
        return u_user_type;
    }

    public void setU_user_type(String u_user_type) {
        this.u_user_type = u_user_type;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}