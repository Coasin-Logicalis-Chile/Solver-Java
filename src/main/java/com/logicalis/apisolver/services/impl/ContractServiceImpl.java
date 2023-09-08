
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IContractDAO;
import com.logicalis.apisolver.model.Contract;
import com.logicalis.apisolver.services.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractServiceImpl implements IContractService {

	@Autowired
	private IContractDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<Contract> findAll() {
		return (List<Contract>) dao.findAll();
	}

	@Override
	public Contract save(Contract contract) {
		return dao.save(contract);
	}

	@Override
	public Contract findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Contract> findByActive(boolean active) {
		return null;
	}

	@Override
	public Contract findTopByActive(boolean active) {	return (Contract) dao.findTopByActive(active);	}

	@Override
	public Contract findByIntegrationId(String integrationId) {
		    return   dao.findByIntegrationId(  integrationId);
	}

}
