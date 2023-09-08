package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IScRequestDAO extends CrudRepository<ScRequest, Long> {
    public ScRequest findTopByActive(boolean active);

    public ScRequest findByIntegrationId(String integrationId);

    @Query(value = "SELECT\n" +
            "a.id,\n" +
            "a.number,\n" +
            "a.short_description as shortDescription,\n" +
            "c.label as state,\n" +
            "a.category,\n" +
            "a.subcategory,\n" +
            "a.active,\n" +
            "d.label as priority,\n" +
            "a.ambite_level as ambiteLevel,\n" +
            "a.platform_level as platformLevel,\n" +
            "a.service_level as serviceLevel,\n" +
            "a.specification_level as specificationLevel\n" +
            "\n" +
            "FROM incident a\n" +
            "LEFT OUTER JOIN choice c ON a.state = c.value AND  c.name = 'incident' and c.element = 'state'\n" +
            "LEFT OUTER JOIN choice d ON a.priority = d.value AND  d.name = 'task' and d.element = 'priority'" +
            "WHERE (?1 = '' OR UPPER(a.number || a.short_description || a.category || c.label || a.subcategory || d.label || a.ambite_level  || a.platform_level  || a.service_level  || a.specification_level ) like ?1)" +
            "AND (?2 = 0 OR a.assigned_to = ?2)" +
            "AND (?3 = 0 OR a.company = ?3)" +
            "AND (?4 = '' OR a.state = ?4)", nativeQuery = true)
    public Page<ScRequestFields> findPaginatedScRequestsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state);

    @Query(value = "SELECT\n" +
            "count(a.id)" +
            "FROM incident a\n" +
            "LEFT OUTER JOIN choice c ON a.state = c.value AND  c.name = 'incident' and c.element = 'state'\n" +
            "LEFT OUTER JOIN choice d ON a.priority = d.value AND  d.name = 'task' and d.element = 'priority'" +
            "WHERE (?1 = 0 OR a.assigned_to = ?1)" +
            "AND (?2 = 0 OR a.company = ?2)" +
            "AND (?3 = '' OR a.state = ?3)", nativeQuery = true)
    public Long countScRequestsByFilters(Long assignedTo, Long company, String state);

    public List<ScRequest> findByCompany(String integrationId);
}
