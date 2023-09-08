
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnScRequestItem;

import java.util.List;

public interface ISnScRequestItemService {
	
	public List<SnScRequestItem> findAll();
	
	public SnScRequestItem save(SnScRequestItem snScRequestItem);
	
	public SnScRequestItem findById(Long id);
	
	public void delete(Long id);

	public List<SnScRequestItem> findByActive(boolean active);
	public SnScRequestItem findTopByActive(boolean active);
}
