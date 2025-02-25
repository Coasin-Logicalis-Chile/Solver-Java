package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IScTaskDAO;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScTask;
import com.logicalis.apisolver.model.ScTaskFields;
import com.logicalis.apisolver.services.IScTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScTaskServiceImpl implements IScTaskService {
    @Autowired
    private IScTaskDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<ScTask> findAll() {
        return (List<ScTask>) dao.findAll();
    }

    @Override
    public ScTask save(ScTask scTask) {
        return dao.save(scTask);
    }

    @Override
    public ScTask findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<ScTask> findByActive(boolean active) {
        return null;
    }

    @Override
    public List<ScTask> findByScRequestItem(ScRequestItem scRequestItem) {

        return dao.findByScRequestItem(scRequestItem);
    }

    @Override
    public ScTask findTopByActive(boolean active) {
        return (ScTask) dao.findTopByActive(active);
    }

    @Override
    public ScTask findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public Page<ScTaskFields> findPaginatedScTasksByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo) {
        return dao.findPaginatedScTasksByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, solved, scaling, scalingAssignedTo, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
    }

    @Override
    public Long countScTasksByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open) {
        return dao.countScTasksByFilters(assignedTo, company, state, openUnassigned, solved, scaling, scalingAssignedTo, assignedToGroup, closed, open);
    }

    @Override
    public Long countTaskSLAByFilters(Long company, Long assignedTo) {
        return dao.countTaskSLAByFilters(company, assignedTo);
    }

    @Override
    public Page<ScTaskFields> findPaginatedScTasksSLAByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling,  Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo) {
        return dao.findPaginatedScTasksSLAByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, solved, scaling, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
    }

    @Override
    public Page<ScTaskFields> findPaginatedScTasksScalingByFilters(Pageable pageRequest, String filter, Long company, Long sysUser, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo) {
        return dao.findPaginatedScTasksScalingByFilters(pageRequest, filter, company, sysUser, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
    }
}
