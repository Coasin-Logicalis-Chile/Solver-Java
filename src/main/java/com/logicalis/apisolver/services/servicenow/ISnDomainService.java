
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnDomain;

import java.util.List;

public interface ISnDomainService {
	
	public List<SnDomain> findAll();
	
	public SnDomain save(SnDomain snDomain);
	
	public SnDomain findById(Long id);
	
	public void delete(Long id);

	public List<SnDomain> findByActive(boolean active);
	public SnDomain findTopByActive(boolean active);
}
