
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScCategoryItem;

import java.util.List;

public interface ISnScCategoryItemService {
	
	public List<SnScCategoryItem> findAll();
	
	public SnScCategoryItem save(SnScCategoryItem snScCategoryItem);
	
	public SnScCategoryItem findById(Long id);
	
	public void delete(Long id);

	public List<SnScCategoryItem> findByActive(boolean active);
	public SnScCategoryItem findTopByActive(boolean active);
}
