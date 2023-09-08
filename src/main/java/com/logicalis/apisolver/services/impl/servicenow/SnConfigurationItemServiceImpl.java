
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnConfigurationItemDAO;
import com.logicalis.apisolver.model.servicenow.SnConfigurationItem;
import com.logicalis.apisolver.services.servicenow.ISnConfigurationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnConfigurationItemServiceImpl implements ISnConfigurationItemService {

	@Autowired
	private ISnConfigurationItemDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnConfigurationItem> findAll() {
		return (List<SnConfigurationItem>) dao.findAll();
	}

	@Override
	public SnConfigurationItem save(SnConfigurationItem snConfigurationItem) {
		return dao.save(snConfigurationItem);
	}

	@Override
	public SnConfigurationItem findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnConfigurationItem> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnConfigurationItem findTopByActive(boolean active) {	return (SnConfigurationItem) dao.findTopByActive(active);	}

}
