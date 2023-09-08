
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ContractSla;

import java.util.List;

public interface IContractSlaService {

    public List<ContractSla> findAll();

    public ContractSla save(ContractSla contractSla);

    public ContractSla findById(Long id);

    public void delete(Long id);

    public List<ContractSla> findByActive(boolean active);

    public ContractSla findTopByActive(boolean active);

    public ContractSla findByIntegrationId(String integrationId);
}
