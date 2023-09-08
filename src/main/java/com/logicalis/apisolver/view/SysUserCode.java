package com.logicalis.apisolver.view;

import java.io.Serializable;

public class SysUserCode implements Serializable {


    Long id;

    String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
