
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnLocation;

import java.util.List;

public interface ISnLocationService {
	
	public List<SnLocation> findAll();
	
	public SnLocation save(SnLocation snLocation);
	
	public SnLocation findById(Long id);
	
	public void delete(Long id);

	public List<SnLocation> findByActive(boolean active);
	public SnLocation findTopByActive(boolean active);
}
