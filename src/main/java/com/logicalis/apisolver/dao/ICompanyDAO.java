package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Company;
import org.springframework.data.repository.CrudRepository;

public interface ICompanyDAO extends CrudRepository<Company, Long> {
    public Company findTopByActive(boolean active);
    public Company findByIntegrationId(String integrationId);
}
