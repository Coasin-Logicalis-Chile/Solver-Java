
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
	@Autowired
	private Rest rest;

	@GetMapping("/sn_ci_services")
	public List<SnCiService> show() {
		System.out.println(App.Start());
		APIResponse apiResponse = null;
		List<SnCiService> snCiServices = new ArrayList<>();
		String[] sparmOffSets =  Util.offSets99000();
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
			startTime = System.currentTimeMillis();
			final int[] count = {1};
			for (String sparmOffSet : sparmOffSets) {
				String result = rest.responseByEndPoint(EndPointSN.CiService().concat(sparmOffSet));
				System.out.println(tag.concat("(".concat(EndPointSN.CiService().concat(sparmOffSet)).concat(")")));
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
						ciService.setName(Util.isNull(snCiService.getName()));
						ciService.setIntegrationId(Util.isNull(snCiService.getSys_id()));
						ciService.setShortDescription(Util.isNull(snCiService.getShort_description()));
						ciService.setStatus(Util.isNull(snCiService.getStatus()));
						ciService.setUpdated(Util.isNull(snCiService.getUpdated()));
						ciService.setCreatedBy(Util.isNull(snCiService.getCreated_by()));
						ciService.setAssetTag(Util.isNull(snCiService.getAsset_tag()));
						ciService.setCategory(Util.isNull(snCiService.getCategory()));
						ciService.setComments(Util.isNull(snCiService.getComments()));
						ciService.setCorrelationId(Util.isNull(snCiService.getCorrelation_id()));
						ciService.setCostCurrency(Util.isNull(snCiService.getCost_cc()));
						ciService.setCreated(Util.isNull(snCiService.getCreated()));
						ciService.setHostname(Util.isNull(snCiService.getHostname()));
						ciService.setImpact(Util.isNull(snCiService.getImpact()));
						ciService.setInstalled(Util.isNull(snCiService.getInstalled()));
						ciService.setModelNumber(Util.isNull(snCiService.getModel_number()));
						ciService.setMonitor(Util.isNull(snCiService.getMonitor()));
						ciService.setOperationalStatus(Util.isNull(snCiService.getOperational_status()));
						ciService.setSerialNumber(Util.isNull(snCiService.getSerial_number()));
						ciService.setSpecialInstruction(Util.isNull(snCiService.getSpecial_instruction()));
						ciService.setSubcategory(Util.isNull(snCiService.getSubcategory()));
						ciService.setUpdatedBy(Util.isNull(snCiService.getUpdated_by()));

						Company company = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Company.get(), App.Value()));
						if (company != null) ciService.setCompany(company);

						Location location = Util.filterLocation(locations, Util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Location.get(), App.Value()));
						if (location != null) ciService.setLocation(location);

						Company manufacturer = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snCiServiceJson, "manufacturer", App.Value()));
						if (manufacturer != null) ciService.setManufacturer(manufacturer);

						SysUser assignedTo = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snCiServiceJson, "assigned_to", App.Value()));
						if (assignedTo != null) ciService.setAssignedTo(assignedTo);

						SysUser ownedBy = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snCiServiceJson, "owned_by", App.Value()));
						if (ownedBy != null) ciService.setOwnedBy(ownedBy);

						Domain domain = Util.filterDomain(domains, Util.getIdByJson((JSONObject) snCiServiceJson, "sys_domain", App.Value()));
						if (domain != null) ciService.setDomain(domain);

						SysGroup supportGroup = Util.filterSysGroup(sysGroups, Util.getIdByJson((JSONObject) snCiServiceJson, "support_group", App.Value()));
						if (supportGroup != null) ciService.setSupportGroup(supportGroup);


						CiService exists = ciServiceService.findByIntegrationId(ciService.getIntegrationId());
						String tagAction = App.CreateConsole();
						if (exists!= null) {
							ciService.setId(exists.getId());
							tagAction = App.UpdateConsole();
						}
						ciServiceService.save(ciService);
						Util.printData(tag, count[0],  tagAction.concat(Util.getFieldDisplay(ciService)), Util.getFieldDisplay(company), Util.getFieldDisplay(domain));


						count[0] = count[0] + 1;
					} catch (Exception e) {
						System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
					}
				});

				apiResponse = mapper.readValue(result, APIResponse.class);
				APIExecutionStatus status = new APIExecutionStatus();
				status.setUri(EndPointSN.Location());
				status.setUserAPI(App.SNUser());
				status.setPasswordAPI(App.SNPassword());
				status.setError(apiResponse.getError());
				status.setMessage(apiResponse.getMessage());
				endTime = (System.currentTimeMillis() - startTime);
				status.setExecutionTime(endTime);
				statusService.save(status);
			}

		} catch (Exception e) {
			System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
		}
		System.out.println(App.End());
		return snCiServices;
	}

}
