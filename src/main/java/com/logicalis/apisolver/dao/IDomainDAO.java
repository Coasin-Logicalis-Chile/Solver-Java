package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Domain;
import org.springframework.data.repository.CrudRepository;

public interface IDomainDAO extends CrudRepository<Domain, Long> {
    public Domain findTopByActive(boolean active);
    //public Domain findByName(String name);
    public Domain findByIntegrationId(String integrationId);
}
