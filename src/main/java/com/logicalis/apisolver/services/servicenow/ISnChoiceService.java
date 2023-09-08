
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnChoice;

import java.util.List;

public interface ISnChoiceService {
	
	public List<SnChoice> findAll();
	
	public SnChoice save(SnChoice snChoice);
	
	public SnChoice findById(Long id);
	
	public void delete(Long id);

	public List<SnChoice> findByInactive(boolean inactive);
	public SnChoice findTopByInactive(boolean inactive);
}
