package com.logicalis.apisolver.view;

import lombok.Data;

@Data
public class JournalRequest {
    private String element;
    private String element_id;
    private String value;
    private String name;
    private String sys_created_on;
    private String sys_created_by;
    private String sys_id;

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getElement_id() {
        return element_id;
    }

    public void setElement_id(String element_id) {
        this.element_id = element_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null && !value.equals(""))
            if (value.toLowerCase().contains("img") && value.toLowerCase().contains("<html>")) {
                value = "<html>".concat(value.split("<html>")[1]);
            }
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

}
