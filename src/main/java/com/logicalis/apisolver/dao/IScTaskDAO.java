package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScTask;
import com.logicalis.apisolver.model.ScTaskFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IScTaskDAO extends CrudRepository<ScTask, Long> {
    public ScTask findTopByActive(boolean active);

    public ScTask findByIntegrationId(String integrationId);

    @Query(value = "SELECT DISTINCT a.id AS id,\n" +
    "a.number AS number,\n" +
    "a.short_description AS short_description,\n" +
    "f.label AS state,\n" +
    "b.number AS request,\n" +
    "c.number AS request_item,\n" +
    "h.name AS business_service,\n" +
    "a.active AS active,\n" +
    "g.label AS priority,\n" +
    "i.name AS configuration_item,\n" +
    "d.name AS assignment_group,\n" +
    "a.created_on AS created_on,\n" +
    "a.updated_on AS updated_on, \n" +
    "k.name  AS assigned_to,\n" +
    "l.name  AS requested_for,\n" +
    "COALESCE(j.vip,false) AS vip,\n" +
    "j.user_type AS user_type,\n" +
    "m.name AS opened_by,\n" +
    "a.closed_at AS closed_at\n" +
    "FROM sc_task a\n" +
    "INNER JOIN sc_request    b ON a.sc_request = b.id\n" +
    "INNER JOIN sc_request_item  c ON a.sc_request_item = c.id\n" +
    "INNER JOIN sys_group d ON a.assignment_group = d.id\n" +
    "INNER JOIN sys_user_group e ON e.sys_group = d.id\n" +
    "LEFT OUTER JOIN choice     f ON a.state = f.value AND  f.name = 'task' AND f.element = 'state'\n" +
    "LEFT OUTER JOIN choice     g ON a.priority = g.value AND g.name = 'task' AND g.element = 'priority'\n" +
    "LEFT OUTER JOIN ci_service    h ON a.ci_service = h.id\n" +
    "LEFT OUTER JOIN configuration_item i ON a.configuration_item = i.id\n" +
    "LEFT OUTER JOIN sys_user   j ON a.task_for = j.id\n" +
    "LEFT OUTER JOIN sys_user   k ON a.assigned_to = k.id\n" +
    "LEFT OUTER JOIN sys_user   l ON a.requested_for = l.id\n" +
    "LEFT OUTER JOIN sys_user   m ON a.opened_by = m.id\n" +
    "WHERE (?1 = '' OR UPPER(a.number " +
    "|| coalesce(a.short_description, '') " +
    "|| coalesce(a.integration_id, '') " +
    "|| coalesce(f.label, '') " +
    "|| coalesce(b.number, '') " +
    "|| coalesce(c.number, '') " +
    "|| coalesce(h.name, '') " +
    "|| coalesce(i.name, '') " +
    "|| coalesce(d.name, '') " +
    "|| coalesce(k.name, '') " +
    "|| coalesce(g.label, '')) like ?1)\n" +
    "AND (?2 = 0 OR a.assigned_to = ?2)\n" +
    "AND (?3 = 0 OR a.company = ?3)\n" +
    "AND (?4 = '' OR a.state = ?4)\n" +
    "AND (?5 = false OR (a.assigned_to IS null AND UPPER(f.label) NOT LIKE '%CERRADO%' AND UPPER(f.label) NOT LIKE '%CANCELADO%'))\n" +
    "AND (?6 = false OR UPPER(f.label) like '%RESUELTO%')\n" +
    "AND (?7 = false OR a.scaling IS TRUE)\n" +
    "AND (?8 = 0 OR (a.scaling_assignment_group IN (?8)))\n" +
    "AND (?9 = 0 OR e.sys_user = ?9)\n" +
    "AND (?10 = false OR UPPER(f.label) like '%CERRADO%')\n" +
    "AND (?11 = false OR ((UPPER(f.label) NOT LIKE '%CERRADO%') AND (UPPER(f.label) NOT LIKE '%CANCELADO%')))\n" +
    "AND ((COALESCE(?12) IS null) OR a.assignment_group IN (?12))\n" +
    "AND ((COALESCE(?13) IS null) OR a.assigned_to IN (?13))\n" +
    "AND ((COALESCE(?14) IS null) OR UPPER(f.label) IN (?14))\n" +
    "AND ((COALESCE(?15) IS null) OR UPPER(g.label) IN (?15))\n" +
    "AND ((?16 = '' AND ?17 = '' ) OR (?16 != '' AND ?17 = '' AND (a.created_on BETWEEN TO_TIMESTAMP(?16,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP((CURRENT_DATE || ' 23:59:59'),'YYYY-MM-DD HH24:MI:SS'))) OR (?16 = '' AND ?17 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(('1900-01-01 00:00:00'),'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?17,'YYYY-MM-DD HH24:MI:SS'))) OR (?16 != '' AND ?17 != ''  AND (a.created_on BETWEEN TO_TIMESTAMP(?16,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?17,'YYYY-MM-DD HH24:MI:SS'))))\n" +
    "AND d.active IS TRUE\n" +
    "AND e.active IS TRUE", nativeQuery = true)
public Page<ScTaskFields> findPaginatedScTasksByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    @Query(value = "SELECT DISTINCT a.id AS id,\n" +
            "a.anumber AS number,\n" +
            "a.short_description AS short_description,\n" +
            "a.flabel AS state,\n" +
            "a.bnumber AS request ,\n" +
            "a.cnumber AS request_item,\n" +
            "a.hname AS business_service,\n" +
            "a.dactive AS active,\n" +
            "a.glabel AS priority,\n" +
            "a.iname AS configuration_item,\n" +
            "a.dname AS assignment_group,\n" +
            "a.created_on AS created_on,\n" +
            "a.updated_on AS updated_on,\n" +
            "a.kname  AS assigned_to,\n" +
            "a.lname  AS requested_for,\n" +
            "COALESCE(a.vip,false) AS vip,\n" +
            "a.juser_type AS user_type \n" +
            "FROM view_request a \n" +
            "     INNER JOIN sys_group      d ON a.assignment_group = d.id \n" +
            "     INNER JOIN sys_user_group e ON e.sys_group        = d.id \n" +
            "WHERE (?1 = '' OR UPPER(a.number     \n" +
            "              || coalesce(a.short_description, '')     \n" +
            "              || coalesce(a.integration_id, '')     \n" +
            "              || coalesce(a.flabel, '')     \n" +
            "              || coalesce(a.bnumber, '')     \n" +
            "              || coalesce(a.cnumber, '')     \n" +
            "              || coalesce(a.hname, '')     \n" +
            "              || coalesce(a.iname, '')     \n" +
            "              || coalesce(a.dname, '')     \n" +
            "              || coalesce(a.kname, '')     \n" +
            "              || coalesce(a.glabel, '')) like ?1)\n" +
            "              AND (?2 = 0 OR a.company = ?2)\n" +
            "              AND (?3 = 0 OR (a.scaling_assignment_group IN (SELECT sys_group FROM sys_user_group \n" +
            "                                        WHERE sys_user = ?3)))\n" +
            "              AND ((COALESCE(?4, '') = '') OR a.assignment_group IN (?4))\n" +
            "              AND ((COALESCE(?5, '') = '') OR a.assigned_to IN (?5))\n" +
            "              AND ((COALESCE(?6, '') = '') OR UPPER(a.flabel) IN (?6))\n" +
            "              AND ((COALESCE(?7, '') = '') OR UPPER(a.glabel) IN (?7))\n" +
            "               AND ((?8 = '' AND ?9 = '') OR \n" +
            "                   (?8 != '' AND ?9 = '' AND a.created_on >= TO_TIMESTAMP(?8,'YYYY-MM-DD HH24:MI:SS') AND a.created_on <= TO_TIMESTAMP((CURRENT_DATE || ' 23:59:59'),'YYYY-MM-DD HH24:MI:SS')) OR \n" +
            "                   (?8 = '' AND ?9 != '' AND a.created_on >= TO_TIMESTAMP(('1900-01-01 00:00:00'),'YYYY-MM-DD HH24:MI:SS') AND a.created_on <= TO_TIMESTAMP(?9,'YYYY-MM-DD HH24:MI:SS')) OR \n" +
            "                   (?8 != '' AND ?9 != '' AND a.created_on BETWEEN TO_TIMESTAMP(?8,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?9,'YYYY-MM-DD HH24:MI:SS'))) \n" +
            "              AND a.scaling IS TRUE \n" +
            "              AND a.aactive IS TRUE \n" +
            "              AND UPPER(a.state) NOT LIKE '%RESUELTO%' \n" +
            "              AND d.active IS TRUE \n" +
            "              AND e.active IS TRUE",nativeQuery = true)
    public Page<ScTaskFields> findPaginatedScTasksScalingByFilters(Pageable pageRequest, String filter, Long company, Long sysUser, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    @Query(value = "SELECT DISTINCT a.id AS id ,a.anumber AS number,a.short_description AS short_description\n" +
            " ,a.flabel AS state ,a.bnumber AS request ,a.cnumber AS request_item,a.hname AS business_service\n" +
            " ,a.dactive AS active,a.glabel AS priority ,a.iname AS configuration_item,a.dname AS assignment_group\n" +
            " ,TO_CHAR(a.created_on, 'DD-MM-YYYY HH24:MI:SS') AS created_on\n" +
            " ,TO_CHAR(a.updated_on, 'DD-MM-YYYY HH24:MI:SS') AS updated_on\n" +
            " ,a.kname  AS assigned_to,a.lname  AS requested_for ,COALESCE(a.vip,false) AS vip,a.juser_type AS user_type \n" +
            "FROM view_request a \n" +
            "     INNER JOIN sys_group      d ON a.assignment_group = d.id \n" +
            "     INNER JOIN sys_user_group e ON e.sys_group        = d.id \n" +
            "     INNER JOIN (SELECT DISTINCT  z.id AS sc_task, b.stage  \n" +
            "                 FROM   sc_task z \n" +
            "                    INNER JOIN task_sla b ON z.id = b.sc_task \n" +
            "                 WHERE  stage     = 'in_progress' \n" +
            "                 AND    b.percentage IS NOT NULL \n" +
            "                 and    b.percentage != '' \n" +
            "                 AND Cast(Split_part(COALESCE(b.percentage, '0'), '.', 1) AS INTEGER) > 90 --AND    ((b.percentage::double precision)::int) > 90 \n" +
            ")   k ON k.sc_task = a.id \n" +
            "WHERE (?1 = '' OR UPPER(a.number\n" +
            "              || coalesce(a.short_description, '')|| coalesce(a.integration_id, '')|| coalesce(a.flabel, '') || coalesce(a.bnumber, '')     \n" +
            "              || coalesce(a.cnumber, '')|| coalesce(a.hname, '')|| coalesce(a.iname, '')  || coalesce(a.dname, '')\n" +
            "              || coalesce(a.lname, '')  || coalesce(a.glabel, '')) like ?1)\n" +
            "AND (?2 =  0 OR a.assigned_to = ?2)\n" +
            "AND (?3 =  0 OR a.company = ?3)\n" +
            "AND (?4 = '' OR a.state = ?4)\n" +
            "AND (?5 = false OR (a.assigned_to IS null AND UPPER(a.flabel) NOT LIKE ALL (ARRAY['%CERRADO%','%CANCELADO%'])))\n" +
            "AND (?6 = false OR UPPER(a.flabel) like '%RESUELTO%')\n" +
            "AND (?7 = false OR a.scaling = ?7)\n" +
            "AND (?9 = 0 OR e.sys_user = ?9)\n" +
            "AND (?10 = false OR UPPER(a.flabel) LIKE '%CERRADO%')\n" +
            "AND (?11 = false OR (UPPER(a.flabel) NOT LIKE '%CERRADO%' AND UPPER(a.flabel) NOT LIKE '%CANCELADO%'))\n" +
            "AND (((COALESCE(?12) IS null) OR a.assignment_group IN (?12))\n" +
            "      OR (?8 = 0 OR (a.scaling IS TRUE AND a.scaling_assigned_to = ?8\n" +
            "          AND (UPPER(a.flabel) NOT LIKE ALL (ARRAY['%CERRADO%', '%RESUELTO%']) ))))\n" +
            "AND ((COALESCE(?13) IS null) OR a.assigned_to IN (?13))\n" +
            "AND ((COALESCE(?14) IS null) OR UPPER(a.flabel) IN (?14))\n" +
            "AND ((COALESCE(?15) IS null) OR UPPER(a.glabel) IN (?15))\n" +
            "AND ( (?16 = '' AND ?17 = '' )\n" +
            "     OR (?16 != '' AND ?17 = ''  AND (a.created_on BETWEEN TO_DATE(?16,'YYYY-MM-DD')\n" +
            "              AND CURRENT_DATE ))\n" +
            "     OR (?16 ='' AND ?17!=''  AND (a.created_on BETWEEN TO_DATE('1900-01-01','YYYY-MM-DD')\n" +
            "           AND TO_DATE(?17,'YYYY-MM-DD')))\n" +
            "     OR (?16!='' AND ?17!= ''  AND (a.created_on BETWEEN TO_DATE(?16,'YYYY-MM-DD')\n" +
            "           AND TO_DATE(?17,'YYYY-MM-DD')))" +
            ")\n" +
            "AND UPPER(a.flabel) NOT LIKE ALL (ARRAY['%CERRADO%', '%CANCELADO%'])\n" +
            "AND d.active       IS TRUE\n" +
            "AND e.active       IS TRUE", nativeQuery = true)
    public Page<ScTaskFields> findPaginatedScTasksSLAByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    @Query(value = "select count(DISTINCT a.number)\n" +
            "from  vw_countScTasksByFilters a\n" +
            "where (?1 = 0  OR a.assigned_to = ?1)\n" +
            "AND   (?2 = 0  OR a.company = ?2)\n" +
            "AND   (?8 = 0   OR a.sys_user = ?8)\n" +
            "AND   (?3 = '' OR a.state = ?3)\n" +
            "AND   (?4 = false OR a.assigned_to IS null)\n" +
            "AND   (?5 = false OR UPPER(a.label) like '%RESUELTO%')\n" +
            "AND   (?9 = false OR UPPER(a.label) like '%CERRADO%')\n" +
            "AND   (?10 = false OR UPPER(a.label) NOT LIKE ALL (ARRAY['%CERRADO%', '%CANCELADO%'] ))\n" +
            "AND   (?6  = false OR (a.scaling IS TRUE AND a.active IS TRUE\n" +
            "                    AND exists (select 'x' \n" +
            "                            from sys_user_group u \n" +
            "                            where u.sys_group = a.scaling_assignment_group and u.sys_user = ?7)))", nativeQuery = true)
    public Long countScTasksByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open);

    /*
    @Query(value = "SELECT count(*)\n" +
            "FROM sc_task a \n" +
            "     JOIN sc_request b      ON a.sc_request = b.id\n" +
            "     JOIN sc_request_item c ON a.sc_request_item = c.id\n" +
            "     JOIN choice f          ON a.state = f.value  AND f.name= 'task'\n" +
            "                                                  AND f.element = 'state' \n" +
            "     LEFT JOIN choice g   ON a.priority = g.value AND g.name = 'task' AND g.element = 'priority'\n" +
            "     LEFT JOIN sys_user h ON a.task_for = h.id\n" +
            "     JOIN ( SELECT DISTINCT z.id AS sc_task, w.stage \n" +
            "            FROM sc_task z\n" +
            "                 JOIN task_sla w ON z.id = w.sc_task\n" +
            "            WHERE  w.stage = 'in_progress' \n" +
            "            AND    w.percentage <> '' \n" +
            "            AND    w.percentage IS NOT NULL \n" +
            "            AND Cast(Split_part(COALESCE(w.percentage, '0'), '.', 1) AS INTEGER) > 90) i \n" +
            "                ON i.sc_task = a.id\n" +
            "WHERE a.company = ?1\n" +
            "AND   (upper(f.label) not like all (ARRAY['%CERRADO%', '%CANCELADO%']) )\n" +
            "and  exists  ( select 'x'  \n" +
            "        from sys_group      b \n" +
            "             INNER JOIN sys_user_group c ON c.sys_group = b.id  \n" +
            "       where  b.id = a.assignment_group \n" +
            "       and    b.active is TRUE \n" +
            "       and    c.active is true\n" +
            "       and    c.sys_user = ?2)",nativeQuery = true)
     */
    @Query(value = "SELECT count(*)\n" +
            "FROM sc_task a \n" +
            "     JOIN sc_request b      ON a.sc_request = b.id\n" +
            "     JOIN sc_request_item c ON a.sc_request_item = c.id\n" +
            "     JOIN choice f          ON a.state = f.value  AND f.name= 'task'\n" +
            "                                                  AND f.element = 'state' \n" +
            "     LEFT JOIN choice g   ON a.priority = g.value AND g.name = 'task' AND g.element = 'priority'\n" +
            "     LEFT JOIN sys_user h ON a.task_for = h.id\n" +
            "     JOIN ( SELECT DISTINCT z.id AS sc_task, w.stage \n" +
            "            FROM sc_task z\n" +
            "                 JOIN task_sla w ON z.id = w.sc_task\n" +
            "            WHERE  w.stage = 'in_progress' \n" +
            "            AND    w.percentage <> '' \n" +
            "            AND    w.percentage IS NOT NULL \n" +
            "            AND Cast(Split_part(COALESCE(w.percentage, '0'), '.', 1) AS INTEGER) > 90) i \n" +
            "                ON i.sc_task = a.id\n" +
            "WHERE a.company = ?1\n" +
            "AND   (upper(f.label) not like all (ARRAY['%CERRADO%', '%CANCELADO%']) )\n" +
            "and   exists (select 'x' \n" +
            "                         from   vw_user b \n" +
            "                          where  b.id  = a.assignment_group\n" +
            "                           and    b.sys_user = ?2\n" +
            "                           and    b.company = a.company)",nativeQuery = true)
    public Long countTaskSLAByFilters(Long company, Long assignedTo);

    List<ScTask> findByScRequestItem(ScRequestItem scRequestItem);
}
