package com.logicalis.apisolver.view;

import java.io.Serializable;

public class JournalSolver implements Serializable {
    private Long id;
    private String name;
    private String value;
    private String origin;
    private String element;
    private Long createBy;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null && value != "")
            if (value.toLowerCase().contains("<html>")) {
                value = "<html>".concat(value.split("<html>")[1]);
            }
        this.value = value;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
}
