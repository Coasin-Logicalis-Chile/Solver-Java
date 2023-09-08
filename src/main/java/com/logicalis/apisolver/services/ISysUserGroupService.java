
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.SysUserGroup;
import com.logicalis.apisolver.model.SysUserGroupFields;

import java.util.List;

public interface ISysUserGroupService {

    public List<SysUserGroup> findAll();

    public SysUserGroup save(SysUserGroup sysUserGroup);

    public SysUserGroup findById(Long id);

    public void delete(Long id);

    public List<SysUserGroup> findByActive(boolean active);

    public SysUserGroup findTopByActive(boolean active);

    public SysUserGroup findByIntegrationId(String integrationId);

    public List<SysUserGroupFields> findUserForGroupByFilters(Long company);
}
