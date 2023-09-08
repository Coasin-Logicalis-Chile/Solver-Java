
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnContractDAO;
import com.logicalis.apisolver.model.servicenow.SnContract;
import com.logicalis.apisolver.services.servicenow.ISnContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnContractServiceImpl implements ISnContractService {

	@Autowired
	private ISnContractDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnContract> findAll() {
		return (List<SnContract>) dao.findAll();
	}

	@Override
	public SnContract save(SnContract snContract) {
		return dao.save(snContract);
	}

	@Override
	public SnContract findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnContract> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnContract findTopByActive(boolean active) {	return (SnContract) dao.findTopByActive(active);	}

}
