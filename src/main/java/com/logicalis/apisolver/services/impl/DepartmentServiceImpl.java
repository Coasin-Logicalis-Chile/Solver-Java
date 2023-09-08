
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IDepartmentDAO;
import com.logicalis.apisolver.model.Department;
import com.logicalis.apisolver.services.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private IDepartmentDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return (List<Department>) dao.findAll();
    }

    @Override
    public Department save(Department department) {
        return dao.save(department);
    }

    @Override
    public Department findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<Department> findByActive(boolean active) {
        return null;
    }

    @Override
    public Department findTopByActive(boolean active) {
        return (Department) dao.findTopByActive(active);
    }

    @Override
    public Department findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

}
