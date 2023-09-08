
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IScCategoryDAO;
import com.logicalis.apisolver.model.ScCategory;
import com.logicalis.apisolver.services.IScCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScCategoryServiceImpl implements IScCategoryService {

    @Autowired
    private IScCategoryDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<ScCategory> findAll() {
        return (List<ScCategory>) dao.findAll();
    }

    @Override
    public ScCategory save(ScCategory scCategory) {
        return dao.save(scCategory);
    }

    @Override
    public ScCategory findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<ScCategory> findByActive(boolean active) {
        return null;
    }

    @Override
    public ScCategory findTopByActive(boolean active) {
        return (ScCategory) dao.findTopByActive(active);
    }

    @Override
    public ScCategory findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }
}
