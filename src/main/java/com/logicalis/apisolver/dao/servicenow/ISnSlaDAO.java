package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSla;
import org.springframework.data.repository.CrudRepository;

public interface ISnSlaDAO extends CrudRepository<SnSla, Long> {
    public SnSla findTopByActive(boolean active);
}
