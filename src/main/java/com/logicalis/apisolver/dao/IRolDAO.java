package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Rol;
import org.springframework.data.repository.CrudRepository;

public interface IRolDAO extends CrudRepository<Rol, Long> {
    public Rol findTopByActive(boolean active);
}
