
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ScCategory;

import java.util.List;

public interface IScCategoryService {

    public List<ScCategory> findAll();

    public ScCategory save(ScCategory scCategory);

    public ScCategory findById(Long id);

    public void delete(Long id);

    public List<ScCategory> findByActive(boolean active);

    public ScCategory findTopByActive(boolean active);


    public ScCategory findByIntegrationId(String integrationId);
}
