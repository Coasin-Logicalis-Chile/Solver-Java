
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISysAuditDAO;
import com.logicalis.apisolver.model.SysAudit;
import com.logicalis.apisolver.services.ISysAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysAuditServiceImpl implements ISysAuditService {

    @Autowired
    private ISysAuditDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<SysAudit> findAll() {
        return (List<SysAudit>) dao.findAll();
    }

    @Override
    public SysAudit save(SysAudit sysAudit) {
        return dao.save(sysAudit);
    }

    @Override
    public SysAudit findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<SysAudit> findByActive(boolean active) {
        return null;
    }

    @Override
    public SysAudit findTopByActive(boolean active) {
        return (SysAudit) dao.findTopByActive(active);
    }

    @Override
    public SysAudit findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public List<SysAudit> findByInternalCheckpoint(String internalCheckpoint) {
        return dao.findByInternalCheckpoint(internalCheckpoint);
    }

}
