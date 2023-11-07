package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IIncidentDAO;
import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.IncidentFields;
import com.logicalis.apisolver.model.IncidentSummary;
import com.logicalis.apisolver.services.IIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentServiceImpl implements IIncidentService {
    @Autowired
    private IIncidentDAO dao;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incident> findAll() {
        return (List<Incident>) dao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Incident> findAll(Pageable pageRequest) {
        return (Page<Incident>) dao.findAll(pageRequest);
    }

    @Override
    public Incident save(Incident incident) {
        return dao.save(incident);
    }

    @Override
    public Incident findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<Incident> findByActive(boolean active) {
        return null;
    }

    @Override
    public Page<IncidentFields> findPaginatedIncidentsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo) {
        return (Page<IncidentFields>) dao.findPaginatedIncidentsByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, solved, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
    }

    @Override
    public Page<IncidentFields> findPaginatedIncidentsSlaByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo) {
        return (Page<IncidentFields>) dao.findPaginatedIncidentsSlaByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
    }

    @Override
    public List<IncidentFields> findIncidentsByFilters(String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open) {
        return (List<IncidentFields>) dao.findIncidentsByFilters(filter, assignedTo, company, state, openUnassigned, solved, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, closed, open);
    }

    @Override
    public Long countIncidentsByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean closed, Long assignedToGroup, boolean open) {
        return (Long) dao.countIncidentsByFilters(assignedTo, company, state, openUnassigned, solved, closed, assignedToGroup, open);
    }

    @Override
    public Long countIncidentsSLAByFilters(Long company, Long assignedTo) {
        return (Long) dao.countIncidentsSLAByFilters(company, assignedTo);
    }

    @Override
    public Incident findTopByActive(boolean active) {
        return (Incident) dao.findTopByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public Incident findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public List<Incident> findByCompany(String integrationId) {
        return dao.findByCompany(integrationId);
    }

    @Override
    public List<IncidentSummary> findBySummary(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String currentDate, String lastDate, Long closedBy) {
        return dao.findBySummary(assignedTo, company, state, openUnassigned, solved, currentDate, lastDate, closedBy);
    }
}
