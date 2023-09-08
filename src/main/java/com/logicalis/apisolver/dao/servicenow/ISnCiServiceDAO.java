package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCiService;
import org.springframework.data.repository.CrudRepository;

public interface ISnCiServiceDAO extends CrudRepository<SnCiService, Long> {
    public SnCiService findTopByActive(boolean active);
}
