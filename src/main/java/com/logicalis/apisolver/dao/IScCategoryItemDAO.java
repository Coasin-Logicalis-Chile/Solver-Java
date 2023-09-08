package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ScCategoryItem;
import org.springframework.data.repository.CrudRepository;

public interface IScCategoryItemDAO extends CrudRepository<ScCategoryItem, Long> {
    public ScCategoryItem findTopByActive(boolean active);
    public ScCategoryItem findByIntegrationId(String integrationId);
}
