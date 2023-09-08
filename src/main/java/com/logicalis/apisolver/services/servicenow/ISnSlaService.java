
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSla;

import java.util.List;

public interface ISnSlaService {
	
	public List<SnSla> findAll();
	
	public SnSla save(SnSla snSla);
	
	public SnSla findById(Long id);
	
	public void delete(Long id);

	public List<SnSla> findByActive(boolean active);
	public SnSla findTopByActive(boolean active);
}
