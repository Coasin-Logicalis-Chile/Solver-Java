
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnScCategoryItemDAO;
import com.logicalis.apisolver.model.servicenow.SnScCategoryItem;
import com.logicalis.apisolver.services.servicenow.ISnScCategoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnScCategoryItemServiceImpl implements ISnScCategoryItemService {

	@Autowired
	private ISnScCategoryItemDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnScCategoryItem> findAll() {
		return (List<SnScCategoryItem>) dao.findAll();
	}

	@Override
	public SnScCategoryItem save(SnScCategoryItem snScCategoryItem) {
		return dao.save(snScCategoryItem);
	}

	@Override
	public SnScCategoryItem findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnScCategoryItem> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnScCategoryItem findTopByActive(boolean active) {	return (SnScCategoryItem) dao.findTopByActive(active);	}

}
