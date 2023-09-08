package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScCategoryItem;
import org.springframework.data.repository.CrudRepository;

public interface ISnScCategoryItemDAO extends CrudRepository<SnScCategoryItem, Long> {
    public SnScCategoryItem findTopByActive(boolean active);
}
