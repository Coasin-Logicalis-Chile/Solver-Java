package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.SysUserGroup;
import com.logicalis.apisolver.model.SysUserGroupFields;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISysUserGroupDAO extends CrudRepository<SysUserGroup, Long> {
    public SysUserGroup findTopByActive(boolean active); public
    SysUserGroup findByIntegrationId(String integrationId);
    @Query(value = "SELECT a.sys_group AS sysGroupId,\n" +
            "a.sys_user  AS sysUserId\n" +
            "FROM sys_user_group a\n" +
            "INNER JOIN sys_group b ON a.sys_group = b.id AND b.active IS TRUE\n" +
            "WHERE  (?1 = 0 OR b.company = ?1)\n" +
            "AND a.active IS TRUE\n" +
            "ORDER  BY a.sys_group ASC", nativeQuery = true)
    public List<SysUserGroupFields> findUserForGroupByFilters(Long company);
}
