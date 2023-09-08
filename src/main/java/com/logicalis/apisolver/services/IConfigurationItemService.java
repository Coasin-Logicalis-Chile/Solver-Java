
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ConfigurationItem;

import java.util.List;

public interface IConfigurationItemService {
	
	public List<ConfigurationItem> findAll();
	
	public ConfigurationItem save(ConfigurationItem configurationItem);
	
	public ConfigurationItem findById(Long id);
	
	public void delete(Long id);

	public List<ConfigurationItem> findByActive(boolean active);
	public ConfigurationItem findTopByActive(boolean active);

	public ConfigurationItem findByIntegrationId(String integrationId);
}
