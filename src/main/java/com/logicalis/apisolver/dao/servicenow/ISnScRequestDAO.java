package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScRequest;
import org.springframework.data.repository.CrudRepository;

public interface ISnScRequestDAO extends CrudRepository<SnScRequest, Long> {
    public SnScRequest findTopByActive(boolean active);
}
