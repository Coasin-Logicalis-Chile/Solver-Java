
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Department;

import java.util.List;

public interface IDepartmentService {

    public List<Department> findAll();

    public Department save(Department department);

    public Department findById(Long id);

    public void delete(Long id);

    public List<Department> findByActive(boolean active);

    public Department findTopByActive(boolean active);

    public Department findByIntegrationId(String integrationId);
}
