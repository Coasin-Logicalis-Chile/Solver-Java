package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCatalog;
import org.springframework.data.repository.CrudRepository;

public interface ISnCatalogDAO extends CrudRepository<SnCatalog, Long> {
    public SnCatalog findTopByActive(boolean active);
}
