
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IScRequestItemDAO;
import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScRequestItemInfo;
import com.logicalis.apisolver.services.IScRequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScRequestItemServiceImpl implements IScRequestItemService {

    @Autowired
    private IScRequestItemDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<ScRequestItem> findAll() {
        return (List<ScRequestItem>) dao.findAll();
    }

    @Override
    public ScRequestItem save(ScRequestItem scRequestItem) {
        return dao.save(scRequestItem);
    }

    @Override
    public ScRequestItem findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public   List<ScRequestItem> findByScRequest(ScRequest scRequest) {
        return dao.findByScRequest(scRequest);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<ScRequestItem> findByActive(boolean active) {
        return null;
    }

    @Override
    public ScRequestItem findTopByActive(boolean active) {
        return (ScRequestItem) dao.findTopByActive(active);
    }

    @Override
    public Page<ScRequestItemInfo> findPaginatedScRequestItemsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved) {
        return dao.findPaginatedScRequestItemsByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, solved);
    }

    @Override
    public Long countScRequestItemsByFilters(Long assignedTo, Long company, String state) {
        return dao.countScRequestItemsByFilters(assignedTo, company, state);
    }

    @Override
    public ScRequestItem findByIntegrationId(String integrationId) {
        return (ScRequestItem) dao.findByIntegrationId(integrationId);
    }

    @Override
    public List<ScRequestItem> findByCompany(String integrationId) {
        return dao.findByCompany(integrationId);
    }
}
