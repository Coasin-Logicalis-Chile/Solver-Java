package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCatalogLine;
import org.springframework.data.repository.CrudRepository;

public interface ISnCatalogLineDAO extends CrudRepository<SnCatalogLine, Long> {
    public SnCatalogLine findTopByActive(boolean active);
}
