package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Catalog;
import org.springframework.data.repository.CrudRepository;

public interface ICatalogDAO extends CrudRepository<Catalog, Long> {
    public Catalog findTopByActive(boolean active);
    public Catalog findByIntegrationId(String integrationId);
}
