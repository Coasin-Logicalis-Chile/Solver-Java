package com.logicalis.apisolver.util;

import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.servicenow.SnAttachment;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    //private String remoteHost = "portalsolver.westus2.cloudapp.azure.com";
    private final static String remoteHost = "10.10.0.5";
    private final static String username = "solveruser";
    private final static String password = "Coasinlogic2022$";
    private final static String Date_REGEX = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$";
    private final static Pattern Date_PATTERN = Pattern.compile(Date_REGEX);
    //COLORS
    @Autowired
    private Environment environment;
    public final String ANSI_RESET = "\u001B[0m";

    // Declaring the color
    // Custom declaration
    public final String ANSI_YELLOW = "\u001B[33m";

    public boolean dateValidator(String date) {
        Matcher matcher = Date_PATTERN.matcher(date);
        return matcher.matches();
    }

    public static String getInstanceServiceNow() {
        System.out.println("PATH_INSTANCE_SERVICENOW");
        System.out.println(System.getenv("PATH_INSTANCE_SERVICENOW"));
        return System.getenv("PATH_INSTANCE_SERVICENOW");
    }

    public void uploadFile(File localFile, SnAttachment snAttachment) throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();
        String tag = "[Upload File]";
        try {
            System.out.println(environment.getProperty("setting.attachments.dir").concat(snAttachment.getSys_id().concat(".").concat(snAttachment.getFile_name().split("\\s*[.]+")[1])));
            sftpClient.put(new FileSystemFile(localFile), environment.getProperty("remote.dir").concat(snAttachment.getSys_id().concat(".").concat(snAttachment.getFile_name().split("\\s*[.]+")[1])));
        } catch (Exception e) {
            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            e.printStackTrace();
        }
        sftpClient.close();
        sshClient.disconnect();
    }

    private SSHClient setupSshj() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(remoteHost);
        client.authPassword(username, password);
        return client;
    }

    public String parseJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();
        try {
            return ((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String parseJson(String journal, String levelOne) {
        JSONParser parser = new JSONParser();
        try {
            return ((JSONObject) parser.parse(journal)).get(levelOne).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean parseBooleanJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();
        try {
            return Boolean.parseBoolean(((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Long parseIdJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();
        try {
            return (Long) ((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.valueOf(0);
    }

    public String getIdByJson(JSONObject jsonObject, String levelOne, String levelTwo) {
        String id = "";
        if (jsonObject != null) {
            JSONObject object = (JSONObject) jsonObject;
            if (object != null) {
                if (object.get(levelOne) != null) {
                    if (!object.get(levelOne).equals("")) {
                        JSONObject json = (JSONObject) object.get(levelOne);
                        if (json != null) {
                            if (json.get(levelTwo) != null) {
                                if (!json.get(levelTwo).equals("")) {
                                    id = (String) json.get(levelTwo);
                                    if (id.contains("sicredi.net"))
                                        id = "";
                                }
                            }
                        }
                    }
                }
            }
        }
        return id;
    }

    public String getIdByJson(JSONObject jsonObject, String levelOne) {
        String id = "";
        if (jsonObject != null) {
            JSONObject json = (JSONObject) jsonObject;
            if (json != null) {
                if (json.get(levelOne) != null) {
                    if (!json.get(levelOne).equals("")) {
                        id = (String) json.get(levelOne);
                        if (id.contains("sicredi.net"))
                            id = "";
                    }
                }
            }
        }
        return id;
    }

    public String isNull(String a) {
        return a == null || a.trim() == "" ? "" : a.trim();
    }

    public boolean isNullBool(String a) {
        return a == null || a == "";
    }

    public String isNull(String a, String b) {
        return a == null ? b : a;
    }

    public String isNullIntegration(Object o) {
        if (o != null) {
            String className = o.getClass().getName();
            if (className.equals(new SysUser().getClass().getName())) {
                SysUser sysUser = (SysUser) o;
                return sysUser.getIntegrationId();
            } else if (className.equals(new SysGroup().getClass().getName())) {
                SysGroup sysGroup = (SysGroup) o;
                return sysGroup.getIntegrationId();
            }
        }
        return "";
    }


    public boolean hasData(String a) {
        return a == null || a == "" ? false : true;
    }

    public boolean hasData(Object a) {
        return a == null || a == "" ? false : true;
    }

    public Domain filterDomain(List<Domain> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public SysGroup filterSysGroup(List<SysGroup> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public Company filterCompany(List<Company> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public SysUser filterSysUser(List<SysUser> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public CiService filterCiService(List<CiService> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }


    public ConfigurationItem filterConfigurationItem(List<ConfigurationItem> list, String filter) {
        if (filter.contains("sicredi.net"))
            return null;
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }


    public Location filterLocation(List<Location> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public Department filterDepartments(List<Department> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public void printData(String tag, int count, String levelOne) {
        System.out.print(tag);
        System.out.println("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne));
    }

    public void printData(String tag, int count, String levelOne, String levelTwo) {
        System.out.print(tag);
        System.out.println("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo));
    }

    public void printData(String tag, int count, String levelOne, String levelTwo, String levelThree) {
        System.out.print(tag);
        System.out.println("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree));
    }

    public void printData(String tag, String levelOne, String levelTwo, String levelThree) {
        System.out.print("\u001B[37m".concat(tag));
        System.out.println(String.valueOf(levelOne).concat(" ")
                .concat(" ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree));
    }

    public void printData(String tag, String levelOne) {
        System.out.print(tag);
        System.out.println("\u001B[37m(".concat(String.valueOf(levelOne)).concat(" "));
    }


    public void printData(String tag, int count, String levelOne, String levelTwo, String levelThree, String levelFour) {
        System.out.print(tag);
        System.out.println("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree)
                .concat(" | ")
                .concat(levelFour));
    }

    public String getFieldDisplay(SysAudit sysAudit) {
        return sysAudit != null ? sysAudit.getDocumentkey() != "" ? sysAudit.getDocumentkey() : "SysAudit" : "SysAudit";
    }

    public String getFieldDisplay(SysDocumentation sysDocumentation) {
        return sysDocumentation != null ? sysDocumentation.getUniqueIdentifier() != "" ? sysDocumentation.getUniqueIdentifier() : "SysDocumentation" : "SysDocumentation";
    }

    public String getFieldDisplay(SysGroup group) {
        return group != null ? group.getName() != "" ? group.getName() : "SysGroup" : "SysGroup";
    }

    public String getFieldDisplay(ScTask scTask) {
        return scTask != null ? scTask.getNumber() != "" ? scTask.getNumber() : "ScTask" : "ScTask";
    }

    public String getFieldDisplay(TaskSla taskSla) {
        return taskSla != null ? getFieldDisplay(taskSla.getDomain()) : "TaskSlaByCompany";
    }

    public String getFieldDisplay(Choice choice) {
        return choice != null ? choice.getName() != "" ? choice.getName() : "Choice" : "Choice";
    }

    public String getFieldDisplay(ContractSla contractSla) {
        return contractSla != null ? contractSla.getName() != "" ? contractSla.getName() : App.ContractSla() : App.ContractSla();
    }


    public String getFieldDisplay(CmnSchedule cmnSchedule) {
        return cmnSchedule != null ? cmnSchedule.getName() != "" ? cmnSchedule.getName() : App.CmnSchedule() : App.CmnSchedule();
    }


    public String getFieldDisplay(Incident incident) {
        return incident != null ? incident.getNumber() != "" ? incident.getNumber() : App.Incident() : App.Incident();
    }

    public String getFieldDisplay(Attachment attachment) {
        return attachment != null ? attachment.getFileName() != "" ? attachment.getFileName() : "Attachment" : "Attachment";
    }

    public String getFieldDisplay(SysUserGroup sysUserGroup) {
        return sysUserGroup != null ? sysUserGroup.getSysGroup() != null ? isNull(sysUserGroup.getSysGroup().getName(), "SysUserGroup") : "SysUserGroup" : "SysUserGroup";
    }

    public String getFieldDisplay(CatalogLine catalogLine) {
        return catalogLine != null ? catalogLine.getNumber() != "" ? catalogLine.getNumber() : App.CatalogLine() : App.CatalogLine();
    }

    public String getFieldDisplay(ScRequest scRequest) {
        return scRequest != null ? scRequest.getNumber() != "" ? scRequest.getNumber() : App.ScRequest() : App.ScRequest();
    }

    public String getFieldDisplay(ScRequestItem scRequestItem) {
        return scRequestItem != null ? scRequestItem.getNumber() != "" ? scRequestItem.getNumber() : App.ScRequestItem() : App.ScRequestItem();
    }

    public String getFieldDisplay(Company company) {
        return company != null ? company.getName() != "" ? company.getName() : App.Company() : App.Company();
    }

    public String getFieldDisplay(Domain domain) {
        return domain != null ? domain.getName() != "" ? domain.getName() : App.Domain() : App.Domain();
    }

    public String getFieldDisplay(SysUser sysUser) {
        return sysUser != null ? sysUser.getName() != "" ? sysUser.getName() : App.SysUser() : App.SysUser();
    }

    private static ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static ZoneId LOCAL_ZONE = ZoneId.of("America/Santiago");

    public LocalDateTime getLocalDateTime(String data) {
        if (data != "" && data != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
            return dateTime.atZone(UTC_ZONE).withZoneSameInstant(LOCAL_ZONE).toLocalDateTime();
        }
        return null;
    }

    public String LocalDateTimeToString(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        } else
            return "";
    }

    public String getFieldDisplay(Location location) {
        return location != null ? location.getName() != "" ? location.getName() : App.Location() : App.Location();
    }

    public String getFieldDisplay(Department department) {
        return department != null ? department.getName() != "" ? department.getName() : App.Department() : App.Department();
    }

    public String getFieldDisplay(ConfigurationItem configurationItem) {
        return configurationItem != null ? configurationItem.getName() != "" ? configurationItem.getName() : App.ConfigurationItem() : App.ConfigurationItem();
    }

    public String getFieldDisplay(CiService ciService) {
        return ciService != null ? ciService.getName() != "" ? ciService.getName() : App.CiService() : App.CiService();
    }

    public String getFieldDisplay(Journal journal) {
        return journal != null ? journal.getElement() != "" ? journal.getElement() : App.Journal() : App.Journal();
    }

    public String[] offSets65000() {
        return new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
    }

    public String[] offSets100000() {
        return new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000", "70000", "75000", "80000", "85000", "90000", "95000", "100000"};
    }

    public String[] offSets99000() {
        return new String[]{"0", "3000", "6000", "9000", "12000", "15000", "18000", "21000", "24000", "27000", "30000", "33000", "36000", "39000", "42000", "45000", "48000", "51000", "54000", "57000", "60000", "63000", "66000", "69000", "72000", "75000", "78000", "81000", "84000", "87000", "90000", "93000", "96000", "99000"};
    }

    public String[] offSets1500000() {
        int off = 1500000;
        ArrayList<String> offsets = new ArrayList<>();
        int size = Math.floorMod(off, 30000);

        for (int i = 0; i <= off; i = i + 3000) {
            offsets.add(String.valueOf(i));
        }
        final int[] count = {0};
        String[] response = new String[offsets.toArray().length];
        offsets.forEach(offSet -> {
            response[count[0]] = offSet;
            count[0]++;
        });
        return response;
    }

    public String[] offSets7500000() {
        int off = 7500000;
        ArrayList<String> offsets = new ArrayList<>();
        int size = Math.floorMod(off, 30000);

        for (int i = 0; i <= off; i = i + 3000) {
            offsets.add(String.valueOf(i));
        }
        final int[] count = {0};
        String[] response = new String[offsets.toArray().length];
        offsets.forEach(offSet -> {
            response[count[0]] = offSet;
            count[0]++;
        });
        return response;
    }

    public String[] offSets3000() {
        return new String[]{"0", "3000"};
    }

    public String[] offSets6000() {
        int off = 6000;
        ArrayList<String> offsets = new ArrayList<>();
        int size = Math.floorMod(off, 6000);

        for (int i = 0; i <= off; i = i + 3000) {
            offsets.add(String.valueOf(i));
        }
        final int[] count = {0};
        String[] response = new String[offsets.toArray().length];
        offsets.forEach(offSet -> {
            response[count[0]] = offSet;
            count[0]++;
        });
        return response;
    }

    public String[] offSets50000() {
        int off = 50000;
        ArrayList<String> offsets = new ArrayList<>();
        int size = Math.floorMod(off, 50000);

        for (int i = 0; i <= off; i = i + 2000) {
            offsets.add(String.valueOf(i));
        }
        final int[] count = {0};
        String[] response = new String[offsets.toArray().length];
        offsets.forEach(offSet -> {
            response[count[0]] = offSet;
            count[0]++;
        });
        return response;
    }

    public String[] offSets500000() {
        int off = 500000;
        ArrayList<String> offsets = new ArrayList<>();
        int size = Math.floorMod(off, 30000);

        for (int i = 0; i <= off; i = i + 3000) {
            offsets.add(String.valueOf(i));
        }
        final int[] count = {0};
        String[] response = new String[offsets.toArray().length];
        offsets.forEach(offSet -> {
            response[count[0]] = offSet;
            count[0]++;
        });
        return response;
    }

    /* public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo){
         String integrationId = getIdByJson(jsonObject, levelOne, levelTwo);
         if (util.hasData(integrationId)) {
             return sysUserService.findByIntegrationId(integrationId);
         } else
             return null;
     }*/
    public String removeImg(String content) {
        Document document = Jsoup.parse(content);
        document.select("img").remove();
        return document.toString();
        //return Jsoup.parse(content).text();
    }

    private String styleImg(String width, String height) {
        return "width: ".concat(width).concat("px !important").concat("; height: ").concat(height).concat("px !important").concat(";");
    }

    public String reeplaceImg(String content) {
        try {
            if (content.toLowerCase().contains("img") && content.toLowerCase().contains("<html>")) {
                //    content = "<html>".concat(content.split("<html>")[1]);
            /*if (content.toLowerCase().contains("img") && content.toLowerCase().contains("<html>")) {
                content = "<html>".concat(content.split("<html>")[1]);*/
                Document document = Jsoup.parse(content, "UTF-8");
                document.select("html").select("img").forEach(img -> {
                    String width = getDigits(img.toString().substring(img.toString().lastIndexOf("width="), img.toString().lastIndexOf("width=") + 12));
                    String height = getDigits(img.toString().substring(img.toString().lastIndexOf("height="), img.toString().lastIndexOf("height=") + 12));
                    Element resize = document.createElement("img")
                            .attr("style", styleImg(width, height))
                            .attr("src", img.attr("src"));
                    img.replaceWith(resize);
                });
                return document.html();
            }
            return content;
        } catch (Exception e) {
            System.out.println("Error IMG: ".concat(e.getMessage()));
            System.out.println("Error IMG Content: ".concat(content));
            return content;
        }
    }

    String getDigits(String value) {
        Matcher matcher = Pattern.compile("\\d+").matcher(value);
        String digits = "";
        while (matcher.find()) {
            digits = digits.concat(matcher.group());
        }
        return digits;
    }
}
