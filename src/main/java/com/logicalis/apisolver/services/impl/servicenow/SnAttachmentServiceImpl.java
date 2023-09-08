
package com.logicalis.apisolver.services.impl.servicenow;

import com.logicalis.apisolver.dao.servicenow.ISnAttachmentDAO;
import com.logicalis.apisolver.model.servicenow.SnAttachment;
import com.logicalis.apisolver.services.servicenow.ISnAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnAttachmentServiceImpl implements ISnAttachmentService {

	@Autowired
	private ISnAttachmentDAO dao;

	@Override
	@Transactional(readOnly = true)
	public List<SnAttachment> findAll() {
		return (List<SnAttachment>) dao.findAll();
	}

	@Override
	public SnAttachment save(SnAttachment snAttachment) {
		return dao.save(snAttachment);
	}

	@Override
	public SnAttachment findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<SnAttachment> findByActive(boolean active) {
		return null;
	}

	@Override
	public SnAttachment findTopByActive(boolean active) {	return (SnAttachment) dao.findTopByActive(active);	}

}
