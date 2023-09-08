package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Sla;
import org.springframework.data.repository.CrudRepository;

public interface ISlaDAO extends CrudRepository<Sla, Long> {
    public Sla findTopByActive(boolean active);
   // public Sla findByIntegrationId(String integrationId);
}
