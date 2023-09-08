
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISysDocumentationDAO;
import com.logicalis.apisolver.model.SysDocumentation;
import com.logicalis.apisolver.services.ISysDocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDocumentationServiceImpl implements ISysDocumentationService {

    @Autowired
    private ISysDocumentationDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<SysDocumentation> findAll() {
        return (List<SysDocumentation>) dao.findAll();
    }

    @Override
    public SysDocumentation save(SysDocumentation sysDocumentation) {
        return dao.save(sysDocumentation);
    }

    @Override
    public SysDocumentation findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<SysDocumentation> findByActive(boolean active) {
        return null;
    }

    @Override
    public SysDocumentation findTopByActive(boolean active) {
        return (SysDocumentation) dao.findTopByActive(active);
    }

    @Override
    public SysDocumentation findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public SysDocumentation findByUniqueIdentifier(String uniqueIdentifier) {
        return dao.findByUniqueIdentifier(uniqueIdentifier);
    }


}
