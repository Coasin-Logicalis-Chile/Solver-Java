
package com.logicalis.apisolver.services.servicenow;

import com.logicalis.apisolver.model.servicenow.SnAttachment;

import java.util.List;

public interface ISnAttachmentService {
	
	public List<SnAttachment> findAll();
	
	public SnAttachment save(SnAttachment snAttachment);
	
	public SnAttachment findById(Long id);
	
	public void delete(Long id);

	public List<SnAttachment> findByActive(boolean active);
	public SnAttachment findTopByActive(boolean active);
}
