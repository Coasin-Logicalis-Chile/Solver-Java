package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.CmnSchedule;
import org.springframework.data.repository.CrudRepository;

public interface ICmnScheduleDAO extends CrudRepository<CmnSchedule, Long> {
    public CmnSchedule findTopByActive(boolean active);

    public CmnSchedule findByIntegrationId(String integrationId);
}
