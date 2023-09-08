package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Contract;
import org.springframework.data.repository.CrudRepository;

public interface IContractDAO extends CrudRepository<Contract, Long> {
    public Contract findTopByActive(boolean active);

  public Contract findByIntegrationId(String integrationId);
}
