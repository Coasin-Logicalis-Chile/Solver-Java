package com.logicalis.apisolver.model.enums;

import java.util.Base64;

import org.springframework.core.env.Environment;

public class App {
    public static String SNDevelopment() {
        return "lalogicalisdev";
    }

    public static String SNTests() {
        return "lalogicalistest";
    }

    public static String SNProduction() {
        return "lalogicalis";
    }

    private static Environment environment;

    public static void setEnvironment(Environment pEnv) {
        environment = pEnv;
    }

    public static String SNInstance() {
        return "https://".concat("lalogicalis").concat(".service-now.com");
    }

    public static String SNUser() {
        return environment.getProperty("app.servicenow.user");
    }

    public static String SNPassword() {
        return new String(Base64.getDecoder().decode(environment.getProperty("app.servicenow.password")));
    }

    public static String Name() {
        return "Name";
    }

    public static String Title() {
        return "Title";
    }

    public static String Domain() {
        return "Domain";
    }

    public static String Value() {
        return "value";
    }

    public static String Company() {
        return "Company";
    }

    public static String Location() {
        return "Location";
    }

    public static String SysUser() {
        return "SysUser";
    }

    public static String ScRequest() {
        return "ScRequest";
    }

    public static String ScRequestItem() {
        return "ScRequestItem";
    }

    public static String Department() {
        return "Department";
    }

    public static String CmnSchedule() {
        return "CmnSchedule";
    }

    public static String ContractSla() {
        return "ContractSla";
    }

    public static String Incident() {
        return "Incident";
    }

    public static String ConfigurationItem() {
        return "ConfigurationItem";
    }

    public static String CatalogLine() {
        return "CatalogLine";
    }

    public static String CiService() {
        return "CiService";
    }

    public static String Journal() {
        return "Journal";
    }

    public static String End() {
        return "•••••••••••••••• [ END ] ••••••••••••••••";
    }

    public static String Start() {
        return "•••••••••••••••• [ START ] ••••••••••••••••";
    }

    public static String CreateConsole() {

        return "\u001B[33m( CREATE ) ";
    }

    public static String UpdateConsole() {
        return "\u001B[36m( UPDATE ) ";
    }

    public static String TableData() {
        return "<table style=\"border-collapse: collapse; border-style: none; border-color: transparent;\" border=\"1\">\n" + "<tbody>\n" + "TBODY\n" + "</tbody>\n" + "</table>";
    }

    public static String TRData() {
        return "<tr>\n" + "<td style=\"text-align: right;\">FIELD: </td>\n" + "<td style=\"text-align: right;\">&nbsp;</td>\n" + "<td style=\"text-align: left;\">VALUE</td>\n" + "</tr>";
    }

    public static String OriginFieldChanges() {
        return "field_changes";
    }

}
