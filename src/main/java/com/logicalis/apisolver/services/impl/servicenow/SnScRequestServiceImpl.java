
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnScRequestDAO;
import com.logicalis.apisolver.model.servicenow.SnScRequest;
import com.logicalis.apisolver.services.servicenow.ISnScRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnScRequestServiceImpl implements ISnScRequestService {

	@Autowired
	private ISnScRequestDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnScRequest> findAll() {
		return (List<SnScRequest>) dao.findAll();
	}

	@Override
	public SnScRequest save(SnScRequest snScRequest) {
		return dao.save(snScRequest);
	}

	@Override
	public SnScRequest findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnScRequest> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnScRequest findTopByActive(boolean active) {	return (SnScRequest) dao.findTopByActive(active);	}

}
