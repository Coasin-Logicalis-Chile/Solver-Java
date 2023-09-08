package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnChoice;
import org.springframework.data.repository.CrudRepository;

public interface ISnChoiceDAO extends CrudRepository<SnChoice, Long> {
    public SnChoice findTopByInactive(boolean inactive);
}
