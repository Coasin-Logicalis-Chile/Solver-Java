
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScRequest;

import java.util.List;

public interface ISnScRequestService {
	
	public List<SnScRequest> findAll();
	
	public SnScRequest save(SnScRequest snScRequest);
	
	public SnScRequest findById(Long id);
	
	public void delete(Long id);

	public List<SnScRequest> findByActive(boolean active);
	public SnScRequest findTopByActive(boolean active);
}
