
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Location;

import java.util.List;

public interface ILocationService {

    public List<Location> findAll();

    public Location save(Location location);

    public Location findById(Long id);

    public void delete(Long id);

    public List<Location> findByActive(boolean active);

    public Location findTopByActive(boolean active);

    public Location findByIntegrationId(String integrationId);
}
