package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnDepartment;
import org.springframework.data.repository.CrudRepository;

public interface ISnDepartmentDAO extends CrudRepository<SnDepartment, Long> {
    public SnDepartment findTopByActive(boolean active);
}
