
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Company;

import java.util.List;

public interface ICompanyService {
	
	public List<Company> findAll();
	
	public Company save(Company company);
	
	public Company findById(Long id);
	
	public void delete(Long id);

	public List<Company> findByActive(boolean active);
	public Company findTopByActive(boolean active);
	public Company findByIntegrationId(String integrationId);
}
