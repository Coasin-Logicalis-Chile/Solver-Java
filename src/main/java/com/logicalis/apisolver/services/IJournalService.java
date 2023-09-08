
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.Journal;
import com.logicalis.apisolver.model.JournalInfo;
import com.logicalis.apisolver.model.ScRequestItem;

import java.util.List;

public interface IJournalService {

    public List<Journal> findAll();

    public Journal save(Journal journal);

    public Journal findById(Long id);

    public List<Journal> findByIncident(Incident incident);

    public List<Journal> findJournalsByScRequestItem(ScRequestItem scRequestItem);

    public List<JournalInfo> findInfoByIncident(Long id);

    public List<JournalInfo> findJournalInfoByScRequestItem(Long id);

    public List<JournalInfo> findInfoByIncidentOrScRequestItem(String incident, String scRequestItem);

    public void delete(Long id);

    public List<Journal> findByActive(boolean active);

    public Journal findTopByActive(boolean active);

    public Journal findByIntegrationId(String integrationId);

    public Journal findByInternalCheckpoint(String internalCheckpoint);

    public List<JournalInfo> findInfoByIncidentRelatedNotes(String incident);

    public List<JournalInfo> findInfoByScRequestRelatedNotes(String scRequestItem);
}
