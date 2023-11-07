package com.logicalis.apisolver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.SysUser;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//@Conditional(required = {"identification,transactionCode,channelCode,transactionId,appId", "requestId,validationSchema,transactionId,appId",  "requestId,answersContactData,transactionId,appId"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JournalInput{
    private Long id;
    private String origin;
    private String element;
    private String name;
    private String value;
    @Builder.Default
    private boolean parse = false;
    @Builder.Default
    private boolean reviewed = false;
    private String createdOn;
    private String integrationId;
    private String internalCheckpoint;
    private boolean active = true;
    private Incident incident;
    private ScRequestItem scRequestItem;
    private ScRequest scRequest;
    private SysUser createBy;

}
