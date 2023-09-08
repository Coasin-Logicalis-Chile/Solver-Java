package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnConfigurationItem;
import org.springframework.data.repository.CrudRepository;

public interface ISnConfigurationItemDAO extends CrudRepository<SnConfigurationItem, Long> {
    public SnConfigurationItem findTopByActive(boolean active);
}
