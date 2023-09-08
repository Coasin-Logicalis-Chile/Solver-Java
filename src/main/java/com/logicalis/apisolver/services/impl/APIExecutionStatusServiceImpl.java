
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IAPIExecutionStatusDAO;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class APIExecutionStatusServiceImpl implements IAPIExecutionStatusService {

	@Autowired
	private IAPIExecutionStatusDAO aPIExecutionStatusDAO;
	
	@Override
	@Transactional(readOnly = true)
	public List<APIExecutionStatus> findAll() {
		return (List<APIExecutionStatus>) aPIExecutionStatusDAO.findAll();
	}

	@Override
	public APIExecutionStatus save(APIExecutionStatus aPIExecutionStatus) {
		return aPIExecutionStatusDAO.save(aPIExecutionStatus);
	}

}
