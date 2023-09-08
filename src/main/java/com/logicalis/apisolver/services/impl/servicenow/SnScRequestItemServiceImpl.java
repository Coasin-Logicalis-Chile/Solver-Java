
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnScRequestItemDAO;
import com.logicalis.apisolver.model.servicenow.SnScRequestItem;
import com.logicalis.apisolver.services.servicenow.ISnScRequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnScRequestItemServiceImpl implements ISnScRequestItemService {

	@Autowired
	private ISnScRequestItemDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnScRequestItem> findAll() {
		return (List<SnScRequestItem>) dao.findAll();
	}

	@Override
	public SnScRequestItem save(SnScRequestItem snScRequestItem) {
		return dao.save(snScRequestItem);
	}

	@Override
	public SnScRequestItem findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnScRequestItem> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnScRequestItem findTopByActive(boolean active) {	return (SnScRequestItem) dao.findTopByActive(active);	}

}
