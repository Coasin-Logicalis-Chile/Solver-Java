
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IContractSlaDAO;
import com.logicalis.apisolver.model.ContractSla;
import com.logicalis.apisolver.services.IContractSlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractSlaServiceImpl implements IContractSlaService {

    @Autowired
    private IContractSlaDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<ContractSla> findAll() {
        return (List<ContractSla>) dao.findAll();
    }

    @Override
    public ContractSla save(ContractSla contractSla) {
        return dao.save(contractSla);
    }

    @Override
    public ContractSla findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<ContractSla> findByActive(boolean active) {
        return null;
    }

    @Override
    public ContractSla findTopByActive(boolean active) {
        return (ContractSla) dao.findTopByActive(active);
    }

    @Override
    public ContractSla findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

}
