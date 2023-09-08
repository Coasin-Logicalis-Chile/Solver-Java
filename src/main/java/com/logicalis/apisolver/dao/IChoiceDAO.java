package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Choice;
import com.logicalis.apisolver.model.ChoiceFields;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IChoiceDAO extends CrudRepository<Choice, Long> {
    public Choice findTopByInactive(boolean inactive);

    @Query(value = "SELECT label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM choice\n" +
            "WHERE name = 'incident'\n" +
            "AND element IN ('state', 'close_code', 'incident_state', 'u_pending_reason')\n" +
            "AND inactive IS false\n" +
            "UNION ALL\n" +
            "SELECT label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM   choice\n" +
            "WHERE name = 'task'\n" +
            "AND element IN ('priority', 'impact', 'urgency')\n" +
            "AND inactive IS false\n" +
            "ORDER  BY element,\n" +
            "label", nativeQuery = true)
    public List<ChoiceFields> choicesByIncident();

    @Query(value = "SELECT \n" +
            "label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM choice\n" +
            "WHERE name = 'sc_req_item'\n" +
            "AND element IN ( 'state', 'stage' )\n" +
            "AND inactive IS false\n" +
            "UNION ALL\n" +
            "SELECT \n" +
            "label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM   choice\n" +
            "WHERE name = 'task'\n" +
            "AND element IN ( 'priority' )\n" +
            "AND inactive IS false\n" +
            "ORDER  BY element,\n" +
            "label", nativeQuery = true)
    public List<ChoiceFields> choicesByScRequestItem();

    @Query(value = "SELECT label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM choice\n" +
            "WHERE name = 'task'\n" +
            "AND element IN ('state','stage','priority','impact','urgency')\n" +
            "AND inactive IS false\n" +
            "UNION ALL\n" +
            "SELECT \n" +
            "label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM choice\n" +
            "WHERE name = 'sc_task'\n" +
            "AND element IN ( 'u_pending_reason' )\n" +
            "AND inactive IS false\n" +
            "ORDER BY element,\n" +
            "label", nativeQuery = true)
    public List<ChoiceFields> choicesByScTask();

    public Choice findByIntegrationId(String integrationId);
    public Choice findByValueAndElementAndName(String value,String element,String name);
    @Query(value = "\tSELECT label,\n" +
            "value,\n" +
            "element,\n" +
            "solver,\n" +
            "solver_dependent_value AS solverDependentValue\n" +
            "FROM choice\n" +
            "WHERE name = ?1\n" +
            "AND element = ?2\n" +
            "AND value = ?3\n"+
            "AND inactive IS false" , nativeQuery = true)
    public ChoiceFields findByFilters(String name, String element, String value);
}
