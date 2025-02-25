package com.logicalis.apisolver.model;

public interface TaskSlaInfo {
    String getNumber();

    String getSchedule();

    String getSlaDefinition();

    String getType();

    String getStage();

    String getBusinessTimeLeft();

    String getBusinessDuration();

    String getBusinessPercentage();

    String getStartTime();

    String getEndTime();

    String getAssignmentGroup();
    
    String getTriggerGroup();
}
