
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScTask;
import com.logicalis.apisolver.model.ScTaskFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IScTaskService {

    public List<ScTask> findAll();

    public List<ScTask> findByScRequestItem(ScRequestItem scRequestItem);

    public ScTask save(ScTask scTask);

    public ScTask findById(Long id);

    public void delete(Long id);

    public List<ScTask> findByActive(boolean active);

    public ScTask findTopByActive(boolean active);

    public ScTask findByIntegrationId(String integrationId);

    public Page<ScTaskFields> findPaginatedScTasksByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    public Long countScTasksByFilters(Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long scalingAssignedTo, Long assignedToGroup, boolean closed, boolean open);

    public Long countTaskSLAByFilters(Long company, Long assignedTo);

    public Page<ScTaskFields> findPaginatedScTasksSLAByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved, boolean scaling, Long assignedToGroup, boolean closed, boolean open, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);

    public Page<ScTaskFields> findPaginatedScTasksScalingByFilters(Pageable pageRequest, String filter, Long company, Long sysUser, List<Long> sysGroups, List<Long> sysUsers, List<String> states, List<String> priorities, String createdOnFrom, String createdOnTo);
}
