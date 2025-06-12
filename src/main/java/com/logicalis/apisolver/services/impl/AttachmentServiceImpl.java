package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IAttachmentDAO;
import com.logicalis.apisolver.model.Attachment;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;
import com.logicalis.apisolver.services.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttachmentServiceImpl implements IAttachmentService {
    @Autowired
    private IAttachmentDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> findAll() {
        return (List<Attachment>) dao.findAll();
    }

    @Override
    public Attachment save(Attachment attachment) {
        return dao.save(attachment);
    }

    @Override
    public void deleteByIdDirect(Long id) {
        dao.deleteByIdDirect(id); // LLama al método del repositorio
    }

    @Override
    public Attachment findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<Attachment> findByActive(boolean active) {
        return null;
    }

    @Override
    public Attachment findTopByActive(boolean active) {
        return (Attachment) dao.findTopByActive(active);
    }

    @Override
    public Attachment findByIntegrationId(String integrationId) {
        return (Attachment) dao.findByIntegrationId(integrationId);
    }

    @Override
    public List<AttachmentInfo> findAttachmentsByIncident(Long id) {
        return dao.findAttachmentsByIncident(id);
    }

    @Override
    public List<AttachmentInfo> findAttachmentsByIncidentOrScRequestItem(String incident, String scRequestItem) {
        return dao.findAttachmentsByIncidentOrScRequestItem(incident, scRequestItem);
    }

    @Override
    public List<AttachmentInfo> findAttachmentsByScRequestItem(Long id) {
        return dao.findAttachmentsByScRequestItem(id);
    }

    @Override
    public List<AttachmentInfo> findAttachmentsByScTask(Long id) {
        return dao.findAttachmentsByScTask(id);
    }

    @Override
    public List<Attachment> findByElement(String integrationId) {
        return dao.findByElement(integrationId);
    }
    @Override
    public List<Attachment> findSolverByElement(String integrationId) {
        return dao.findByElement(integrationId);
    }
}
