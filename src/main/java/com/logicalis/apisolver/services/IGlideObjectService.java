
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.GlideObject;

import java.util.List;

public interface IGlideObjectService {
	
	public List<GlideObject> findAll();
	
	public GlideObject save(GlideObject glideObject);
	
	public GlideObject findById(Long id);
	
	public void delete(Long id);

	public List<GlideObject> findByActive(boolean active);
	public GlideObject findTopByActive(boolean active);
}
