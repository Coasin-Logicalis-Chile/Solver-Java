package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.Attachment;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IAttachmentDAO extends CrudRepository<Attachment, Long> {
    public Attachment findTopByActive(boolean active);

    Attachment findByIntegrationId(String integrationId);

    @Query(value = "SELECT id, \n" +
            "content_type as contentType,\n" +
            "element,\n" +
            "download_linksn as downloadLinkSN, \n" +
            "file_name as fileName, \n" +
            "integration_id as integrationId, \n" +
            "incident\n" +
            "FROM attachment\n" +
            "WHERE (?1 = 0 OR incident = ?1)", nativeQuery = true)
    List<AttachmentInfo> findAttachmentsByIncident(Long id);

    @Query(value = "SELECT id, \n" +
            "content_type AS contentType,\n" +
            "element,\n" +
            "download_linksn AS downloadLinkSN, \n" +
            "file_name AS fileName, \n" +
            "integration_id AS integrationId, \n" +
            "sc_request_item AS scRequestItem\n" +
            "FROM attachment\n" +
            "WHERE (?1 = 0 OR sc_request_item = ?1)", nativeQuery = true)
    List<AttachmentInfo> findAttachmentsByScRequestItem(Long id);

    @Query(value = "SELECT a.id,\n" +
            "a.content_type AS contentType,\n" +
            "a.element,\n" +
            "a.download_linksn AS downloadLinkSN,\n" +
            "a.file_name AS fileName,\n" +
            "a.integration_id AS integrationId,\n" +
            "a.incident,\n" +
            "a.sc_request_item AS scRequestItem\n" +
            "FROM attachment a\n" +
            "INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
            "INNER JOIN sc_request c ON b.sc_request = c.id\n" +
            "INNER JOIN incident d ON c.sc_request_parent = d.integration_id\n" +
            "WHERE (?1 = '' OR d.integration_id = ?1)\n" +
            "AND (?2 = '' OR c.integration_id = ?2)", nativeQuery = true)
    List<AttachmentInfo> findAttachmentsByIncidentOrScRequestItem(String incident, String scRequestItem);

    /* @Query(value = "SELECT id, \n" +
             "content_type AS contentType,\n" +
             "element,\n" +
             "download_linksn AS downloadLinkSN, \n" +
             "file_name AS fileName, \n" +
             "integration_id AS integrationId, \n" +
             "sc_task AS scTask\n" +
             "FROM attachment\n" +
             "WHERE (?1 = 0 OR sc_task = ?1)", nativeQuery = true)*/
    @Query(value = "SELECT A.ID,\n" +
            "A.CONTENT_TYPE AS CONTENTTYPE,\n" +
            "A.ELEMENT,\n" +
            "A.DOWNLOAD_LINKSN AS DOWNLOADLINKSN,\n" +
            "A.FILE_NAME AS FILENAME,\n" +
            "A.INTEGRATION_ID AS INTEGRATIONID,\n" +
            "A.SC_TASK AS SCTASK,\n" +
            "A.SC_REQUEST_ITEM AS SCREQUESTITEM\n" +
            "FROM ATTACHMENT A\n" +
            "INNER JOIN SC_REQUEST_ITEM B ON A.SC_REQUEST_ITEM = B.ID\n" +
            "AND exists (SELECT 'x' FROM SC_TASK K WHERE k.ID = ?1 and k.SC_REQUEST_ITEM = b.id)\n" +
            "UNION\n" +
            "SELECT \n" +
            "A.ID,\n" +
            "A.CONTENT_TYPE AS CONTENTTYPE,\n" +
            "A.ELEMENT,\n" +
            "A.DOWNLOAD_LINKSN AS DOWNLOADLINKSN,\n" +
            "A.FILE_NAME AS FILENAME,\n" +
            "A.INTEGRATION_ID AS INTEGRATIONID,\n" +
            "A.SC_TASK AS SCTASK,\n" +
            "A.SC_REQUEST_ITEM AS SCREQUESTITEM\n" +
            "FROM ATTACHMENT A\n" +
            "INNER JOIN SC_TASK B ON A.SC_TASK = B.ID\n" +
            "INNER JOIN SC_REQUEST_ITEM C ON B.SC_REQUEST_ITEM = C.ID\n" +
            "WHERE A.SC_TASK = ?1", nativeQuery = true)
    List<AttachmentInfo> findAttachmentsByScTask(Long id);

    public List<Attachment> findByElement(String integrationId);

    public List<Attachment> findSolverByElement(String integrationId);

    //public Attachment uploadFile(MultipartFile file_name, String element);
}
