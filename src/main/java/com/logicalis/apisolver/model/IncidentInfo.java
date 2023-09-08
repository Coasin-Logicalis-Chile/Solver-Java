package com.logicalis.apisolver.model;

public interface IncidentInfo {
    CompanyInfo getCompany();

    interface CompanyInfo {
        String getName();
    }
}
