package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.CiService;
import org.springframework.data.repository.CrudRepository;

public interface ICiServiceDAO extends CrudRepository<CiService, Long> {
    public CiService findTopByActive(boolean active);
    public CiService findByIntegrationId(String integrationId);
}
