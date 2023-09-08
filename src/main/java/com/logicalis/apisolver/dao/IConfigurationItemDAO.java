package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ConfigurationItem;
import org.springframework.data.repository.CrudRepository;

public interface IConfigurationItemDAO extends CrudRepository<ConfigurationItem, Long> {
    public ConfigurationItem findTopByActive(boolean active);

    public ConfigurationItem findByIntegrationId(String integrationId);
}
