
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnConfigurationItem;

import java.util.List;

public interface ISnConfigurationItemService {
	
	public List<SnConfigurationItem> findAll();
	
	public SnConfigurationItem save(SnConfigurationItem snConfigurationItem);
	
	public SnConfigurationItem findById(Long id);
	
	public void delete(Long id);

	public List<SnConfigurationItem> findByActive(boolean active);
	public SnConfigurationItem findTopByActive(boolean active);
}
