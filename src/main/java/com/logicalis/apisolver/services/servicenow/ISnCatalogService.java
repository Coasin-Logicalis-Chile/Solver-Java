
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCatalog;

import java.util.List;

public interface ISnCatalogService {
	
	public List<SnCatalog> findAll();
	
	public SnCatalog save(SnCatalog snCatalog);
	
	public SnCatalog findById(Long id);
	
	public void delete(Long id);

	public List<SnCatalog> findByActive(boolean active);
	public SnCatalog findTopByActive(boolean active);
}
