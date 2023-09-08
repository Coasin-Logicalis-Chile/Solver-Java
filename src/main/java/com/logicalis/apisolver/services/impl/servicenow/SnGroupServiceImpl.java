
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnGroupDAO;
import com.logicalis.apisolver.model.servicenow.SnGroup;
import com.logicalis.apisolver.services.servicenow.ISnGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnGroupServiceImpl implements ISnGroupService {

	@Autowired
	private ISnGroupDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnGroup> findAll() {
		return (List<SnGroup>) dao.findAll();
	}

	@Override
	public SnGroup save(SnGroup snGroup) {
		return dao.save(snGroup);
	}

	@Override
	public SnGroup findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnGroup> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnGroup findTopByActive(boolean active) {	return (SnGroup) dao.findTopByActive(active);	}

}
