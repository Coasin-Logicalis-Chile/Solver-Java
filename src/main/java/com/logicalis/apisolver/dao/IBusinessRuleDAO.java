package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.BusinessRule;
import org.springframework.data.repository.CrudRepository;

public interface IBusinessRuleDAO extends CrudRepository<BusinessRule, Long> {
    public BusinessRule findTopByActive(boolean active);
}
