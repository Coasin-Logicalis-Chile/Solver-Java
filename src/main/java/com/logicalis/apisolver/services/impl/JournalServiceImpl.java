
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IJournalDAO;
import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.Journal;
import com.logicalis.apisolver.model.JournalInfo;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.services.IJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JournalServiceImpl implements IJournalService {

    @Autowired
    private IJournalDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<Journal> findAll() {
        return (List<Journal>) dao.findAll();
    }

    @Override
    public Journal save(Journal journal) {
        return dao.save(journal);
    }

    @Override
    public Journal findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<Journal> findByActive(boolean active) {
        return null;
    }

    @Override
    public Journal findTopByActive(boolean active) {
        return (Journal) dao.findTopByActive(active);
    }

    @Override
    public List<Journal> findByIncident(Incident incident) {
        return (List<Journal>) dao.findByIncident(incident);
    }


    @Override
    public List<Journal> findJournalsByScRequestItem(ScRequestItem scRequestItem) {
        return dao.findJournalsByScRequestItem(scRequestItem);

    }

    @Override
    public Journal findByIntegrationId(String integrationId) {
        return (Journal) dao.findByIntegrationId(integrationId);
    }

    @Override
    public Journal findByInternalCheckpoint(String internalCheckpoint) {
        return dao.findByInternalCheckpoint(internalCheckpoint);
    }

    @Override
    public List<JournalInfo> findInfoByIncidentRelatedNotes(String incident) {
        return dao.findInfoByIncidentRelatedNotes(incident);
    }

    @Override
    public List<JournalInfo> findInfoByScRequestRelatedNotes(String scRequestItem) {
        return dao.findInfoByScRequestRelatedNotes(scRequestItem);
    }

    @Override
    public List<JournalInfo> findInfoByIncident(Long id) {
        return dao.findInfoByIncident(id);
    }

    @Override
    public List<JournalInfo> findJournalInfoByScRequestItem(Long id) {
        return dao.findJournalInfoByScRequestItem(id);
    }

    @Override
    public List<JournalInfo> findInfoByIncidentOrScRequestItem(String incident, String scRequestItem) {
        return dao.findInfoByIncidentOrScRequestItem(incident, scRequestItem);
    }


}
