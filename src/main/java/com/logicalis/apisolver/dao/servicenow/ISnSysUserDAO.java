package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSysUser;
import org.springframework.data.repository.CrudRepository;

public interface ISnSysUserDAO extends CrudRepository<SnSysUser, Long> {
    public SnSysUser findTopByActive(boolean active);
}
