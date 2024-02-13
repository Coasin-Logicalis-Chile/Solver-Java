package com.logicalis.apisolver.model;

public interface ScTaskFields {
    Long getId();

    boolean getActive();

    String getNumber();

    String getShort_description();

    String getState();

    String getPriority();

    String getRequest();

    String getRequest_item();

    String getBusiness_service();

    String getConfiguration_item();

    String getAssignment_group();

    boolean getVip();

    String getCreated_on();

    String getUpdated_on();

    String getAssigned_to();

    String getRequested_for();

    String getUser_type();

    String getOpened_by();

    String getClosed_at();
}
