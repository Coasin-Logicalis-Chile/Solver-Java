
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnCiServiceDAO;
import com.logicalis.apisolver.model.servicenow.SnCiService;
import com.logicalis.apisolver.services.servicenow.ISnCiServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnCiServiceServiceImpl implements ISnCiServiceService {

	@Autowired
	private ISnCiServiceDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnCiService> findAll() {
		return (List<SnCiService>) dao.findAll();
	}

	@Override
	public SnCiService save(SnCiService snCiService) {
		return dao.save(snCiService);
	}

	@Override
	public SnCiService findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnCiService> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnCiService findTopByActive(boolean active) {	return (SnCiService) dao.findTopByActive(active);	}

}
