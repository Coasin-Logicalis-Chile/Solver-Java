package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnLocation;
import org.springframework.data.repository.CrudRepository;

public interface ISnLocationDAO extends CrudRepository<SnLocation, Long> {
    public SnLocation findTopByActive(boolean active);
}
