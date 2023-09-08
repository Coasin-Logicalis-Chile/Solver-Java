
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISysUserGroupDAO;
import com.logicalis.apisolver.model.SysUserGroup;
import com.logicalis.apisolver.model.SysUserGroupFields;
import com.logicalis.apisolver.services.ISysUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserGroupServiceImpl implements ISysUserGroupService {

	@Autowired
	private ISysUserGroupDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SysUserGroup> findAll() {
		return (List<SysUserGroup>) dao.findAll();
	}

	@Override
	public SysUserGroup save(SysUserGroup sysUserGroup) {
		return dao.save(sysUserGroup);
	}

	@Override
	public SysUserGroup findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SysUserGroup> findByActive(boolean active) {
		return null;
	}

	@Override
	public SysUserGroup findTopByActive(boolean active) {	return (SysUserGroup) dao.findTopByActive(active);	}

	@Override
	@Transactional(readOnly = true)
	public SysUserGroup findByIntegrationId(String integrationId) {
		return dao.findByIntegrationId(integrationId);
	}

	@Override
	public List<SysUserGroupFields> findUserForGroupByFilters(Long company ) {
		return  dao.findUserForGroupByFilters(company);
	}

}
