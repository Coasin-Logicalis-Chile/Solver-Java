
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.CatalogLine;

import java.util.List;

public interface ICatalogLineService {
	
	public List<CatalogLine> findAll();
	
	public CatalogLine save(CatalogLine catalogLine);
	
	public CatalogLine findById(Long id);
	
	public void delete(Long id);

	public List<CatalogLine> findByActive(boolean active);
	public CatalogLine findTopByActive(boolean active);

	public CatalogLine findByIntegrationId(String integrationId);
}
