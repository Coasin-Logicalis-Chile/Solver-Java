
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCatalogLine;

import java.util.List;

public interface ISnCatalogLineService {
	
	public List<SnCatalogLine> findAll();
	
	public SnCatalogLine save(SnCatalogLine snCatalogLine);
	
	public SnCatalogLine findById(Long id);
	
	public void delete(Long id);

	public List<SnCatalogLine> findByActive(boolean active);
	public SnCatalogLine findTopByActive(boolean active);
}
