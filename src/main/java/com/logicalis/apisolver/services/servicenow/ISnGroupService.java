
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnGroup;

import java.util.List;

public interface ISnGroupService {
	
	public List<SnGroup> findAll();
	
	public SnGroup save(SnGroup snGroup);
	
	public SnGroup findById(Long id);
	
	public void delete(Long id);

	public List<SnGroup> findByActive(boolean active);
	public SnGroup findTopByActive(boolean active);
}
