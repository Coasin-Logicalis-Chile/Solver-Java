
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.IncidentFields;
import com.logicalis.apisolver.model.IncidentSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IIncidentService {

    public long count();

    public List<Incident> findAll();

    public Page<Incident> findAll(Pageable pageRequest);

    public Incident save(Incident incident);

    public Incident findById(Long id);

    public void delete(Long id);

    public List<Incident> findByActive(boolean active);

    public Page<IncidentFields> findPaginatedIncidentsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    public Page<IncidentFields> findPaginatedIncidentsSlaByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    public List<IncidentFields> findIncidentsByFilters(String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String incidentParent, String scRequestParent, String scRequestItemParent, Long assignedToGroup, boolean closed, boolean open);

    public Long countIncidentsByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean closed, Long assignedToGroup, boolean open);

    /*public Long countIncidentParentByFilters(String incidentParent);*/

    public Long countIncidentsSLAByFilters(Long company, Long assignedTo);

    public Incident findTopByActive(boolean active);

    public Incident findByIntegrationId(String integrationId);

    public List<Incident> findByCompany(String integrationId);

    public List<IncidentSummary> findBySummary(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, String currentDate, String lastDate, Long closedBy);

}
