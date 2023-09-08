
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Rol;

import java.util.List;

public interface IRolService {
	
	public List<Rol> findAll();
	
	public Rol save(Rol rol);
	
	public Rol findById(Long id);
	
	public void delete(Long id);

	public List<Rol> findByActive(boolean active);
	public Rol findTopByActive(boolean active);
}
