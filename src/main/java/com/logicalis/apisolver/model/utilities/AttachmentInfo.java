package com.logicalis.apisolver.model.utilities;

public interface AttachmentInfo {
    Long getId();

    String getContentType();

    String getDownloadLinkSN();

    String getDownloadLink();

    String getFileName();

    String getIntegrationId();

    String getElement();

    Long getIncident();

    Long getScRequestItem();

    Long getScTask();
}
