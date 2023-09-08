
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnCiService;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnCiServiceService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SnCiServiceController {

	@Autowired
	private ISnCiServiceService snCiServiceService;
	@Autowired
	private ICiServiceService ciServiceService;
	@Autowired
	private IDomainService domainService;
	@Autowired
	private ILocationService locationService;
	@Autowired
	private IAPIExecutionStatusService statusService;
	@Autowired
	private ICompanyService companyService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysGroupService sysGroupService;

	private Util util = new Util();
	App app = new App();
	EndPointSN endPointSN = new EndPointSN();
	@GetMapping("/sn_ci_services")
	public List<SnCiService> show() {
		System.out.println(app.Start());
		APIResponse apiResponse = null;
		List<SnCiService> snCiServices = new ArrayList<>();
		String[] sparmOffSets =  util.offSets99000();
		Util util = new Util();
		long startTime = 0;
		long endTime = 0;
		String tag = "[CiService] ";
		try {
			List<Domain> domains = domainService.findAll();
			System.out.println(tag.concat("(Get All Domains)"));
			List<Company> companies = companyService.findAll();
			System.out.println(tag.concat("(Get All Companies)"));
			List<Location> locations = locationService.findAll();
			System.out.println(tag.concat("(Get All Locations)"));
			List<SysUser> sysUsers = sysUserService.findAll();
			System.out.println(tag.concat("(Get All Sys Users)"));
			List<SysGroup> sysGroups = sysGroupService.findAll();
			System.out.println(tag.concat("(Get All Sys Groups)"));
			Rest rest = new Rest();
			startTime = System.currentTimeMillis();
			final int[] count = {1};
			for (String sparmOffSet : sparmOffSets) {
				String result = rest.responseByEndPoint(endPointSN.CiService().concat(sparmOffSet));
				System.out.println(tag.concat("(".concat(endPointSN.CiService().concat(sparmOffSet)).concat(")")));
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JSONParser parser = new JSONParser();
				JSONObject resultJson = new JSONObject();
				JSONArray ListSnCiServiceJson = new JSONArray();

				resultJson = (JSONObject) parser.parse(result);
				if (resultJson.get("result") != null)
					ListSnCiServiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

				ListSnCiServiceJson.stream().forEach(snCiServiceJson -> {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					SnCiService snCiService = new SnCiService();
					try {
						snCiService = objectMapper.readValue(snCiServiceJson.toString(), SnCiService.class);
						snCiServices.add(snCiService);
						CiService ciService = new CiService();
						ciService.setName(util.isNull(snCiService.getName()));
						ciService.setIntegrationId(util.isNull(snCiService.getSys_id()));
						ciService.setShortDescription(util.isNull(snCiService.getShort_description()));
						ciService.setStatus(util.isNull(snCiService.getStatus()));
						ciService.setUpdated(util.isNull(snCiService.getUpdated()));
						ciService.setCreatedBy(util.isNull(snCiService.getCreated_by()));
						ciService.setAssetTag(util.isNull(snCiService.getAsset_tag()));
						ciService.setCategory(util.isNull(snCiService.getCategory()));
						ciService.setComments(util.isNull(snCiService.getComments()));
						ciService.setCorrelationId(util.isNull(snCiService.getCorrelation_id()));
						ciService.setCostCurrency(util.isNull(snCiService.getCost_cc()));
						ciService.setCreated(util.isNull(snCiService.getCreated()));
						ciService.setHostname(util.isNull(snCiService.getHostname()));
						ciService.setImpact(util.isNull(snCiService.getImpact()));
						ciService.setInstalled(util.isNull(snCiService.getInstalled()));
						ciService.setModelNumber(util.isNull(snCiService.getModel_number()));
						ciService.setMonitor(util.isNull(snCiService.getMonitor()));
						ciService.setOperationalStatus(util.isNull(snCiService.getOperational_status()));
						ciService.setSerialNumber(util.isNull(snCiService.getSerial_number()));
						ciService.setSpecialInstruction(util.isNull(snCiService.getSpecial_instruction()));
						ciService.setSubcategory(util.isNull(snCiService.getSubcategory()));
						ciService.setUpdatedBy(util.isNull(snCiService.getUpdated_by()));

						Company company = util.filterCompany(companies, util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Company.get(), app.Value()));
						if (company != null) ciService.setCompany(company);

						Location location = util.filterLocation(locations, util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Location.get(), app.Value()));
						if (location != null) ciService.setLocation(location);

						Company manufacturer = util.filterCompany(companies, util.getIdByJson((JSONObject) snCiServiceJson, "manufacturer", app.Value()));
						if (manufacturer != null) ciService.setManufacturer(manufacturer);

						SysUser assignedTo = util.filterSysUser(sysUsers, util.getIdByJson((JSONObject) snCiServiceJson, "assigned_to", app.Value()));
						if (assignedTo != null) ciService.setAssignedTo(assignedTo);

						SysUser ownedBy = util.filterSysUser(sysUsers, util.getIdByJson((JSONObject) snCiServiceJson, "owned_by", app.Value()));
						if (ownedBy != null) ciService.setOwnedBy(ownedBy);

						Domain domain = util.filterDomain(domains, util.getIdByJson((JSONObject) snCiServiceJson, "sys_domain", app.Value()));
						if (domain != null) ciService.setDomain(domain);

						SysGroup supportGroup = util.filterSysGroup(sysGroups, util.getIdByJson((JSONObject) snCiServiceJson, "support_group", app.Value()));
						if (supportGroup != null) ciService.setSupportGroup(supportGroup);


						CiService exists = ciServiceService.findByIntegrationId(ciService.getIntegrationId());
						String tagAction = app.CreateConsole();
						if (exists!= null) {
							ciService.setId(exists.getId());
							tagAction = app.UpdateConsole();
						}
						ciServiceService.save(ciService);
						util.printData(tag, count[0],  tagAction.concat(util.getFieldDisplay(ciService)), util.getFieldDisplay(company), util.getFieldDisplay(domain));


						count[0] = count[0] + 1;
					} catch (Exception e) {
						System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
					}
				});

				apiResponse = mapper.readValue(result, APIResponse.class);
				APIExecutionStatus status = new APIExecutionStatus();
				status.setUri(endPointSN.Location());
				status.setUserAPI(app.SNUser());
				status.setPasswordAPI(app.SNPassword());
				status.setError(apiResponse.getError());
				status.setMessage(apiResponse.getMessage());
				endTime = (System.currentTimeMillis() - startTime);
				status.setExecutionTime(endTime);
				statusService.save(status);
			}

		} catch (Exception e) {
			System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
		}
		System.out.println(app.End());
		return snCiServices;
	}

}
