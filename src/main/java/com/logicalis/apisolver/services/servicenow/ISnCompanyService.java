
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCompany;

import java.util.List;

public interface ISnCompanyService {
	
	public List<SnCompany> findAll();
	
	public SnCompany save(SnCompany snCompany);
	
	public SnCompany findById(Long id);
	
	public void delete(Long id);

	public List<SnCompany> findByActive(boolean active);
	public SnCompany findTopByActive(boolean active);
}
