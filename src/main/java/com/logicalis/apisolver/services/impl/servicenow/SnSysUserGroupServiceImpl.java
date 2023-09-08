
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnSysUserGroupDAO;
import com.logicalis.apisolver.model.servicenow.SnSysUserGroup;
import com.logicalis.apisolver.services.servicenow.ISnSysUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnSysUserGroupServiceImpl implements ISnSysUserGroupService {

	@Autowired
	private ISnSysUserGroupDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnSysUserGroup> findAll() {
		return (List<SnSysUserGroup>) dao.findAll();
	}

	@Override
	public SnSysUserGroup save(SnSysUserGroup snSysUserGroup) {
		return dao.save(snSysUserGroup);
	}

	@Override
	public SnSysUserGroup findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnSysUserGroup> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnSysUserGroup findTopByActive(boolean active) {	return (SnSysUserGroup) dao.findTopByActive(active);	}

}
