package com.logicalis.apisolver.model.servicenow;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SnLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private String sys_id;
    private String name;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
    private boolean active;


    public String getsys_id() {
        return sys_id;
    }

    public void setsys_id(String sys_id) {
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}