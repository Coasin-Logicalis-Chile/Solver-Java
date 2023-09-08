
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnDepartmentDAO;
import com.logicalis.apisolver.model.servicenow.SnDepartment;
import com.logicalis.apisolver.services.servicenow.ISnDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnDepartmentServiceImpl implements ISnDepartmentService {

	@Autowired
	private ISnDepartmentDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnDepartment> findAll() {
		return (List<SnDepartment>) dao.findAll();
	}

	@Override
	public SnDepartment save(SnDepartment snDepartment) {
		return dao.save(snDepartment);
	}

	@Override
	public SnDepartment findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnDepartment> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnDepartment findTopByActive(boolean active) {	return (SnDepartment) dao.findTopByActive(active);	}

}
