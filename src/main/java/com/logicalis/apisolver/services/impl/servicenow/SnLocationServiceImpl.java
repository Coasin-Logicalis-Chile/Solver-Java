
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnLocationDAO;
import com.logicalis.apisolver.model.servicenow.SnLocation;
import com.logicalis.apisolver.services.servicenow.ISnLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnLocationServiceImpl implements ISnLocationService {

	@Autowired
	private ISnLocationDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnLocation> findAll() {
		return (List<SnLocation>) dao.findAll();
	}

	@Override
	public SnLocation save(SnLocation snLocation) {
		return dao.save(snLocation);
	}

	@Override
	public SnLocation findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnLocation> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnLocation findTopByActive(boolean active) {	return (SnLocation) dao.findTopByActive(active);	}

}
