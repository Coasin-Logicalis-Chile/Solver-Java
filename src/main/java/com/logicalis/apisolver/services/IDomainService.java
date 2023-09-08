
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Domain;

import java.util.List;

public interface IDomainService {
	
	public List<Domain> findAll();
	
	public Domain save(Domain domain);
	
	public Domain findById(Long id);
	
	public void delete(Long id);
	//public Domain findByName(String name);
	public Domain findByIntegrationId(String integrationId);
	public List<Domain> findByActive(boolean active);
	public Domain findTopByActive(boolean active);
}
