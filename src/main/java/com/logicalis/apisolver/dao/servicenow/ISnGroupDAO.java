package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnGroup;
import org.springframework.data.repository.CrudRepository;

public interface ISnGroupDAO extends CrudRepository<SnGroup, Long> {
    public SnGroup findTopByActive(boolean active);
}
