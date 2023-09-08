
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScCategory;

import java.util.List;

public interface ISnScCategoryService {
	
	public List<SnScCategory> findAll();
	
	public SnScCategory save(SnScCategory snScCategory);
	
	public SnScCategory findById(Long id);
	
	public void delete(Long id);

	public List<SnScCategory> findByActive(boolean active);
	public SnScCategory findTopByActive(boolean active);
}
