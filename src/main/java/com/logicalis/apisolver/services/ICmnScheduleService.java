
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.CmnSchedule;

import java.util.List;

public interface ICmnScheduleService {

    public List<CmnSchedule> findAll();

    public CmnSchedule save(CmnSchedule cmnSchedule);

    public CmnSchedule findById(Long id);

    public void delete(Long id);

    public List<CmnSchedule> findByActive(boolean active);

    public CmnSchedule findTopByActive(boolean active);

    public CmnSchedule findByIntegrationId(String integrationId);
}
