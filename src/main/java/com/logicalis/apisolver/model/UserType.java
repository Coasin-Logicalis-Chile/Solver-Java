package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;
@Entity
public class UserType implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "active")
    private boolean active;

    @Column(name = "name")
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
