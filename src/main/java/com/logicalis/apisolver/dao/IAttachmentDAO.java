package com.logicalis.apisolver.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.logicalis.apisolver.model.Attachment;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;

public interface IAttachmentDAO extends CrudRepository<Attachment, Long> {
        public Attachment findTopByActive(boolean active);

        Attachment findByIntegrationId(String integrationId);

        @Transactional
        @Modifying
        @Query("DELETE FROM Attachment a WHERE a.integrationId = :integrationId")
        void deleteByIntegrationId(@Param("integrationId") String integrationId);

        @Query(value = "SELECT id, \n" +
                        "content_type as contentType,\n" +
                        "element,\n" +
                        "download_linksn as downloadLinkSN, \n" +
                        "download_link as downloadLink, \n" +
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
                        "download_link AS downloadLink, \n" +
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
                        "download_link AS downloadLink, \n" +
                        "a.file_name AS fileName,\n" +
                        "a.integration_id AS integrationId,\n" +
                        "a.incident,\n" +
                        "a.sc_request_item AS scRequestItem\n" +
                        "FROM attachment a\n" +
                        "INNER JOIN sc_request_item b ON a.sc_request_item = b.id\n" +
                        "INNER JOIN sc_request c ON b.sc_request = c.id\n" +
                        "INNER JOIN incident d ON c.integration_id = d.sc_request_parent\n" +
                        "WHERE (?1 = '' OR d.integration_id = ?1)\n" +
                        "AND (?2 = '' OR c.integration_id = ?2)", nativeQuery = true)
        List<AttachmentInfo> findAttachmentsByIncidentOrScRequestItem(String incident, String scRequestItem);

        @Query(value = "SELECT A.ID,\n" +
                "A.CONTENT_TYPE AS CONTENTTYPE,\n" +
                "A.ELEMENT,\n" +
                "A.DOWNLOAD_LINKSN AS DOWNLOADLINKSN,\n" +
                "A.DOWNLOAD_LINK AS DOWNLOADLINK,\n" +
                "A.FILE_NAME AS FILENAME,\n" +
                "A.INTEGRATION_ID AS INTEGRATIONID,\n" +
                "A.SC_TASK AS SCTASK,\n" +
                "A.SC_REQUEST_ITEM AS SCREQUESTITEM\n" +
                "FROM ATTACHMENT A\n" +
                "INNER JOIN SC_TASK B ON A.SC_TASK = B.ID\n" +
                "WHERE A.SC_TASK = ?1\n" +
                "\n" +
                "UNION\n" +
                "\n" +
                "SELECT A.ID,\n" +
                "A.CONTENT_TYPE AS CONTENTTYPE,\n" +
                "A.ELEMENT,\n" +
                "A.DOWNLOAD_LINKSN AS DOWNLOADLINKSN,\n" +
                "A.DOWNLOAD_LINK AS DOWNLOADLINK,\n" +
                "A.FILE_NAME AS FILENAME,\n" +
                "A.INTEGRATION_ID AS INTEGRATIONID,\n" +
                "A.SC_TASK AS SCTASK,\n" +
                "A.SC_REQUEST_ITEM AS SCREQUESTITEM\n" +
                "FROM ATTACHMENT A\n" +
                "INNER JOIN SC_REQUEST_ITEM B ON A.SC_REQUEST_ITEM = B.ID\n" +
                "WHERE EXISTS (SELECT 1 FROM SC_TASK K WHERE K.ID = ?1 AND K.SC_REQUEST_ITEM = B.ID)\n" +
                "AND NOT EXISTS (\n" +
                "    SELECT 1 FROM ATTACHMENT A2\n" +
                "    WHERE A2.SC_TASK = ?1\n" +
                "    AND A2.CONTENT_TYPE = A.CONTENT_TYPE\n" +
                "    AND A2.FILE_NAME = A.FILE_NAME\n" +
                ")", nativeQuery = true)
         List<AttachmentInfo> findAttachmentsByScTask(Long id);

        public List<Attachment> findByElement(String integrationId);

    
        public List<Attachment> findSolverByElement(String integrationId);
}
