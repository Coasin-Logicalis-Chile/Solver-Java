package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSysUserGroup;
import org.springframework.data.repository.CrudRepository;

public interface ISnSysUserGroupDAO extends CrudRepository<SnSysUserGroup, Long> {
    public SnSysUserGroup findTopByActive(boolean active);
}
