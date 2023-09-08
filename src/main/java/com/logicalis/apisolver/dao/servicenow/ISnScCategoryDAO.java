package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScCategory;
import org.springframework.data.repository.CrudRepository;

public interface ISnScCategoryDAO extends CrudRepository<SnScCategory, Long> {
    public SnScCategory findTopByActive(boolean active);
}
