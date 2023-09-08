
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnChoiceDAO;
import com.logicalis.apisolver.model.servicenow.SnChoice;
import com.logicalis.apisolver.services.servicenow.ISnChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnChoiceServiceImpl implements ISnChoiceService {

	@Autowired
	private ISnChoiceDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnChoice> findAll() {
		return (List<SnChoice>) dao.findAll();
	}

	@Override
	public SnChoice save(SnChoice snChoice) {
		return dao.save(snChoice);
	}

	@Override
	public SnChoice findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnChoice> findByInactive(boolean inactive) {
		return null;
	}

	@Override
	public SnChoice findTopByInactive(boolean inactive) {	return (SnChoice) dao.findTopByInactive(inactive);	}

}
