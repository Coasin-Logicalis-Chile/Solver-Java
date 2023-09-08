
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.CiService;

import java.util.List;

public interface ICiServiceService {
	
	public List<CiService> findAll();
	
	public CiService save(CiService ciService);

	public CiService findById(Long id);
	public CiService findByIntegrationId(String integrationId);
	
	public void delete(Long id);

	public List<CiService> findByActive(boolean active);
	public CiService findTopByActive(boolean active);
}
