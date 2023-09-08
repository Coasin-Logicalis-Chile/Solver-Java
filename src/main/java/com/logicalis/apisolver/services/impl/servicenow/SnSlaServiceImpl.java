
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnSlaDAO;
import com.logicalis.apisolver.model.servicenow.SnSla;
import com.logicalis.apisolver.services.servicenow.ISnSlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnSlaServiceImpl implements ISnSlaService {

	@Autowired
	private ISnSlaDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnSla> findAll() {
		return (List<SnSla>) dao.findAll();
	}

	@Override
	public SnSla save(SnSla snSla) {
		return dao.save(snSla);
	}

	@Override
	public SnSla findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnSla> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnSla findTopByActive(boolean active) {	return (SnSla) dao.findTopByActive(active);	}

}
