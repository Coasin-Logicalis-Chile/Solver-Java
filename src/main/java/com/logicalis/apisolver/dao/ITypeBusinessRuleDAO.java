package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.TypeBusinessRule;
import org.springframework.data.repository.CrudRepository;

public interface ITypeBusinessRuleDAO extends CrudRepository<TypeBusinessRule, Long> {
    public TypeBusinessRule findTopByActive(boolean active);
}
