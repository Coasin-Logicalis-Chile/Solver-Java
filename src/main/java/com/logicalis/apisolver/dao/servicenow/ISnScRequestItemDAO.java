package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScRequestItem;
import org.springframework.data.repository.CrudRepository;

public interface ISnScRequestItemDAO extends CrudRepository<SnScRequestItem, Long> {
    public SnScRequestItem findTopByActive(boolean active);
}
