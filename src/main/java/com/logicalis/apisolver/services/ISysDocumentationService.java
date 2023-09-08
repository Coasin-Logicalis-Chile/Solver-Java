
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.SysDocumentation;

import java.util.List;

public interface ISysDocumentationService {

    public List<SysDocumentation> findAll();

    public SysDocumentation save(SysDocumentation sysDocumentation);

    public SysDocumentation findById(Long id);

    public void delete(Long id);

    public List<SysDocumentation> findByActive(boolean active);

    public SysDocumentation findTopByActive(boolean active);

    public SysDocumentation findByIntegrationId(String integrationId);

    public SysDocumentation findByUniqueIdentifier(String uniqueIdentifier);
}
