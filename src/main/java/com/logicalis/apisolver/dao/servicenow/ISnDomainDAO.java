package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnDomain;
import org.springframework.data.repository.CrudRepository;

public interface ISnDomainDAO extends CrudRepository<SnDomain, Long> {
    public SnDomain findTopByActive(boolean active);
}
