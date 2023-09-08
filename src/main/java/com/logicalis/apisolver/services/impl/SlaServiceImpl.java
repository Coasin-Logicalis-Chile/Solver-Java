
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISlaDAO;
import com.logicalis.apisolver.model.Sla;
import com.logicalis.apisolver.services.ISlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlaServiceImpl implements ISlaService {

	@Autowired
	private ISlaDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Sla> findAll() {
		return (List<Sla>) dao.findAll();
	}

	@Override
	public Sla save(Sla sla) {
		return dao.save(sla);
	}

	@Override
	public Sla findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Sla> findByActive(boolean active) {
		return null;
	}

	@Override
	public Sla findTopByActive(boolean active) {	return (Sla) dao.findTopByActive(active);	}

}
