package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.IncidentFields;
import com.logicalis.apisolver.model.IncidentSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IIncidentDAO extends PagingAndSortingRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {

    public Incident findTopByActive(boolean active);

    /*
    @Query(value = "SELECT DISTINCT a.id AS id,\n" +
            "a.number AS number,\n" +
            "a.short_description AS short_description,\n" +
            "d.label AS state,\n" +
            "a.category AS category,\n" +
            "a.subcategory AS subcategory,\n" +
            "CAST(a.active AS varchar) AS active,\n" +
            "e.label AS priority,\n" +
            "a.ambite_level AS ambite_level,\n" +
            "a.platform_level AS platform_level,\n" +
            "a.service_level AS service_level,\n" +
            "a.specification_level AS specification_level,\n" +
            "b.name AS assignment_group,\n" +
            "TO_CHAR(a.created_on, 'DD-MM-YYYY HH24:MI:SS') AS created_on,\n" +
            "TO_CHAR(a.updated_on, 'DD-MM-YYYY HH24:MI:SS') AS updated_on,\n" +
            "h.name AS assigned_to,\n" +
            "j.name AS caller,\n" +
            "COALESCE(f.vip, false) AS vip,\n" +
            "a.incident_parent AS incident_parent,\n" +
            "a.count_parent AS count_parent,\n" +
            "f.user_type AS user_type,\n" +
            "COALESCE(i.number,'') as parent\n" +
            "FROM incident a\n" +
            "INNER JOIN sys_group b ON a.assignment_group = b.id\n" +
            "INNER JOIN sys_user_group c ON c.sys_group = b.id\n" +
            "INNER JOIN choice d ON a.state = d.value AND  d.name = 'incident' AND d.element = 'state'\n" +
            "INNER JOIN choice g ON a.incident_state = g.value AND  g.name = 'incident' AND g.element = 'incident_state'\n" +
            "LEFT OUTER JOIN choice e ON a.priority = e.value AND  e.name = 'task' AND e.element = 'priority'\n" +
            "LEFT OUTER JOIN sys_user f ON a.task_for = f.id\n" +
            "LEFT OUTER JOIN sys_user h ON a.assigned_to = h.id\n" +
            "LEFT OUTER JOIN incident i ON a.incident_parent = i.integration_id\n" +
            "LEFT OUTER JOIN sys_user j ON a.caller = j.id\n" +
            "WHERE (?1 = '' OR UPPER(a.number " +
            "|| coalesce(a.short_description, '') " +
            "|| coalesce(a.category, '') " +
            "|| coalesce(d.label, '') " +
            "|| coalesce(a.subcategory, '') " +
            "|| coalesce(e.label, '') " +
            "|| coalesce(a.ambite_level, '') " +
            "|| coalesce(a.platform_level, '') " +
            "|| coalesce(a.service_level, '') " +
            "|| coalesce(a.specification_level, '') " +
            "|| coalesce(b.name, '') " +
            "|| coalesce(h.name, '') " +
            "|| a.integration_id ) like ?1)\n" +
            "AND (?2 = 0 OR a.assigned_to = ?2)\n" +
            "AND (?3 = 0 OR a.company = ?3)\n" +
            "AND (?4 = '' OR a.state = ?4)\n" +
            "AND (?5 = false OR (a.assigned_to IS null AND UPPER(d.label) NOT LIKE '%CERRADO%' AND UPPER(d.label) NOT LIKE '%CANCELADO%'))\n" +
            "AND (?6 = false OR UPPER(d.label) like '%RESUELTO%')\n" +
            "AND (?7 = '' OR a.incident_parent = ?7)\n" +
            "AND (?8 = '' OR a.sc_request_parent = ?8)\n" +
            "AND (?9 = '' OR a.sc_request_item_parent = ?9)\n" +
            "AND (?10 = 0 OR c.sys_user = ?10)\n" +
            "AND (?11 = false OR UPPER(d.label) like '%CERRADO%')\n" +
            "AND (?12 = false OR (UPPER(d.label) NOT LIKE '%CERRADO%' AND UPPER(d.label) NOT LIKE '%CANCELADO%' AND UPPER(d.label) NOT LIKE '%RESUELTO%' AND UPPER(g.label) NOT LIKE '%RESUELTO%'))\n" +
            "AND ((COALESCE(?13) IS null) OR a.assignment_group IN (?13))\n" +
            "AND ((COALESCE(?14) IS null) OR a.assigned_to IN (?14))\n" +
            "AND ((COALESCE(?15) IS null) OR UPPER(d.label) IN (?15))\n" +
            "AND ((COALESCE(?16) IS null) OR UPPER(e.label) IN (?16))\n" +
            "AND ((?17 = '' AND ?18 = '' ) OR (?17 != '' AND ?18 = '' AND (a.created_on BETWEEN TO_TIMESTAMP(?17,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP((CURRENT_DATE || ' 23:59:59'),'YYYY-MM-DD HH24:MI:SS'))) OR (?17 = '' AND ?18 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(('1900-01-01 00:00:00'),'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?18,'YYYY-MM-DD HH24:MI:SS'))) OR (?17 != '' AND ?18 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(?17,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?18,'YYYY-MM-DD HH24:MI:SS'))))\n" +
            "AND a.delete IS FALSE\n" +
            "AND b.active IS TRUE", nativeQuery = true)
    */
    @Query(value = "SELECT  distinct  a.id   AS id, \n" +
            "                   a.number AS number, \n" +
            "                   a.short_description AS short_description, \n" +
            "                   d.label  AS state, \n" +
            "                   a.category AS category, \n" +
            "                   a.subcategory AS subcategory, \n" +
            "              CAST(a.active AS varchar) AS active, \n" +
            "              e.label AS priority, \n" +
            "                   a.ambite_level AS ambite_level, \n" +
            "                   a.platform_level AS platform_level, \n" +
            "                   a.service_level AS service_level, \n" +
            "                   a.specification_level AS specification_level, \n" +
            "              'b.name' AS assignment_group, \n" +
            "           TO_CHAR(a.created_on, 'DD-MM-YYYY HH24:MI:SS') AS created_on, \n" +
            "           TO_CHAR(a.updated_on, 'DD-MM-YYYY HH24:MI:SS') AS updated_on, \n" +
            "                   h.name AS assigned_to, \n" +
            "                   j.name AS caller, \n" +
            "          COALESCE(f.vip, false) AS vip, \n" +
            "                   a.incident_parent AS incident_parent, \n" +
            "                   a.count_parent AS count_parent, \n" +
            "                   f.user_type AS user_type, \n" +
            "          COALESCE(i.number,'') as parent            \n" +
            "FROM incident a \n" +
            "     INNER JOIN choice         d ON a.state            = d.value AND  d.name = 'incident' AND d.element = 'state' \n" +
            "     INNER JOIN choice         g ON a.incident_state   = g.value AND  g.name = 'incident' AND g.element = 'incident_state' \n" +
            "     LEFT  OUTER JOIN choice   e ON a.priority         = e.value AND  e.name = 'incident' AND e.element = 'priority' \n" +
            "     LEFT  OUTER JOIN sys_user f ON a.task_for         = f.id \n" +
            "     LEFT  OUTER JOIN sys_user h ON a.assigned_to      = h.id \n" +
            "     LEFT  OUTER JOIN incident i ON a.incident_parent  = i.integration_id \n" +
            "     LEFT  OUTER JOIN sys_user j ON a.caller           = j.id \n" +
            "WHERE (?1= '' OR UPPER(a.number      \n" +
            "              || coalesce(a.short_description, '')    \n" +
            "              || coalesce(a.category, '')    \n" +
            "              || coalesce(d.label, '')    \n" +
            "              || coalesce(a.subcategory, '')    \n" +
            "              || coalesce(e.label, '')    \n" +
            "              || coalesce(a.ambite_level, '')    \n" +
            "              || coalesce(a.platform_level, '')    \n" +
            "              || coalesce(a.service_level, '')    \n" +
            "              || coalesce(a.specification_level, '')    \n" +
            "              || coalesce(h.name, '')    \n" +
            "              || a.integration_id ) like ?1) \n" +
            "              AND (?2 = 0 OR a.assigned_to = ?2) \n" +
            "              AND (?3 = 0 OR a.company = ?3)  \n" +
            "              AND (?4 = '' OR a.state = ?4)   \n" +
            "              AND (?5 = false OR (a.assigned_to IS null \n" +
            "                           AND UPPER(d.label) NOT like ALL (ARRAY['%CERRADO%','%CANCELADO%'] )) )\n" +
            "              AND (?6 = false OR UPPER(d.label) like '%RESUELTO%')  \n" +
            "              AND (?7 = '' OR a.incident_parent = ?7)  \n" +
            "              AND (?8 = '' OR a.sc_request_parent = ?8) \n" +
            "              AND (?9 = '' OR a.sc_request_item_parent = ?9) \n" +
            "              AND (?11 = false OR  UPPER(d.label) like '%CERRADO%') \n" +
            "              AND (?12  = false  OR (UPPER(d.label) NOT like ALL (ARRAY['%CERRADO%','%RESUELTO%','%CANCELADO%'] )\n" +
            "                        AND UPPER(g.label) NOT LIKE '%RESUELTO%')) \n" +
            "              AND ((COALESCE(?13) IS null) OR a.assignment_group IN (?13)) \n" +
            "              AND ((COALESCE(?14) IS null) OR a.assigned_to IN (?14)) \n" +
            "              AND ((COALESCE(?15) IS null) OR UPPER(d.label) IN (?15)) \n" +
            "              AND ((COALESCE(?16) IS null) OR UPPER(e.label) IN (?16)) \n" +
            "              AND ((?17 = '' AND ?18 = '' ) \n" +
            "                     OR (?17 != '' AND ?18 = '' \n" +
            "                     AND (a.created_on BETWEEN TO_DATE(?17,'YYYY-MM-DD')\n" +
            "                                AND CURRENT_DATE)  ) \n" +
            "                      OR (?17 = '' AND ?18 != ''  \n" +
            "                      AND (a.created_on BETWEEN TO_DATE(('1900-01-01'),'YYYY-MM-DD') \n" +
            "                               AND TO_DATE(?18,'YYYY-MM-DD')))\n" +
            "                     OR (?17 != '' AND ?18 != '' \n" +
            "                     AND (a.created_on BETWEEN TO_DATE(?17,'YYYY-MM-DD') \n" +
            "                               AND TO_DATE(?18,'YYYY-MM-DD'))))\n" +
            "              AND a.delete IS FALSE \n" +
            "              AND ((?10 = 0) or exists ( select 'x'  from sys_group      b\n" +
            "                                          INNER JOIN sys_user_group c ON c.sys_group = b.id \n" +
            "                                      where  b.id = a.assignment_group\n" +
            "                                      and    b.active is TRUE\n" +
            "                                      and    c.sys_user = ?10 ) )", nativeQuery = true)
    public Page<IncidentFields> findPaginatedIncidentsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    /*
    @Query(value = "SELECT a.id AS id,\n" +
            "a.number AS number,\n" +
            "a.short_description AS short_description,\n" +
            "d.label AS state,\n" +
            "a.category AS category,\n" +
            "a.subcategory AS subcategory,\n" +
            "a.active AS active,\n" +
            "e.label AS priority,\n" +
            "a.ambite_level AS ambite_level,\n" +
            "a.platform_level AS platform_level,\n" +
            "a.service_level AS service_level,\n" +
            "a.specification_level AS specification_level,\n" +
            "b.name AS assignment_group,\n" +
            "j.name AS caller,\n" +
            "TO_CHAR(a.created_on, 'DD-MM-YYYY HH24:MI:SS') AS created_on,\n" +
            "TO_CHAR(a.updated_on, 'DD-MM-YYYY HH24:MI:SS') AS updated_on,\n" +
            "h.name AS assigned_to,\n" +
            "COALESCE(f.vip, false) AS vip,\n" +
            "a.incident_parent AS incident_parent,\n" +
            "a.count_parent AS count_parent,\n" +
            "f.user_type AS user_type,\n" +
            "COALESCE(i.number,'') as parent\n" +
            "FROM incident a\n" +
            "INNER JOIN sys_group b ON a.assignment_group = b.id\n" +
            "INNER JOIN sys_user_group c ON c.sys_group = b.id\n" +
            "INNER JOIN choice d ON a.state = d.value AND d.NAME = 'incident' AND d.element = 'state'\n" +
            "LEFT OUTER JOIN choice e ON a.priority = e.value AND e.NAME = 'task' AND e.element = 'priority'\n" +
            "LEFT OUTER JOIN sys_user f ON a.task_for = f.id\n" +
            "LEFT OUTER JOIN sys_user h ON a.assigned_to = h.id\n" +
            "LEFT OUTER JOIN incident i ON COALESCE(a.incident_parent, '') = i.integration_id\n" +
            "LEFT OUTER JOIN sys_user j ON a.caller = j.id\n" +
            "INNER JOIN (SELECT DISTINCT\n" +
            "   a.id AS incident,\n" +
            "   b.stage\n" +
            "       FROM   incident a\n" +
            "       INNER JOIN task_sla b ON a.id = b.incident\n" +
            "       WHERE  stage = 'in_progress'\n" +
            "              AND b.percentage IS NOT NULL\n" +
            "              AND b.percentage != ''\n" +
            "              AND Cast(Split_part(COALESCE(b.percentage, '0'), '.', 1) AS INTEGER) > 90)\n" +
            "  g ON coalesce(g.incident, 0) = a.id\n" +
            "WHERE (?1 = '' OR UPPER(a.number\n" +
            "|| coalesce(a.short_description, '')\n" +
            "|| coalesce(a.category, '')\n" +
            "|| coalesce(d.label, '')\n" +
            "|| coalesce(a.subcategory, '')\n" +
            "|| coalesce(e.label, '')\n" +
            "|| coalesce(a.ambite_level, '')\n" +
            "|| coalesce(a.platform_level, '')\n" +
            "|| coalesce(a.service_level, '')\n" +
            "|| coalesce(a.specification_level, '')\n" +
            "|| coalesce(b.name, '')\n" +
            "|| coalesce(h.name, '') " +
            "|| a.integration_id ) like ?1)\n" +
            "AND (?2 = 0 OR a.assigned_to = ?2)\n" +
            "AND (?3 = 0 OR a.company = ?3)\n" +
            "AND (?4 = '' OR a.state = ?4)\n" +
            "AND (?5 = false OR (a.assigned_to IS null AND UPPER(d.label) NOT LIKE '%CERRADO%' AND UPPER(d.label) NOT LIKE '%CANCELADO%'))\n" +
            "AND (?6 = '' OR a.incident_parent = ?6)\n" +
            "AND (?7 = '' OR a.sc_request_parent = ?7)\n" +
            "AND (?8 = '' OR a.sc_request_item_parent = ?8)\n" +
            "AND (?9 = 0 OR c.sys_user = ?9)\n" +
            "AND ((COALESCE(?10) IS null) OR a.assignment_group IN (?10))\n" +
            "AND ((COALESCE(?11) IS null) OR a.assigned_to IN (?11))\n" +
            "AND ((COALESCE(?12) IS null) OR UPPER(d.label) IN (?12))\n" +
            "AND ((COALESCE(?13) IS null) OR UPPER(e.label) IN (?13))\n" +
            "AND ((?14 = '' AND ?15 = '' ) OR (?14 != '' AND ?15 = '' AND (a.created_on BETWEEN TO_TIMESTAMP(?14,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP((CURRENT_DATE || ' 23:59:59'),'YYYY-MM-DD HH24:MI:SS'))) OR (?14 = '' AND ?15 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(('1900-01-01 00:00:00'),'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?15,'YYYY-MM-DD HH24:MI:SS'))) OR (?14 != '' AND ?15 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(?14,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?15,'YYYY-MM-DD HH24:MI:SS'))))\n" +
            "AND upper(d.label) NOT LIKE '%CERRADO%'\n" +
            "AND upper(d.label) NOT LIKE '%CANCELADO%'\n" +
            "AND b.active IS TRUE", nativeQuery = true)
    */
    @Query(value = "SELECT a.id AS id, \n" +
            "              a.number AS number, \n" +
            "              a.short_description AS short_description, \n" +
            "              d.label AS state, \n" +
            "              a.category AS category, \n" +
            "              a.subcategory AS subcategory, \n" +
            "              a.active AS active, \n" +
            "              e.label AS priority, \n" +
            "              a.ambite_level AS ambite_level, \n" +
            "              a.platform_level AS platform_level, \n" +
            "              a.service_level AS service_level, \n" +
            "              a.specification_level AS specification_level, \n" +
            "              b.name AS assignment_group, \n" +
            "              j.name AS caller, \n" +
            "              TO_CHAR(a.created_on, 'DD-MM-YYYY HH24:MI:SS') AS created_on, \n" +
            "              TO_CHAR(a.updated_on, 'DD-MM-YYYY HH24:MI:SS') AS updated_on, \n" +
            "              h.name AS assigned_to, \n" +
            "              COALESCE(f.vip, false) AS vip, \n" +
            "              a.incident_parent AS incident_parent, \n" +
            "              a.count_parent AS count_parent, \n" +
            "              f.user_type AS user_type, \n" +
            "              COALESCE(i.number,'') as parent \n" +
            "              FROM incident a \n" +
            "              INNER JOIN sys_group b ON a.assignment_group = b.id \n" +
            "              INNER JOIN sys_user_group c ON c.sys_group = b.id \n" +
            "              INNER JOIN choice d ON a.state = d.value AND d.NAME = 'incident' AND d.element = 'state' \n" +
            "              LEFT OUTER JOIN choice e ON a.priority = e.value AND e.NAME = 'task' AND e.element = 'priority' \n" +
            "              LEFT OUTER JOIN sys_user f ON a.task_for = f.id \n" +
            "              LEFT OUTER JOIN sys_user h ON a.assigned_to = h.id \n" +
            "              LEFT OUTER JOIN incident i ON COALESCE(a.incident_parent, '') = i.integration_id \n" +
            "              LEFT OUTER JOIN sys_user j ON a.caller = j.id \n" +
            "              INNER JOIN (SELECT DISTINCT  a.id AS incident, b.stage \n" +
            "                          FROM   incident a \n" +
            "                          INNER JOIN task_sla b ON a.id = b.incident \n" +
            "                          WHERE  stage = 'in_progress' \n" +
            "                          AND b.percentage IS NOT NULL \n" +
            "                          AND b.percentage != '' \n" +
            "                          AND Cast(Split_part(COALESCE(b.percentage, '0'), '.', 1) AS INTEGER) > 90) g ON coalesce(g.incident, 0) = a.id\n" +
            "              WHERE (?1 = '' OR UPPER(a.number\n" +
            "              || coalesce(a.short_description, '') \n" +
            "              || coalesce(a.category, '') \n" +
            "              || coalesce(d.label, '') \n" +
            "              || coalesce(a.subcategory, '') \n" +
            "              || coalesce(e.label, '') \n" +
            "              || coalesce(a.ambite_level, '') \n" +
            "              || coalesce(a.platform_level, '') \n" +
            "              || coalesce(a.service_level, '') \n" +
            "              || coalesce(a.specification_level, '') \n" +
            "              || coalesce(b.name, '') \n" +
            "              || coalesce(h.name, '')    \n" +
            "              || a.integration_id ) like ?1) \n" +
            "              AND (?2 = 0 OR a.assigned_to = ?2) \n" +
            "              AND (?3 = 0 OR a.company = ?3) \n" +
            "              AND (?4 = '' OR a.state = ?4) \n" +
            "              AND (?5 = false OR (a.assigned_to IS null \n" +
            "                    and (UPPER(d.label) NOT like ALL (ARRAY['%CERRADO%','%CANCELADO%'] ))))\n" +
            "              AND (?6 = '' OR a.incident_parent = ?6) \n" +
            "              AND (?7 = '' OR a.sc_request_parent = ?7) \n" +
            "              AND (?8 = '' OR a.sc_request_item_parent = ?8) \n" +
            "              AND (?9 = 0 OR c.sys_user = ?9) \n" +
            "              AND (?10 IS null OR a.assignment_group IN (?10)) \n" +
            "              AND (?11 IS null OR a.assigned_to IN (?11)) \n" +
            "              AND (?12 IS null OR UPPER(d.label) IN (?12)) \n" +
            "              AND (?13 IS null OR UPPER(e.label) IN (?13)) \n" +
            "              AND ((?14 = '' AND ?15 = '' ) \n" +
            "                    OR (?14 != '' AND ?15 = '' \n" +
            "                      AND (a.created_on BETWEEN TO_DATE(?14,'YYYY-MM-DD') \n" +
            "                              AND CURRENT_DATE)  ) \n" +
            "                    OR (?14 = '' AND ?15 != ''  \n" +
            "                       AND (a.created_on BETWEEN TO_DATE(('1900-01-01'),'YYYY-MM-DD') \n" +
            "                                 AND TO_DATE(?15,'YYYY-MM-DD'))) \n" +
            "                    OR (?14 != '' AND ?15 != '' \n" +
            "                       AND (a.created_on BETWEEN TO_DATE(?14,'YYYY-MM-DD') \n" +
            "                                AND TO_DATE(?15,'YYYY-MM-DD')))) \n" +
            "              AND upper(d.label) NOT LIKE '%CERRADO%' \n" +
            "              AND upper(d.label) NOT LIKE '%CANCELADO%' \n" +
            "              AND b.active IS TRUE", nativeQuery = true)
    public Page<IncidentFields> findPaginatedIncidentsSlaByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    @Query(value = "SELECT DISTINCT a.id AS id,\n" +
            "a.number AS number,\n" +
            "a.short_description AS short_description,\n" +
            "d.label AS state,\n" +
            "a.category,\n" +
            "a.subcategory,\n" +
            "a.active,\n" +
            "e.label AS priority,\n" +
            "a.ambite_level AS ambite_level,\n" +
            "a.platform_level AS platform_level,\n" +
            "a.service_level AS service_level,\n" +
            "a.specification_level AS specification_level,\n" +
            "COALESCE(b.name,'') AS assignment_group,\n" +
            "COALESCE(f.vip, false) AS vip,\n" +
            "a.incident_parent AS incident_parent,\n" +
            "a.count_parent AS count_parent,\n" +
            "f.user_type AS user_type,\n" +
            "COALESCE(i.number,'') as parent\n" +
            "FROM incident a\n" +
            "LEFT OUTER JOIN sys_group b ON a.assignment_group = b.id\n" +
            "LEFT OUTER JOIN sys_user_group c ON c.sys_group = b.id\n" +
            "LEFT OUTER JOIN choice d ON a.state = d.value AND  d.name = 'incident' AND d.element = 'state'\n" +
            "LEFT OUTER JOIN choice e ON a.priority = e.value AND  e.name = 'task' AND e.element = 'priority'\n" +
            "LEFT OUTER JOIN sys_user f ON a.task_for = f.id\n" +
            "LEFT OUTER JOIN sys_user h ON a.assigned_to = h.id\n" +
            "LEFT OUTER JOIN incident i ON a.incident_parent = i.integration_id\n" +
            "WHERE (?1 = '' OR UPPER(a.number " +
            "|| coalesce(a.short_description, '') " +
            "|| coalesce(a.category, '') " +
            "|| coalesce(d.label, '') " +
            "|| coalesce(a.subcategory, '') " +
            "|| coalesce(e.label, '') " +
            "|| coalesce(a.ambite_level, '') " +
            "|| coalesce(a.platform_level, '') " +
            "|| coalesce(a.service_level, '') " +
            "|| coalesce(a.specification_level, '') " +
            "|| coalesce(b.name, '') " +
            "|| a.integration_id ) like ?1)\n" +
            "AND (?2 = 0 OR a.assigned_to = ?2)\n" +
            "AND (?3 = 0 OR a.company = ?3)\n" +
            "AND (?4 = '' OR a.state = ?4)\n" +
            "AND (?5 = false OR (a.assigned_to IS null AND UPPER(d.label) NOT LIKE '%CERRADO%' AND UPPER(d.label) NOT LIKE '%CANCELADO%'))\n" +
            "AND (?6 = false OR UPPER(d.label) like '%RESUELTO%')\n" +
            "AND (?7 = '' OR a.incident_parent = ?7)\n" +
            "AND (?8 = '' OR a.sc_request_parent = ?8)\n" +
            "AND (?9 = '' OR a.sc_request_item_parent = ?9)\n" +
            "AND (?10 = 0 OR c.sys_user = ?10)\n" +
            "AND (?11 = false OR UPPER(d.label) like '%CERRADO%')\n" +
            "AND (?12 = false OR (UPPER(d.label) NOT LIKE '%CERRADO%' AND UPPER(d.label) NOT LIKE '%CANCELADO%'))\n" +
            "AND a.delete IS FALSE\n" +
            "AND COALESCE(b.active, TRUE) IS TRUE", nativeQuery = true)
    public List<IncidentFields> findIncidentsByFilters(String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open);

    /*
    @Query(value = "SELECT count(*)\n" +
            "FROM view_incidentes  a\n" +
            "WHERE (?1 = 0 OR a.assigned_to = ?1)\n" +
            "AND   (?2 = 0 OR a.company = ?2)\n" +
            "AND   (?3 = '' OR a.state = ?3)\n" +
            "AND   (?4= false OR a.assigned_to IS null)\n" +
            "AND   (?5 = false OR UPPER(a.dlabel) like '%RESUELTO%')\n" +
            "AND   (?6 = false OR UPPER(a.dlabel) like '%CERRADO%')\n" +
            "AND   (?8 = false OR ((UPPER(a.dlabel) NOT LIKE ALL (ARRAY['%CERRADO%', '%CANCELADO%', '%RESUELTO%%']))\n" +
            "                         AND UPPER(a.glabel) NOT LIKE '%RESUELTO%'))\n" +
            "AND   a.delete IS FALSE\n" +
            "AND   (?7 = 0 or exists ( select 'x'\n" +
            "                           from   vw_user b \n" +
            "                           where  b.id  = a.assignment_group\n" +
            "                           and    b.active is true\n" +
            "                           AND    b.sys_user = ?7\n" +
            "                           and    b.company = a.company)) ",nativeQuery = true)
    */
    @Query(value = "SELECT count(a.number) \n" +
            "FROM incident a  \n" +
            "     INNER JOIN choice d ON a.state = d.value          AND  d.name = 'incident' AND d.element = 'state' \n" +
            "     INNER JOIN choice g ON a.incident_state = g.value AND  g.name = 'incident' AND g.element = 'incident_state' \n" +
            "     LEFT OUTER JOIN choice e ON a.priority = e.value  AND  e.name = 'task'    AND e.element = 'priority' \n" +
            "     LEFT OUTER JOIN sys_user f ON a.task_for = f.id \n" +
            "WHERE (?1 = 0 OR a.assigned_to = ?1)\n" +
            "AND (?2 = 0 OR a.company = ?2) \n" +
            "AND (?3 = '' OR a.state = ?3)  \n" +
            "AND (?4 = false OR a.assigned_to IS null)  \n" +
            "AND (?5 = false OR UPPER(d.label) like '%RESUELTO%')  \n" +
            "AND (?6 = false OR UPPER(d.label) like '%CERRADO%')  \n" +
            "AND (?8 = false OR (UPPER(d.label) NOT LIKE ALL (ARRAY['%CERRADO%','%RESUELTO%','%CANCELADO%'] ) \n" +
            "            AND UPPER(g.label) NOT LIKE '%RESUELTO%'))\n" +
            "AND a.delete IS FALSE \n" +
            "AND ((?7 = 0) or exists ( select 'x'  from sys_group      b\n" +
            "                                          INNER JOIN sys_user_group c ON c.sys_group = b.id \n" +
            "                                          where  b.id = a.assignment_group\n" +
            "                                           and    b.active is TRUE\n" +
            "                                           and    c.sys_user = ?7 ) )",nativeQuery = true)
    public Long countIncidentsByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean closed, Long assignedToGroup, boolean open);

    @Query(value = "SELECT count(*) \n" +
            "FROM incident  a \n" +
            "    INNER JOIN      choice d   ON a.state = d.value    AND d.NAME = 'incident' AND d.element = 'state' \n" +
            "    LEFT OUTER JOIN choice e   ON a.priority = e.value AND e.NAME = 'task'     AND e.element = 'priority' \n" +
            "    LEFT OUTER JOIN sys_user f ON a.task_for = f.id \n" +
            "    INNER JOIN (SELECT DISTINCT  a.id AS incident, b.stage \n" +
            "                FROM   incident a \n" +
            "                    INNER JOIN task_sla b ON a.id = b.incident \n" +
            "                    INNER JOIN choice   d ON a.state = d.value AND d.NAME = 'incident' AND d.element = 'state' \n" +
            "                WHERE  stage = 'in_progress' \n" +
            "                AND b.percentage IS NOT NULL \n" +
            "                AND b.percentage != '' \n" +
            "                AND upper(d.label) NOT LIKE '%CERRADO%' \n" +
            "                AND Cast(Split_part(COALESCE(b.percentage, '0'), '.', 1) AS INTEGER) > 90)   g ON COALESCE(g.incident,0) = a.id \n" +
            "WHERE a.company = ?1\n" +
            "AND   upper(d.label) NOT LIKE '%CERRADO%' \n" +
            "AND   upper(d.label) NOT LIKE '%CANCELADO%' \n" +
            "and   exists ( select 'x' \n" +
            "               from   vw_user b \n" +
            "               where  b.id  = a.assignment_group\n" +
            "               and    b.active is true\n" +
            "               and    b.sys_user = ?2\n" +
            "               and    b.company = a.company) ",nativeQuery = true)
    public Long countIncidentsSLAByFilters(Long company, Long assignedTo);

    @Query(value = "SELECT a.number,\n" +
            "to_char(a.closed, 'day') AS day,\n" +
            "a.closed_at\n" +
            "FROM   incident AS a\n" +
            "INNER JOIN choice   AS b ON a.state = b.value\n" +
            "INNER JOIN sys_group c ON a.assignment_group = c.id\n" +
            "INNER JOIN sys_user_group d ON d.sys_group = c.id\n" +
            "WHERE (?1 = 0 OR a.assigned_to = ?1)\n" +
            "AND (?2 = 0 OR a.company = ?2)\n" +
            "AND (?3 = '' OR a.state = ?3)\n" +
            "AND (?4 = false OR a.assigned_to IS null)\n" +
            "AND (?5 = false OR UPPER(b.label) like '%CERRADO%')\n" +
            "AND ((?6 = '' AND ?7 = '') OR (a.closed BETWEEN TO_TIMESTAMP(?6,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?7,'YYYY-MM-DD HH24:MI:SS')))\n" +
            "AND (?8 = 0 OR a.closed_by = ?8)\n" +
            "AND c.active IS TRUE", nativeQuery = true)
    /*
    @Query(value = "SELECT count(*) from view_incidentes a\n" +
            "  where (?3 = '' OR a.state = ?3)\n" +
            "  and (?1= 0 OR a.assigned_to = ?1)\n" +
            "  AND (?2 = 0 OR a.company = ?2)\n" +
            "  AND (?4 = false OR a.assigned_to is null)\n" +
            "  AND (?5 = false OR  UPPER(a.dlabel) like '%RESUELTO%')\n" +
            "  AND (?6 = false OR  UPPER(a.dlabel) like '%CERRADO%')\n" +
            "  AND (false = false OR (UPPER(a.dlabel) NOT LIKE ALL (ARRAY['%CERRADO%', '%CANCELADO%', '%RESUELTO%%'])\n" +
            "            AND UPPER(a.glabel) NOT LIKE '%RESUELTO%'))\n" +
            "  and (?7 = 0 or exists ( select 'x'\n" +
            "           from   vw_user b\n" +
            "           where  b.id  = a.assignment_group\n" +
            "           and    b.active is true\n" +
            "           AND    b.sys_user = ?7\n" +
            "           and    b.company = a.company))", nativeQuery = true)
    */
    public List<IncidentSummary> findBySummary(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String currentDate, String lastDate, Long closedBy);

    public Incident findByIntegrationId(String integrationId);

    public List<Incident> findByCompany(String integrationId);


}
