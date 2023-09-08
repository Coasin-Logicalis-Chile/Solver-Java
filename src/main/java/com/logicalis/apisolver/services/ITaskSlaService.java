
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.TaskSla;
import com.logicalis.apisolver.model.TaskSlaInfo;

import java.util.List;

public interface ITaskSlaService {

    public List<TaskSla> findAll();

    public TaskSla save(TaskSla taskSla);

    public TaskSla findById(Long id);

    public void delete(Long id);

    public List<TaskSla> findByActive(boolean active);

    public TaskSla findTopByActive(boolean active);

    public TaskSla findByIntegrationId(String integrationId);

    public List<TaskSlaInfo> findByIncident(Long id);

    public List<TaskSlaInfo> findByScRequestItem(Long id);

    public List<TaskSlaInfo> findByScTask(Long id);
}
