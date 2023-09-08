package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScRequestItemInfo;
import com.logicalis.apisolver.model.ScTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IScRequestItemDAO extends CrudRepository<ScRequestItem, Long> {

    public ScRequestItem findTopByActive(boolean active);

    public List<ScRequestItem> findByScRequest(ScRequest scRequest);

    public ScRequestItem findByIntegrationId(String integrationId);


    @Query(value = "SELECT \n" +
            "\ta.id, \n" +
            "\ta.active, \n" +
            "\ta.approval, \n" +
            "\ta.number, \n" +
            "\ta.short_description AS shortDescription, \n" +
            "\tg.label AS priority, \n" +
            "\tf.label AS state, \n" +
            "\ti.name AS assignedTo, \n" +
            "\th.name AS assignmentGroup, \n" +
            "\te.name AS ciService, \n" +
            "\td.name AS configurationItem,\n" +
            "\tb.name AS scCategoryItem, \n" +
            "\tc.number AS scRequest\n" +
            "FROM public.sc_request_item a\n" +
            "LEFT OUTER JOIN sc_category_item b ON a.sc_category_item = b.id\n" +
            "LEFT OUTER JOIN sc_request c ON (a.sc_request = c.id AND a.company = c.company)\n" +
            "LEFT OUTER JOIN configuration_item d ON (a.configuration_item = d.id AND a.company = d.company)\n" +
            "LEFT OUTER JOIN ci_service e ON (a.ci_service = e.id AND a.company = e.company)\n" +
            "LEFT OUTER JOIN choice f ON (a.state = f.value AND f.name = 'task' AND f.element = 'state')\n" +
            "LEFT OUTER JOIN choice g ON (a.priority = g.value AND  g.name = 'task' and g.element = 'priority')\n" +
            "LEFT OUTER JOIN sys_group h ON (a.assignment_group = h.id AND a.company = h.company)\n" +
            "LEFT OUTER JOIN sys_user i ON (a.assigned_to = i.id AND a.company = i.company)\n" +
            "WHERE (?1 = '' OR UPPER(coalesce(a.approval, '') \n" +
            "|| coalesce(a.integration_id, '') " +
            "|| coalesce(a.number, '') " +
            "|| coalesce(a.short_description, '') " +
            "|| coalesce(b.name, '') " +
            "|| coalesce(c.number, '') " +
            "|| coalesce(d.name, '') " +
            "|| coalesce(e.name, '') " +
            "|| coalesce(f.label, '') " +
            "|| coalesce(g.label, '') " +
            "|| coalesce(h.name, '') " +
            "|| coalesce(i.name, '') ) like ?1) " +
            "AND (?2 = 0 OR a.assigned_to = ?2) " +
            "AND (?3 = 0 OR a.company = ?3) " +
            "AND (?4 = '' OR a.state = ?4) " +
            "AND (?5 = false OR a.assigned_to IS null) " +
            "AND (?6 = false OR UPPER(f.label) like '%CERRADO%')", nativeQuery = true)
    public Page<ScRequestItemInfo> findPaginatedScRequestItemsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved);

    @Query(value = "SELECT \n" +
            "COUNT(a.id) \n" +
            "FROM public.sc_request_item a\n" +
            "WHERE (?1 = 0 OR a.assigned_to = ?1)\n" +
            "AND (?2 = 0 OR a.company = ?2)\n" +
            "AND (?3 = '' OR a.state = ?3)\n", nativeQuery = true)
    public Long countScRequestItemsByFilters(Long assignedTo, Long company, String state);

    public List<ScRequestItem> findByCompany(String integrationId);
}
