
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.BusinessRule;

import java.util.List;

public interface IBusinessRuleService {
	
	public List<BusinessRule> findAll();
	
	public BusinessRule save(BusinessRule businessRule);
	
	public BusinessRule findById(Long id);
	
	public void delete(Long id);

	public List<BusinessRule> findByActive(boolean active);
	public BusinessRule findTopByActive(boolean active);
}
