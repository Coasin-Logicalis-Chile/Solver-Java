package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.SysAudit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISysAuditDAO extends CrudRepository<SysAudit, Long> {
    public SysAudit findTopByActive(boolean active);

    public SysAudit findByIntegrationId(String integrationId);

    public List<SysAudit> findByInternalCheckpoint(String internalCheckpoint);
}
