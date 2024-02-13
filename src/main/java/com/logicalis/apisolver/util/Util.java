package com.logicalis.apisolver.util;

import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.servicenow.SnAttachment;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class Util {
    //private String remoteHost = "portalsolver.westus2.cloudapp.azure.com";
    private final static String Date_REGEX = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$";
    private final static Pattern Date_PATTERN = Pattern.compile(Date_REGEX);
    private static Environment environment;
    //COLORS
    public final static String ANSI_RESET = "\u001B[0m";

    // Declaring the color
    // Custom declaration
    public final static String ANSI_YELLOW = "\u001B[33m";

    public static boolean dateValidator(String date) {
        Matcher matcher = Date_PATTERN.matcher(date);
        return matcher.matches();
    }

    public static String getInstanceServiceNow() {
        log.info("PATH_INSTANCE_SERVICENOW");
        log.info(System.getenv("PATH_INSTANCE_SERVICENOW"));
        return System.getenv("PATH_INSTANCE_SERVICENOW");
    }

    private static SSHClient setupSshj() throws IOException {
        try (SSHClient client = new SSHClient()) {
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(environment.getProperty("app.sshclient.remotehost"));
            client.authPassword(environment.getProperty("app.sshclient.username"), environment.getProperty("app.sshclient.password"));
            return client;
        } catch (IOException e) {
            log.error(e.toString());
            throw new IOException(e);
        }
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment pEnv) {
        environment = pEnv;
    }

    public void uploadFile(File localFile, SnAttachment snAttachment) throws IOException {
        String tag = "[Upload File]";
        SSHClient sshClient = setupSshj();
        if(!Objects.isNull(sshClient)) {
            try {
                SFTPClient sftpClient = sshClient.newSFTPClient();
                if (!Objects.isNull(sftpClient)) {
                    log.info(environment.getProperty("setting.attachments.dir").concat(snAttachment.getSys_id().concat(".").concat(snAttachment.getFile_name().split("\\s*[.]+")[1])));
                    sftpClient.put(new FileSystemFile(localFile), environment.getProperty("remote.dir").concat(snAttachment.getSys_id().concat(".").concat(snAttachment.getFile_name().split("\\s*[.]+")[1])));
                    sftpClient.close();
                }
            }catch(IOException e){
                log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            }finally {
                sshClient.disconnect();
            }
        }
    }

    public static String parseJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();

        try {
            return ((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo).toString();
        } catch (ParseException e) {
            log.error("Context", e);
        }
        return null;
    }

    public static String parseJson(String journal, String levelOne) {
        JSONParser parser = new JSONParser();
        try {
            return ((JSONObject) parser.parse(journal)).get(levelOne).toString();
        } catch (ParseException e) {
            log.error("Context", e);
        }
        return "";
    }

    public static boolean parseBooleanJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();
        try {
            return Boolean.parseBoolean(((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo).toString());
        } catch (ParseException e) {
            log.error("Context", e);
        }
        return false;
    }

    public static Long parseIdJson(String journal, String levelOne, String levelTwo) {
        JSONParser parser = new JSONParser();
        try {
            return (Long) ((JSONObject) parser.parse(((JSONObject) parser.parse(journal)).get(levelOne).toString())).get(levelTwo);
        } catch (ParseException e) {
            log.error("Context", e);
        }
        return Long.valueOf(0);
    }

    public static String getIdByJson(JSONObject jsonObject, String levelOne, String levelTwo) {
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

    public static String getIdByJson(JSONObject jsonObject, String levelOne) {
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

    public static String isNull(String a) {
        return a == null || a.trim().equals("") ? "" : a.trim();
    }

    public static boolean isNullBool(String a) {
        return a == null || a.equals("");
    }

    public static String isNull(String a, String b) {
        return a == null ? b : a;
    }

    public static String isNullIntegration(Object o) {
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

    public static boolean hasData(String a) {
        return a == null || a.equals("") ? false : true;
    }

    public static boolean hasData(Object a) {
        return a == null || a == "" ? false : true;
    }

    public static Domain filterDomain(List<Domain> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static SysGroup filterSysGroup(List<SysGroup> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static Company filterCompany(List<Company> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static SysUser filterSysUser(List<SysUser> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static CiService filterCiService(List<CiService> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static ConfigurationItem filterConfigurationItem(List<ConfigurationItem> list, String filter) {
        if (filter.contains("sicredi.net"))
            return null;
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }


    public static Location filterLocation(List<Location> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static Department filterDepartments(List<Department> list, String filter) {
        return list.stream()
                .filter(x -> filter.equals(x.getIntegrationId()))
                .findFirst()
                .orElse(null);
    }

    public static void printData(String tag, int count, String levelOne) {
        log.info(tag);
        log.info("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne));
    }

    public static void printData(String tag, int count, String levelOne, String levelTwo) {
        log.info(tag);
        log.info("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo));
    }

    public static void printData(String tag, int count, String levelOne, String levelTwo, String levelThree) {
        log.info(tag);
        log.info("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree));
    }

    public static void printData(String tag, String levelOne, String levelTwo, String levelThree) {
        log.info("\u001B[37m".concat(tag));
        log.info(String.valueOf(levelOne).concat(" ")
                .concat(" ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree));
    }

    public static void printData(String tag, String levelOne) {
        log.info(tag);
        log.info("\u001B[37m(".concat(String.valueOf(levelOne)).concat(" "));
    }

    public static void printData(String tag, int count, String levelOne, String levelTwo, String levelThree, String levelFour) {
        log.info(tag);
        log.info("\u001B[37m(".concat(String.valueOf(count)).concat(") ")
                .concat(levelOne)
                .concat(" | ")
                .concat(levelTwo)
                .concat(" | ")
                .concat(levelThree)
                .concat(" | ")
                .concat(levelFour));
    }

    public static String getFieldDisplay(SysAudit sysAudit) {
        return sysAudit != null ? !sysAudit.getDocumentkey().equals("") ? sysAudit.getDocumentkey() : "SysAudit" : "SysAudit";
    }

    public static String getFieldDisplay(SysDocumentation sysDocumentation) {
        return sysDocumentation != null ? !sysDocumentation.getUniqueIdentifier().equals("") ? sysDocumentation.getUniqueIdentifier() : "SysDocumentation" : "SysDocumentation";
    }

    public static String getFieldDisplay(SysGroup group) {
        return group != null ? !group.getName().equals("") ? group.getName() : "SysGroup" : "SysGroup";
    }

    public static String getFieldDisplay(ScTask scTask) {
        return scTask != null ? !scTask.getNumber().equals("") ? scTask.getNumber() : "ScTask" : "ScTask";
    }

    public static String getFieldDisplay(TaskSla taskSla) {
        return taskSla != null ? getFieldDisplay(taskSla.getDomain()) : "TaskSlaByCompany";
    }

    public static String getFieldDisplay(Choice choice) {
        return choice != null ? !choice.getName().equals("") ? choice.getName() : "Choice" : "Choice";
    }

    public static String getFieldDisplay(ContractSla contractSla) {
        return contractSla != null ? !contractSla.getName().equals("") ? contractSla.getName() : App.ContractSla() : App.ContractSla();
    }

    public static String getFieldDisplay(CmnSchedule cmnSchedule) {
        return cmnSchedule != null ? !cmnSchedule.getName().equals("") ? cmnSchedule.getName() : App.CmnSchedule() : App.CmnSchedule();
    }

    public static String getFieldDisplay(Incident incident) {
        return incident != null ? !incident.getNumber().equals("") ? incident.getNumber() : App.Incident() : App.Incident();
    }

    public static String getFieldDisplay(Attachment attachment) {
        return attachment != null ? !attachment.getFileName().equals("") ? attachment.getFileName() : "Attachment" : "Attachment";
    }

    public static String getFieldDisplay(SysUserGroup sysUserGroup) {
        return sysUserGroup != null ? sysUserGroup.getSysGroup() != null ? isNull(sysUserGroup.getSysGroup().getName(), "SysUserGroup") : "SysUserGroup" : "SysUserGroup";
    }

    public static String getFieldDisplay(CatalogLine catalogLine) {
        return catalogLine != null ? !catalogLine.getNumber().equals("")  ? catalogLine.getNumber() : App.CatalogLine() : App.CatalogLine();
    }

    public static String getFieldDisplay(ScRequest scRequest) {
        return scRequest != null ? !scRequest.getNumber().equals("") ? scRequest.getNumber() : App.ScRequest() : App.ScRequest();
    }

    public static String getFieldDisplay(ScRequestItem scRequestItem) {
        return scRequestItem != null ? !scRequestItem.getNumber().equals("") ? scRequestItem.getNumber() : App.ScRequestItem() : App.ScRequestItem();
    }

    public static String getFieldDisplay(Company company) {
        return company != null ? !company.getName().equals("") ? company.getName() : App.Company() : App.Company();
    }

    public static String getFieldDisplay(Domain domain) {
        return domain != null ? domain.getName().equals("") ? domain.getName() : App.Domain() : App.Domain();
    }

    public static String getFieldDisplay(SysUser sysUser) {
        return sysUser != null ? !sysUser.getName().equals("") ? sysUser.getName() : App.SysUser() : App.SysUser();
    }

    private static ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static ZoneId LOCAL_ZONE = ZoneId.of("America/Santiago");

    public static LocalDateTime getLocalDateTime(String data) {
        if (!data.equals("") && data != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
            return dateTime.atZone(UTC_ZONE).withZoneSameInstant(LOCAL_ZONE).toLocalDateTime();
        }
        return null;
    }

    public static String LocalDateTimeToString(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        } else
            return "";
    }

    public static String getFieldDisplay(Location location) {
        return location != null ? !location.getName().equals("") ? location.getName() : App.Location() : App.Location();
    }

    public static String getFieldDisplay(Department department) {
        return department != null ? !department.getName().equals("") ? department.getName() : App.Department() : App.Department();
    }

    public static String getFieldDisplay(ConfigurationItem configurationItem) {
        return configurationItem != null ? !configurationItem.getName().equals("")? configurationItem.getName() : App.ConfigurationItem() : App.ConfigurationItem();
    }

    public static String getFieldDisplay(CiService ciService) {
        return ciService != null ? !ciService.getName().equals("") ? ciService.getName() : App.CiService() : App.CiService();
    }

    public static String getFieldDisplay(Journal journal) {
        return journal != null ? !journal.getElement().equals("") ? journal.getElement() : App.Journal() : App.Journal();
    }

    public static String[] offSets65000() {
        return new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
    }

    public static String[] offSets100000() {
        return new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000", "70000", "75000", "80000", "85000", "90000", "95000", "100000"};
    }

    public static String[] offSets99000() {
        return new String[]{"0", "3000", "6000", "9000", "12000", "15000", "18000", "21000", "24000", "27000", "30000", "33000", "36000", "39000", "42000", "45000", "48000", "51000", "54000", "57000", "60000", "63000", "66000", "69000", "72000", "75000", "78000", "81000", "84000", "87000", "90000", "93000", "96000", "99000"};
    }

    public static String[] offSets1500000() {
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

    public static String[] offSets7500000() {
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

    public static String[] offSets3000() {
        return new String[]{"0", "3000"};
    }

    public static String[] offSets6000() {
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

    public static String[] offSets50000() {
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

    public static String[] offSets500000() {
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

    public static String removeImg(String content) {
        Document document = Jsoup.parse(content);
        document.select("img").remove();
        return document.toString();
        //return Jsoup.parse(content).text();
    }

    private static String styleImg(String width, String height) {
        return "width: ".concat(width).concat("px !important").concat("; height: ").concat(height).concat("px !important").concat(";");
    }

    public static String reeplaceImg(String content) {
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
            log.error("Error IMG: ".concat(e.getMessage()));
            log.error("Error IMG Content: ".concat(content));
            return content;
        }
    }

    static String getDigits(String value) {
        Matcher matcher = Pattern.compile("\\d+").matcher(value);
        String digits = "";
        while (matcher.find()) {
            digits = digits.concat(matcher.group());
        }
        return digits;
    }
}
