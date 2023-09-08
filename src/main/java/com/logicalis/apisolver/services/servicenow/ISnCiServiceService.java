
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCiService;

import java.util.List;

public interface ISnCiServiceService {
	
	public List<SnCiService> findAll();
	
	public SnCiService save(SnCiService snCiService);
	
	public SnCiService findById(Long id);
	
	public void delete(Long id);

	public List<SnCiService> findByActive(boolean active);
	public SnCiService findTopByActive(boolean active);
}
