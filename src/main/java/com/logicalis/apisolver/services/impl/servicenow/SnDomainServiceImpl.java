
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnDomainDAO;
import com.logicalis.apisolver.model.servicenow.SnDomain;
import com.logicalis.apisolver.services.servicenow.ISnDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnDomainServiceImpl implements ISnDomainService {

	@Autowired
	private ISnDomainDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnDomain> findAll() {
		return (List<SnDomain>) dao.findAll();
	}

	@Override
	public SnDomain save(SnDomain snDomain) {
		return dao.save(snDomain);
	}

	@Override
	public SnDomain findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnDomain> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnDomain findTopByActive(boolean active) {	return (SnDomain) dao.findTopByActive(active);	}

}
