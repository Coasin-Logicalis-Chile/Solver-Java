
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IDomainDAO;
import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.services.IDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DomainServiceImpl implements IDomainService {

	@Autowired
	private IDomainDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Domain> findAll() {
		return (List<Domain>) dao.findAll();
	}

	@Override
	public Domain save(Domain domain) {
		return dao.save(domain);
	}

	@Override
	public Domain findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Domain> findByActive(boolean active) {
		return null;
	}


	@Override
	public Domain findByIntegrationId(String integrationId) { return dao.findByIntegrationId(integrationId); }
	@Override
	public Domain findTopByActive(boolean active) {	return (Domain) dao.findTopByActive(active);	}

}
