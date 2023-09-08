
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ILocationDAO;
import com.logicalis.apisolver.model.Location;
import com.logicalis.apisolver.services.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationServiceImpl implements ILocationService {

	@Autowired
	private ILocationDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Location> findAll() {
		return (List<Location>) dao.findAll();
	}

	@Override
	public Location save(Location location) {
		return dao.save(location);
	}

	@Override
	public Location findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Location> findByActive(boolean active) {
		return null;
	}

	@Override
	public Location findTopByActive(boolean active) {	return (Location) dao.findTopByActive(active);	}

	@Override
	public Location findByIntegrationId(String integrationId){	return dao.findByIntegrationId(integrationId);	}
}
