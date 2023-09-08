
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Catalog;

import java.util.List;

public interface ICatalogService {
	
	public List<Catalog> findAll();
	
	public Catalog save(Catalog catalog);
	
	public Catalog findById(Long id);
	
	public void delete(Long id);

	public List<Catalog> findByActive(boolean active);
	public Catalog findTopByActive(boolean active);
	public Catalog findByIntegrationId(String integrationId);
}