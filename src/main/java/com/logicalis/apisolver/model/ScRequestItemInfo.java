package com.logicalis.apisolver.model;

public interface ScRequestItemInfo {
    Long getId();

    boolean getActive();

    String getApproval();

    String getNumber();

    String getShortDescription();

    String getPriority();

    //String getStage();

    String getState();

    String getAssignedTo();

    String getAssignmentGroup();

    String getCiService();

    String getConfigurationItem();

    String getScCategoryItem();

    String getScRequest();

}
