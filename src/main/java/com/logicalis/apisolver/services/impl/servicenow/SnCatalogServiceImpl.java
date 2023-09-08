
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnCatalogDAO;
import com.logicalis.apisolver.model.servicenow.SnCatalog;
import com.logicalis.apisolver.services.servicenow.ISnCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnCatalogServiceImpl implements ISnCatalogService {

	@Autowired
	private ISnCatalogDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnCatalog> findAll() {
		return (List<SnCatalog>) dao.findAll();
	}

	@Override
	public SnCatalog save(SnCatalog snCatalog) {
		return dao.save(snCatalog);
	}

	@Override
	public SnCatalog findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnCatalog> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnCatalog findTopByActive(boolean active) {	return (SnCatalog) dao.findTopByActive(active);	}

}
