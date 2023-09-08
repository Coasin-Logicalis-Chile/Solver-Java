
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.SysAudit;

import java.util.List;

public interface ISysAuditService {

    public List<SysAudit> findAll();

    public SysAudit save(SysAudit sysAudit);

    public SysAudit findById(Long id);

    public void delete(Long id);

    public List<SysAudit> findByActive(boolean active);

    public SysAudit findTopByActive(boolean active);

    public SysAudit findByIntegrationId(String integrationId);

    public List<SysAudit>  findByInternalCheckpoint(String internalCheckpoint);
}
