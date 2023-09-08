
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnScCategoryDAO;
import com.logicalis.apisolver.model.servicenow.SnScCategory;
import com.logicalis.apisolver.services.servicenow.ISnScCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnScCategoryServiceImpl implements ISnScCategoryService {

	@Autowired
	private ISnScCategoryDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnScCategory> findAll() {
		return (List<SnScCategory>) dao.findAll();
	}

	@Override
	public SnScCategory save(SnScCategory snScCategory) {
		return dao.save(snScCategory);
	}

	@Override
	public SnScCategory findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnScCategory> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnScCategory findTopByActive(boolean active) {	return (SnScCategory) dao.findTopByActive(active);	}

}
