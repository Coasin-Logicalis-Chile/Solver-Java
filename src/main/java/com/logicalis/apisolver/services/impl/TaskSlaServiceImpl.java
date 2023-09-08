
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ITaskSlaDAO;
import com.logicalis.apisolver.model.TaskSla;
import com.logicalis.apisolver.model.TaskSlaInfo;
import com.logicalis.apisolver.services.ITaskSlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskSlaServiceImpl implements ITaskSlaService {

    @Autowired
    private ITaskSlaDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<TaskSla> findAll() {
        return (List<TaskSla>) dao.findAll();
    }

    @Override
    public TaskSla save(TaskSla taskSla) {
        return dao.save(taskSla);
    }

    @Override
    public TaskSla findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<TaskSla> findByActive(boolean active) {
        return null;
    }

    @Override
    public TaskSla findTopByActive(boolean active) {
        return (TaskSla) dao.findTopByActive(active);
    }

    @Override
    public TaskSla findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public List<TaskSlaInfo> findByIncident(Long id) {
        return dao.findByIncident(id);
    }

    @Override
    public List<TaskSlaInfo> findByScRequestItem(Long id) {
        return dao.findByScRequestItem(id);
    }

    @Override
    public List<TaskSlaInfo> findByScTask(Long id) {
        return dao.findByScTask(id);
    }

}
