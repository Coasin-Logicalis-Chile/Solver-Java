package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ScCategory;
import org.springframework.data.repository.CrudRepository;

public interface IScCategoryDAO extends CrudRepository<ScCategory, Long> {
    public ScCategory findTopByActive(boolean active);
    public ScCategory findByIntegrationId(String integrationId);
}
