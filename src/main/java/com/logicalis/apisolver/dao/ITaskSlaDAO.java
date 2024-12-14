package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.TaskSla;
import com.logicalis.apisolver.model.TaskSlaInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ITaskSlaDAO extends CrudRepository<TaskSla, Long> {
    public TaskSla findTopByActive(boolean active);

    public TaskSla findByIntegrationId(String integrationId);
    @Query(value = "SELECT \n" +
            "d.number,\n" +
            "b.name AS schedule,\n" +
            "c.name AS slaDefinition,\n" +
            "a.type,\n" +
            "e.label AS stage,\n" +
            "a.business_time_left AS businessTimeLeft,\n" +
            "a.business_percentage AS businessPercentage,\n" +
            "a.business_duration AS businessDuration,\n" +
            "a.start_time AS startTime,\n" +
            "a.end_time AS endTime,\n" +
            "f.name assignmentGroup,\n" +
            "a.u_trigger_group triggerGroup\n" +
            "FROM public.task_sla a\n" +
            "LEFT OUTER JOIN cmn_schedule b ON a.schedule = b.id\n" +
            "LEFT OUTER JOIN contract_sla c ON a.sla = c.id\n" +
            "LEFT OUTER JOIN incident d ON a.incident = d.id\n" +
            "LEFT OUTER JOIN choice e ON (a.stage = e.value AND e.element = 'stage' AND e.name = 'task_sla')\n"+
            "LEFT OUTER JOIN sys_group f ON a.assignment_group = f.id\n" +
            "WHERE a.incident = ?1\n" +
            "AND a.active IS TRUE\n" +
            "ORDER  BY a.id DESC", nativeQuery = true)
    public List<TaskSlaInfo> findByIncident(Long id);

    @Query(value = "SELECT \n" +
            "d.number,\n" +
            "b.name AS schedule,\n" +
            "c.name AS slaDefinition,\n" +
            "a.type,\n" +
            "e.label AS stage,\n" +
            "a.business_time_left AS businessTimeLeft,\n" +
            "a.business_percentage AS businessPercentage,\n" +
            "a.business_duration AS businessDuration,\n" +
            "a.start_time AS startTime,\n" +
            "a.end_time AS endTime\n" +
            "FROM public.task_sla a\n" +
            "LEFT OUTER JOIN cmn_schedule b ON a.schedule = b.id\n" +
            "LEFT OUTER JOIN contract_sla c ON a.sla = c.id\n" +
            "LEFT OUTER JOIN sc_request_item d ON a.sc_request_item = d.id\n" +
            "LEFT OUTER JOIN choice e ON (a.stage = e.value AND e.element = 'stage' AND e.name = 'task_sla')\n" +
            "WHERE a.sc_request_item = ?1\n" +
            "AND a.active IS TRUE\n" +
            "ORDER  BY a.id DESC", nativeQuery = true)
    public List<TaskSlaInfo> findByScRequestItem(Long id);

    @Query(value = "SELECT \n" +
            "d.number,\n" +
            "b.name AS schedule,\n" +
            "c.name AS slaDefinition,\n" +
            "a.type,\n" +
            "e.label AS stage,\n" +
            "a.business_time_left AS businessTimeLeft,\n" +
            "a.business_percentage AS businessPercentage,\n" +
            "a.business_duration AS businessDuration,\n" +
            "a.start_time AS startTime,\n" +
            "a.end_time AS endTime\n" +
            "FROM public.task_sla a\n" +
            "LEFT OUTER JOIN cmn_schedule b ON a.schedule = b.id\n" +
            "LEFT OUTER JOIN contract_sla c ON a.sla = c.id\n" +
            "LEFT OUTER JOIN sc_task d ON a.sc_task = d.id\n" +
            "LEFT OUTER JOIN choice e ON (a.stage = e.value AND e.element = 'stage' AND e.name = 'task_sla')\n" +
            "WHERE a.sc_task = ?1\n" +
            "AND a.active IS TRUE\n" +
            "ORDER  BY a.id DESC", nativeQuery = true)
    public List<TaskSlaInfo> findByScTask(Long id);
}
