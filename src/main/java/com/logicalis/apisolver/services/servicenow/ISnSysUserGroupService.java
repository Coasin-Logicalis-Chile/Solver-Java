
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSysUserGroup;

import java.util.List;

public interface ISnSysUserGroupService {
	
	public List<SnSysUserGroup> findAll();
	
	public SnSysUserGroup save(SnSysUserGroup snSysUserGroup);
	
	public SnSysUserGroup findById(Long id);
	
	public void delete(Long id);

	public List<SnSysUserGroup> findByActive(boolean active);
	public SnSysUserGroup findTopByActive(boolean active);
}
