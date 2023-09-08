
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.TypeBusinessRule;

import java.util.List;

public interface ITypeBusinessRuleService {
	
	public List<TypeBusinessRule> findAll();
	
	public TypeBusinessRule save(TypeBusinessRule typeBusinessRule);
	
	public TypeBusinessRule findById(Long id);
	
	public void delete(Long id);

	public List<TypeBusinessRule> findByActive(boolean active);
	public TypeBusinessRule findTopByActive(boolean active);
}
