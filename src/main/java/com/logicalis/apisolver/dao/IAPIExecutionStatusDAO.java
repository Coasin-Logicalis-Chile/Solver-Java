package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.APIExecutionStatus;
import org.springframework.data.repository.CrudRepository;

public interface IAPIExecutionStatusDAO extends CrudRepository<APIExecutionStatus, Integer> {
 
}