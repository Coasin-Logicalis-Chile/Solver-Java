
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnContract;

import java.util.List;

public interface ISnContractService {
	
	public List<SnContract> findAll();
	
	public SnContract save(SnContract snContract);
	
	public SnContract findById(Long id);
	
	public void delete(Long id);

	public List<SnContract> findByActive(boolean active);
	public SnContract findTopByActive(boolean active);
}
