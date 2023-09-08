
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IConfigurationItemDAO;
import com.logicalis.apisolver.model.ConfigurationItem;
import com.logicalis.apisolver.services.IConfigurationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigurationItemServiceImpl implements IConfigurationItemService {

	@Autowired
	private IConfigurationItemDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<ConfigurationItem> findAll() {
		return (List<ConfigurationItem>) dao.findAll();
	}

	@Override
	public ConfigurationItem save(ConfigurationItem configurationItem) {
		return dao.save(configurationItem);
	}

	@Override
	public ConfigurationItem findById(Long id) {
		return dao.findById(id).orElse(null);
	}
	@Override
	public ConfigurationItem findByIntegrationId(String integrationId) {
		return dao.findByIntegrationId(integrationId);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<ConfigurationItem> findByActive(boolean active) {
		return null;
	}

	@Override
	public ConfigurationItem findTopByActive(boolean active) {	return (ConfigurationItem) dao.findTopByActive(active);	}

}
