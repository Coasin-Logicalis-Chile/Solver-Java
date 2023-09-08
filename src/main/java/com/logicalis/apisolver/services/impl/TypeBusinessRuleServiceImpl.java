
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ITypeBusinessRuleDAO;
import com.logicalis.apisolver.model.TypeBusinessRule;
import com.logicalis.apisolver.services.ITypeBusinessRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeBusinessRuleServiceImpl implements ITypeBusinessRuleService {

	@Autowired
	private ITypeBusinessRuleDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<TypeBusinessRule> findAll() {
		return (List<TypeBusinessRule>) dao.findAll();
	}

	@Override
	public TypeBusinessRule save(TypeBusinessRule typeBusinessRule) {
		return dao.save(typeBusinessRule);
	}

	@Override
	public TypeBusinessRule findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<TypeBusinessRule> findByActive(boolean active) {
		return null;
	}

	@Override
	public TypeBusinessRule findTopByActive(boolean active) {	return (TypeBusinessRule) dao.findTopByActive(active);	}

}
