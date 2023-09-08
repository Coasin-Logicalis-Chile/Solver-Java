package com.logicalis.apisolver.view;

import java.io.Serializable;

public class DeleteRecordRequest implements Serializable {
    private String element;
    private String integrationId;

    public DeleteRecordRequest(String element, String integrationId) {
        this.element = element;
        this.integrationId = integrationId;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }
}
