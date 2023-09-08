
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IScRequestDAO;
import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestFields;
import com.logicalis.apisolver.services.IScRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScRequestServiceImpl implements IScRequestService {

    @Autowired
    private IScRequestDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<ScRequest> findAll() {
        return (List<ScRequest>) dao.findAll();
    }

    @Override
    public ScRequest save(ScRequest scRequest) {
        return dao.save(scRequest);
    }

    @Override
    public ScRequest findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<ScRequest> findByActive(boolean active) {
        return null;
    }

    @Override
    public ScRequest findTopByActive(boolean active) {
        return (ScRequest) dao.findTopByActive(active);
    }

    @Override
    public ScRequest findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public Page<ScRequestFields> findPaginatedScRequestsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state) {
        return (Page<ScRequestFields>) dao.findPaginatedScRequestsByFilters(pageRequest, filter, assignedTo, company, state);
    }

    @Override
    public List<ScRequest> findByCompany(String integrationId) {
        return dao.findByCompany(integrationId);
    }

}
