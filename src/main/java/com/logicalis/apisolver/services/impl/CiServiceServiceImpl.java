
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ICiServiceDAO;
import com.logicalis.apisolver.model.CiService;
import com.logicalis.apisolver.services.ICiServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CiServiceServiceImpl implements ICiServiceService {

	@Autowired
	private ICiServiceDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<CiService> findAll() {
		return (List<CiService>) dao.findAll();
	}

	@Override
	public CiService save(CiService ciService) {
		return dao.save(ciService);
	}

	@Override
	public CiService findById(Long id) {
		return dao.findById(id).orElse(null);
	}
	@Override
	public CiService findByIntegrationId(String integrationId){
		return dao.findByIntegrationId(integrationId);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<CiService> findByActive(boolean active) {
		return null;
	}

	@Override
	public CiService findTopByActive(boolean active) {	return (CiService) dao.findTopByActive(active);	}

}
