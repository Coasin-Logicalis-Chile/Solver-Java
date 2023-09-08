
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IGlideObjectDAO;
import com.logicalis.apisolver.model.GlideObject;
import com.logicalis.apisolver.services.IGlideObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GlideObjectServiceImpl implements IGlideObjectService {

	@Autowired
	private IGlideObjectDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<GlideObject> findAll() {
		return (List<GlideObject>) dao.findAll();
	}

	@Override
	public GlideObject save(GlideObject glideObject) {
		return dao.save(glideObject);
	}

	@Override
	public GlideObject findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<GlideObject> findByActive(boolean active) {
		return null;
	}

	@Override
	public GlideObject findTopByActive(boolean active) {	return (GlideObject) dao.findTopByActive(active);	}

}
