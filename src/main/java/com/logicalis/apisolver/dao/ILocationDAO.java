package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Location;
import org.springframework.data.repository.CrudRepository;

public interface ILocationDAO extends CrudRepository<Location, Long> {
    public Location findTopByActive(boolean active);
    public Location findByIntegrationId(String integrationId);
}
