
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Contract;

import java.util.List;

public interface IContractService {

    public List<Contract> findAll();

    public Contract save(Contract contract);

    public Contract findById(Long id);

    public void delete(Long id);

    public List<Contract> findByActive(boolean active);

    public Contract findTopByActive(boolean active);

    public Contract findByIntegrationId(String integrationId);
}
