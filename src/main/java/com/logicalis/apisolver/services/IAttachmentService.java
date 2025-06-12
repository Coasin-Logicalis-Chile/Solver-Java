
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Attachment;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IAttachmentService {

    public List<Attachment> findAll();

    public Attachment save(Attachment attachment);

    public Attachment findById(Long id);

    public void delete(Long id);


    public void deleteByIdDirect(Long id);

    public List<Attachment> findByActive(boolean active);

    public Attachment findTopByActive(boolean active);

    public Attachment findByIntegrationId(String integrationId);

    public List<AttachmentInfo> findAttachmentsByIncident(Long id);

    public List<AttachmentInfo> findAttachmentsByIncidentOrScRequestItem(String incident, String scRequestItem);

    public List<AttachmentInfo> findAttachmentsByScRequestItem(Long id);

    public List<AttachmentInfo> findAttachmentsByScTask(Long id);

    public List<Attachment> findByElement(String integrationId);

    public List<Attachment> findSolverByElement(String integrationId);

    //public Attachment uploadFile(MultipartFile file_name, String element);
}
