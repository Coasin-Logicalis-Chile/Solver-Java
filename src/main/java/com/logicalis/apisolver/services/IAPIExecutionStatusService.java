
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.APIExecutionStatus;

import java.util.List;

public interface IAPIExecutionStatusService {
	
	public List<APIExecutionStatus> findAll();
	
	public APIExecutionStatus save(APIExecutionStatus aPIExecutionStatus);

}
