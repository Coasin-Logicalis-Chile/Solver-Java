package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.CatalogLine;
import org.springframework.data.repository.CrudRepository;

public interface ICatalogLineDAO extends CrudRepository<CatalogLine, Long> {
    public CatalogLine findTopByActive(boolean active);
    public CatalogLine findByIntegrationId(String integrationId);

}
