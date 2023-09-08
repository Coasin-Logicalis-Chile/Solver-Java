package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnContract;
import org.springframework.data.repository.CrudRepository;

public interface ISnContractDAO extends CrudRepository<SnContract, Long> {
    public SnContract findTopByActive(boolean active);
}
