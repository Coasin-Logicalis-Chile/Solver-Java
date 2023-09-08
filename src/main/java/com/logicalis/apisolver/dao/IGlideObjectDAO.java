package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.GlideObject;
import org.springframework.data.repository.CrudRepository;

public interface IGlideObjectDAO extends CrudRepository<GlideObject, Long> {
    public GlideObject findTopByActive(boolean active);
}
