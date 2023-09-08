
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ICmnScheduleDAO;
import com.logicalis.apisolver.model.CmnSchedule;
import com.logicalis.apisolver.services.ICmnScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CmnScheduleServiceImpl implements ICmnScheduleService {

    @Autowired
    private ICmnScheduleDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<CmnSchedule> findAll() {
        return (List<CmnSchedule>) dao.findAll();
    }

    @Override
    public CmnSchedule save(CmnSchedule cmnSchedule) {
        return dao.save(cmnSchedule);
    }

    @Override
    public CmnSchedule findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<CmnSchedule> findByActive(boolean active) {
        return null;
    }

    @Override
    public CmnSchedule findTopByActive(boolean active) {
        return (CmnSchedule) dao.findTopByActive(active);
    }

    @Override
    public CmnSchedule findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }
}
