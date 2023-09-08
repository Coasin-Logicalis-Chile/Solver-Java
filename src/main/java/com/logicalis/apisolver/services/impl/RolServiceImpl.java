
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IRolDAO;
import com.logicalis.apisolver.model.Rol;
import com.logicalis.apisolver.services.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolServiceImpl implements IRolService {

	@Autowired
	private IRolDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Rol> findAll() {
		return (List<Rol>) dao.findAll();
	}

	@Override
	public Rol save(Rol rol) {
		return dao.save(rol);
	}

	@Override
	public Rol findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Rol> findByActive(boolean active) {
		return null;
	}

	@Override
	public Rol findTopByActive(boolean active) {	return (Rol) dao.findTopByActive(active);	}

}
