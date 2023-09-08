
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ICatalogDAO;
import com.logicalis.apisolver.model.Catalog;
import com.logicalis.apisolver.services.ICatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogServiceImpl implements ICatalogService {

	@Autowired
	private ICatalogDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Catalog> findAll() {
		return (List<Catalog>) dao.findAll();
	}

	@Override
	public Catalog save(Catalog catalog) {
		return dao.save(catalog);
	}

	@Override
	public Catalog findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Catalog> findByActive(boolean active) {
		return null;
	}

	@Override
	public Catalog findByIntegrationId(String integrationId) {
		return dao.findByIntegrationId(integrationId);
	}
	@Override
	public Catalog findTopByActive(boolean active) {	return (Catalog) dao.findTopByActive(active);	}



}
