package com.logicalis.apisolver.model.enums;

import com.logicalis.apisolver.util.Util;

import java.util.Base64;

public class App {

    public String SNDevelopment() {
        return "lalogicalisdev";
    }


    public String SNTests() {
        return "lalogicalistest";
    }

    public String SNProduction() {
        return "lalogicalis";
    }

    public String SNInstance() {
        return "https://".concat(new Util().getInstanceServiceNow()).concat(".service-now.com");
    }

    public String SNUser() {
        return "solver";
    }

    public String SNPassword() {
        return new String(Base64.getDecoder().decode("U1ZlbFVPMCFMLVdXcFZBMF1FJEskdSM0YlI="));
    }

    public String Name() {
        return "Name";
    }

    public String Title() {
        return "Title";
    }

    public String Domain() {
        return "Domain";
    }

    public String Value() {
        return "value";
    }

    public String Company() {
        return "Company";
    }

    public String Location() {
        return "Location";
    }

    public String SysUser() {
        return "SysUser";
    }

    public String ScRequest() {
        return "ScRequest";
    }

    public String ScRequestItem() {
        return "ScRequestItem";
    }

    public String Department() {
        return "Department";
    }

    public String CmnSchedule() {
        return "CmnSchedule";
    }

    public String ContractSla() {
        return "ContractSla";
    }

    public String Incident() {
        return "Incident";
    }

    public String ConfigurationItem() {
        return "ConfigurationItem";
    }

    public String CatalogLine() {
        return "CatalogLine";
    }

    public String CiService() {
        return "CiService";
    }

    public String Journal() {
        return "Journal";
    }

    public String End() {
        return "•••••••••••••••• [ END ] ••••••••••••••••";
    }

    public String Start() {
        return "•••••••••••••••• [ START ] ••••••••••••••••";
    }

    public String CreateConsole() {

        return "\u001B[33m( CREATE ) ";
    }

    public String UpdateConsole() {
        return "\u001B[36m( UPDATE ) ";
    }

    public String TableData() {
        return "<table style=\"border-collapse: collapse; border-style: none; border-color: transparent;\" border=\"1\">\n" + "<tbody>\n" + "TBODY\n" + "</tbody>\n" + "</table>";
    }

    public String TRData() {
        return "<tr>\n" + "<td style=\"text-align: right;\">FIELD: </td>\n" + "<td style=\"text-align: right;\">&nbsp;</td>\n" + "<td style=\"text-align: left;\">VALUE</td>\n" + "</tr>";
    }

    public String OriginFieldChanges() {
        return "field_changes";
    }

}