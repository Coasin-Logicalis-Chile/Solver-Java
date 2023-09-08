
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnSysUserDAO;
import com.logicalis.apisolver.model.servicenow.SnSysUser;
import com.logicalis.apisolver.services.servicenow.ISnSysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnSysUserServiceImpl implements ISnSysUserService {

	@Autowired
	private ISnSysUserDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnSysUser> findAll() {
		return (List<SnSysUser>) dao.findAll();
	}

	@Override
	public SnSysUser save(SnSysUser snSysUser) {
		return dao.save(snSysUser);
	}

	@Override
	public SnSysUser findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnSysUser> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnSysUser findTopByActive(boolean active) {	return (SnSysUser) dao.findTopByActive(active);	}

}
