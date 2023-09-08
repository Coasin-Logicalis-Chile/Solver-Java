package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.SysGroup;
import com.logicalis.apisolver.model.SysGroupFields;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISysGroupDAO extends CrudRepository<SysGroup, Long> {
    public SysGroup findTopByActive(boolean active);

    public SysGroup findByIntegrationId(String integrationId);

    public SysGroup findByActiveAndName(boolean Active, String name);

    @Query(value = "SELECT\n" +
            "a.id,\n" +
            "a.integration_id as integrationId,\n" +
            "a.name\n" +
            "FROM sys_group a\n" +
            "INNER JOIN company b ON a.company = b.id \n" +
            "WHERE (?1 = 0 OR a.company = ?1)\n" +
            "AND a.active IS TRUE\n" +
            "ORDER BY a.name ASC", nativeQuery = true)
    public List<SysGroupFields> findSysGroupsByFilters(Long company);


    @Query(value = "SELECT DISTINCT\n" +
            "a.id,\n" +
            "a.integration_id as integrationId,\n" +
            "a.name\n" +
            "FROM sys_group a\n" +
            "INNER JOIN company b ON a.company = b.id\n" +
            "INNER JOIN sys_user_group c ON a.id = c.sys_group \n" +
            "WHERE (?1 = 0 OR a.company = ?1)\n" +
            "AND (?2 = 0 OR c.sys_user = ?2)\n" +
            "AND c.active IS TRUE\n" +
            "AND a.active IS TRUE\n" +
            "ORDER BY a.name ASC", nativeQuery = true)
    public List<SysGroupFields> findSysGroupsByFilters(Long company, Long sysUser);

}
