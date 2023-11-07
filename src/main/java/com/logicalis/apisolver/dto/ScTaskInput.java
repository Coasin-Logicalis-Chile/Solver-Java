package com.logicalis.apisolver.dto;

import com.logicalis.apisolver.model.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScTaskInput{
    private Long id;
    private String actionStatus;
    private boolean active;
    private String activityDue;
    private String additionalAssigneeList;
    private String approval;
    private String approvalHistory;
    private String approvalSet;
    private String businessDuration;
    private String calendarDuration;
    private String calendarStc;
    private String closeNotes;
    private String closedAt;
    private String comments;
    private String commentsAndWorkNotes;
    private String contactType;
    private String contract;
    private String correlationDisplay;
    private String correlationId;
    private String deliveryPlan;
    private String deliveryTask;
    private String description;
    private String dueDate;
    private String escalation;
    private String expectedStart;
    private String followUp;
    private String groupList;
    private String impact;
    private String integrationId;
    private String knowledge;
    private String madeSla;
    private String needsAttention;
    private String number;
    private String openedAt;
    private String priority;
    private String reassignmentCount;
    private String routeReason;
    private String serviceOffering;
    private String shortDescription;
    private String skills;
    private String slaDue;
    private String state;
    private String sysClassName;
    private String sysCreatedBy;
    private String sysCreatedOn;
    private String sysModCount;
    private String sysUpdatedBy;
    private String sysUpdatedOn;
    private String taskEffectiveNumber;
    private String timeWorked;
    private String universalRequest;
    private String uponApproval;
    private String uponReject;
    private String urgency;
    private String userInput;
    private String workEnd;
    private String workStart;
    @Builder.Default
    private boolean scaling=false;
    @Builder.Default
    private boolean solverFlagAssignedTo= false;
    @Builder.Default
    private boolean solverFlagClosedBy=false;
    private String reasonPending;
    @Builder.Default
    private String scRequestIntegrationId="default";
    @Builder.Default
    private String scRequestItemIntegrationId="default";
    private SysUser assignedTo;

    private SysUser solverAssignedTo;

    private SysUser scalingAssignedTo;

    private SysGroup assignmentGroup;

    private SysGroup scalingAssignmentGroup;

    private SysUser closedBy;

    private SysUser solverClosedBy;

    private ConfigurationItem configurationItem;

    private Company company;

    private SysUser openedBy;

    private ScRequest scRequest;

    private ScRequestItem scRequestItem;

    private Domain domain;

    private Location location;

    private SysUser taskFor;

    private CiService businessService;

    private Catalog catalog;

    private SysUser requestedFor;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
