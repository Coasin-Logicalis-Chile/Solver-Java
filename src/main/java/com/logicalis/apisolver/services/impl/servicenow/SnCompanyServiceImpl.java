
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnCompanyDAO;
import com.logicalis.apisolver.model.servicenow.SnCompany;
import com.logicalis.apisolver.services.servicenow.ISnCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnCompanyServiceImpl implements ISnCompanyService {

	@Autowired
	private ISnCompanyDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnCompany> findAll() {
		return (List<SnCompany>) dao.findAll();
	}

	@Override
	public SnCompany save(SnCompany snCompany) {
		return dao.save(snCompany);
	}

	@Override
	public SnCompany findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnCompany> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnCompany findTopByActive(boolean active) {	return (SnCompany) dao.findTopByActive(active);	}

}
