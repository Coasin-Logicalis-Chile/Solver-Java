
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnDepartment;

import java.util.List;

public interface ISnDepartmentService {
	
	public List<SnDepartment> findAll();
	
	public SnDepartment save(SnDepartment snDepartment);
	
	public SnDepartment findById(Long id);
	
	public void delete(Long id);

	public List<SnDepartment> findByActive(boolean active);
	public SnDepartment findTopByActive(boolean active);
}
