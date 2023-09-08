
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ICompanyDAO;
import com.logicalis.apisolver.model.Company;
import com.logicalis.apisolver.services.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements ICompanyService {

	@Autowired
	private ICompanyDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Company> findAll() {
		return (List<Company>) dao.findAll();
	}

	@Override
	public Company save(Company company) {
		return dao.save(company);
	}

	@Override
	public Company findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Company> findByActive(boolean active) {
		return null;
	}

	@Override
	public Company findTopByActive(boolean active) {	return (Company) dao.findTopByActive(active);	}

	@Override
	public Company findByIntegrationId(String integrationId) { return dao.findByIntegrationId(integrationId); }


}
