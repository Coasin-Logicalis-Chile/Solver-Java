package com.logicalis.apisolver.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface IncidentFields {
    Long getId();

    boolean isActive();

    String getCategory();

    String getSubcategory();

    String getNumber();

    String getShort_description();

    String getState();

    String getPriority();

    String getAmbite_level();

    String getPlatform_level();

    String getService_level();

    String getSpecification_level();

    String getAssignment_group();

    boolean getVip();

    String getCreated_on();

    String getUpdated_on();

    String getAssigned_to();

    String getCaller();

    String getIncident_parent();

    int getCount_parent();

    String getParent();

    String getUser_type();
}
