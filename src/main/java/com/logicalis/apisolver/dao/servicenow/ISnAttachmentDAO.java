package com.logicalis.apisolver.dao.servicenow;

import com.logicalis.apisolver.model.servicenow.SnAttachment;
import org.springframework.data.repository.CrudRepository;

public interface ISnAttachmentDAO extends CrudRepository<SnAttachment, Long> {
    public SnAttachment findTopByActive(boolean active);
}
