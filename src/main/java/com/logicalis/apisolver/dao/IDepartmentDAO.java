package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Department;
import org.springframework.data.repository.CrudRepository;

public interface IDepartmentDAO extends CrudRepository<Department, Long> {
    public Department findTopByActive(boolean active);

    public Department findByIntegrationId(String integrationId);
}
