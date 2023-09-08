
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnCatalogLineDAO;
import com.logicalis.apisolver.model.servicenow.SnCatalogLine;
import com.logicalis.apisolver.services.servicenow.ISnCatalogLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnCatalogLineServiceImpl implements ISnCatalogLineService {

	@Autowired
	private ISnCatalogLineDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnCatalogLine> findAll() {
		return (List<SnCatalogLine>) dao.findAll();
	}

	@Override
	public SnCatalogLine save(SnCatalogLine snCatalogLine) {
		return dao.save(snCatalogLine);
	}

	@Override
	public SnCatalogLine findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnCatalogLine> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnCatalogLine findTopByActive(boolean active) {	return (SnCatalogLine) dao.findTopByActive(active);	}

}
