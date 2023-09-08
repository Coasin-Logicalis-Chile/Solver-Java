package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.ContractSla;
import org.springframework.data.repository.CrudRepository;

public interface IContractSlaDAO extends CrudRepository<ContractSla, Long> {
    public ContractSla findTopByActive(boolean active);

    public ContractSla findByIntegrationId(String integrationId);
}
