
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnSysUser;

import java.util.List;

public interface ISnSysUserService {
	
	public List<SnSysUser> findAll();
	
	public SnSysUser save(SnSysUser snSysUser);
	
	public SnSysUser findById(Long id);
	
	public void delete(Long id);

	public List<SnSysUser> findByActive(boolean active);
	public SnSysUser findTopByActive(boolean active);
}
