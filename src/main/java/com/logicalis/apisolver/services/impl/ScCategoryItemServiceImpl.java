
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IScCategoryItemDAO;
import com.logicalis.apisolver.model.ScCategoryItem;
import com.logicalis.apisolver.services.IScCategoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScCategoryItemServiceImpl implements IScCategoryItemService {

	@Autowired
	private IScCategoryItemDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<ScCategoryItem> findAll() {
		return (List<ScCategoryItem>) dao.findAll();
	}

	@Override
	public ScCategoryItem save(ScCategoryItem scCategoryItem) {
		return dao.save(scCategoryItem);
	}

	@Override
	public ScCategoryItem findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<ScCategoryItem> findByActive(boolean active) {
		return null;
	}

	@Override
	public ScCategoryItem findTopByActive(boolean active) {	return (ScCategoryItem) dao.findTopByActive(active);	}

	@Override
	public ScCategoryItem findByIntegrationId(String integrationId) {
		return dao.findByIntegrationId(integrationId);
	}
}
