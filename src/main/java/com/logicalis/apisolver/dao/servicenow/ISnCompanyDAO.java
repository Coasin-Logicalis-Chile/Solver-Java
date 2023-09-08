package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnCompany;
import org.springframework.data.repository.CrudRepository;

public interface ISnCompanyDAO extends CrudRepository<SnCompany, Long> {
    public SnCompany findTopByActive(boolean active);
}
