
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IBusinessRuleDAO;
import com.logicalis.apisolver.model.BusinessRule;
import com.logicalis.apisolver.services.IBusinessRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessRuleServiceImpl implements IBusinessRuleService {

	@Autowired
	private IBusinessRuleDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<BusinessRule> findAll() {
		return (List<BusinessRule>) dao.findAll();
	}

	@Override
	public BusinessRule save(BusinessRule businessRule) {
		return dao.save(businessRule);
	}

	@Override
	public BusinessRule findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<BusinessRule> findByActive(boolean active) {
		return null;
	}

	@Override
	public BusinessRule findTopByActive(boolean active) {	return (BusinessRule) dao.findTopByActive(active);	}

}
