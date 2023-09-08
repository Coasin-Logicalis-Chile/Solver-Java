package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.SysDocumentation;
import org.springframework.data.repository.CrudRepository;

public interface ISysDocumentationDAO extends CrudRepository<SysDocumentation, Long> {
    public SysDocumentation findTopByActive(boolean active);

    public SysDocumentation findByIntegrationId(String integrationId);

    public SysDocumentation findByUniqueIdentifier(String uniqueIdentifier);
}
