package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Incident;
import com.logicalis.apisolver.model.Journal;
import com.logicalis.apisolver.model.JournalInfo;
import com.logicalis.apisolver.model.ScRequestItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IJournalDAO extends CrudRepository<Journal, Long> {
    public Journal findTopByActive(boolean active);

    public List<Journal> findByIncident(Incident incident);

    public List<Journal> findJournalsByScRequestItem(ScRequestItem scRequestItem);

    public Journal findByIntegrationId(String integrationId);

    @Query(value = "SELECT id,\n" +
            "       active,\n" +
            "       created_on,\n" +
            "       element,\n" +
            "       integration_id,\n" +
            "       origin,\n" +
            "       value,\n" +
            "       create_by,\n" +
            "       incident\n" +
            "FROM   PUBLIC.journal\n" +
            "WHERE  incident = ?1\n" +
            "ORDER BY created_on DESC", nativeQuery = true)
    public List<JournalInfo> findInfoByIncident(Long id);

    @Query(value = "SELECT id,\n" +
            "       active,\n" +
            "       created_on,\n" +
            "       element,\n" +
            "       integration_id,\n" +
            "       origin,\n" +
            "       value,\n" +
            "       create_by,\n" +
            "       sc_request_item\n" +
            "FROM   PUBLIC.journal\n" +
            "WHERE  sc_request_item = ?1\n" +
            "ORDER BY created_on DESC", nativeQuery = true)
    public List<JournalInfo> findJournalInfoByScRequestItem(Long id);


   /* @Query(value = "select a.id,\n" +
            "a.active,\n" +
            "a.created_on,\n" +
            "a.element,\n" +
            "a.integration_id,\n" +
            "a.origin,\n" +
            "a.value,\n" +
            "a.create_by,\n" +
            "d.id AS incident,\n" +
            "c.id AS sc_request\n" +
            "FROM journal a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "INNER JOIN sc_request c ON b.sc_request = c.id\n" +
            "INNER JOIN incident d ON b.incident_parent = d.integration_id\n" +
            "WHERE (?1 = '' OR d.integration_id = ?1)\n" +
            "AND (?2 = '' OR c.integration_id = ?2)" +
            "ORDER BY a.created_on DESC", nativeQuery = true)*/

    @Query(value = "SELECT  a.id, a.created_on \n" +
            "FROM journal a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id  \n" +
            "INNER JOIN incident c ON b.incident_parent = c.integration_id\n" +
            "where ((?1 = '' AND b.incident_parent = '-1') OR b.incident_parent = ?1)\n" +
            "\n" +
            "UNION\n" +
            "\n" +
            "SELECT  a.id, a.created_on\n" +
            "FROM journal a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id \n" +
            "WHERE EXISTS (SELECT 'x' \n" +
            "\t\t\t\t\t   FROM sc_request_item ri \n" +
            "\t\t\t\t\t   WHERE " +
            "             b.sc_request = ri.sc_request" +
            "             AND ri.incident_parent IS NOT null\n" +
            "\t\t\t\t\t   AND ((?1 = '' AND ri.incident_parent = '-1') OR ri.incident_parent = ?1))\n" +
            "\t\t\t\t\t   \n" +
            "UNION\n" +
            "\n" +
            "SELECT a.id, a.created_on\n" +
            "FROM journal a\n" +
            "INNER JOIN incident b ON a.incident = b.id\n" +
            "WHERE ((?1 = '' AND b.integration_id = '-1') OR b.integration_id = ?1)\n" +
            "UNION\n" +
            "\t\t\t\n" +
            "\t\t\tSELECT  a.id, a.created_on\n" +
            "            FROM journal a\n" +
            "            INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "\t\t\tWHERE ((?2 = '' AND b.integration_id = '-1') OR b.integration_id = ?2)\n" +
            "\t\t\t\n" +
            "\t\t\tUNION\n" +
            "\t\t\t\n" +
            "\t\t\tSELECT a.id, a.created_on\n" +
            "            FROM journal a\n" +
            "            INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "\t\t\tWHERE " +
            "      EXISTS (SELECT 'x'\n" +
            "            FROM journal a1\n" +
            "            INNER JOIN sc_request_item b1 ON a1.sc_request_item = b1.id\n" +
            "\t\t\tWHERE b1.sc_request = b.sc_request =  AND ((?2 = '' AND b1.integration_id = '-1') OR b1.integration_id = ?2))\n" +
            "\t\t\t\n" +
            "\t\t\tUNION\n" +
            "\t\t\t\n" +
            "\t\t\tSELECT a.id, a.created_on\n" +
            "\t\t\tFROM journal a\n" +
            "\t\t\tINNER JOIN incident b ON a.incident = b.id\n" +
            "\t\t\tWHERE EXISTS (SELECT 'x' \n" +
            "            FROM journal a1\n" +
            "            INNER JOIN sc_request_item b1 ON a1.sc_request_item = b1.id\n" +
            "\t\t\tWHERE b1.incident_parent = b.integration_id AND ((?2 = '' AND b1.integration_id = '-1') OR b1.integration_id = ?2))\n" +
            "\t\t\t" +
            "ORDER BY created_on DESC", nativeQuery = true)
    public List<JournalInfo> findInfoByIncidentOrScRequestItem(String incident, String scRequestItem);


    @Query(value = "SELECT  a.id, a.created_on \n" +
            "FROM journal a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id  \n" +
            "INNER JOIN incident c ON b.incident_parent = c.integration_id\n" +
            "where ((?1 = '' AND b.incident_parent = '-1') OR b.incident_parent = ?1)\n" +
            "\n" +
            "UNION\n" +
            "\n" +
            "SELECT  a.id, a.created_on\n" +
            "FROM journal a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id \n" +
            "WHERE exists (SELECT 'c' \n" +
            "\t\t\t\t\t   FROM sc_request_item b1 \n" +
            "\t\t\t\t\t   WHERE b1.sc_request = b.sc_request" +
            "                 b1.incident_parent IS NOT null\n" +
            "\t\t\t\t\t   AND ((?1 = '' AND b1.incident_parent = '-1') OR b1.incident_parent = ?1))\n" +
            "\t\t\t\t\t   \n" +
            "UNION\n" +
            "\n" +
            "SELECT a.id, a.created_on\n" +
            "FROM journal a\n" +
            "INNER JOIN incident b ON a.incident = b.id\n" +
            "WHERE ((?1 = '' AND b.integration_id = '-1') OR b.integration_id = ?1)\n" +
            "ORDER BY created_on DESC", nativeQuery = true)
    public List<JournalInfo> findInfoByIncidentRelatedNotes(String incident);

    @Query(value = "SELECT  a.id, a.created_on\n" +
            "            FROM journal a\n" +
            "            INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "\t\t\tWHERE b.integration_id = ?1\n" +
            "\t\t\t\n" +
            "\t\t\tUNION\n" +
            "\t\t\t\n" +
            "\t\t\tSELECT a.id, a.created_on\n" +
            "            FROM journal a\n" +
            "            INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "WHERE exists (SELECT 'x'\n" +
            "                         FROM journal a1 \n" +
            "                         INNER JOIN sc_request_item b1 ON a1.sc_request_item = b1.id \n" +
            "                        WHERE b1.sc_request  = b.sc_request\n" +
            "                        and b1.integration_id = ?1) \n" +
            "\t\t\t\n" +
            "\t\t\tUNION\n" +
            "\t\t\t\n" +
            "\t\t\tSELECT a.id, a.created_on\n" +
            "\t\t\tFROM journal a\n" +
            "\t\t\tINNER JOIN incident b ON a.incident = b.id\n" +
            "      WHERE exists (SELECT 'x'\n" +
            "               FROM journal a1 \n" +
            "                    INNER JOIN sc_request_item b1 ON a1.sc_request_item = b1.id \n" +
            "               WHERE b1.incident_parent  = b.integration_id\n" +
            "               and  b1.integration_id = ?1) \n" +
            "\t\t\t" +
            "ORDER BY created_on DESC", nativeQuery = true)
    public List<JournalInfo> findInfoByScRequestRelatedNotes(String scRequestItem);

    public Journal findByInternalCheckpoint(String internalCheckpoint);

}
