package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnCiService;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SnCiServiceController {
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
		log.info(App.Start());
		APIResponse apiResponse = null;
		List<SnCiService> snCiServices = new ArrayList<>();
		String[] sparmOffSets =  Util.offSets99000();
		long startTime = 0;
		long endTime = 0;
		String tag = "[CiService] ";
		try {
			List<Domain> domains = domainService.findAll();
			log.info(tag.concat("(Get All Domains)"));
			List<Company> companies = companyService.findAll();
			log.info(tag.concat("(Get All Companies)"));
			List<Location> locations = locationService.findAll();
			log.info(tag.concat("(Get All Locations)"));
			List<SysUser> sysUsers = sysUserService.findAll();
			log.info(tag.concat("(Get All Sys Users)"));
			List<SysGroup> sysGroups = sysGroupService.findAll();
			log.info(tag.concat("(Get All Sys Groups)"));
			startTime = System.currentTimeMillis();
			final int[] count = {1};
			String result;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JSONParser parser = new JSONParser();
			JSONObject resultJson = new JSONObject();
			JSONArray ListSnCiServiceJson = new JSONArray();
			final SnCiService[] snCiService = {new SnCiService()};
			final CiService[] ciService = {new CiService()};
			final Company[] company = new Company[1];
			final Location[] location = new Location[1];
			final Company[] manufacturer = new Company[1];
			final SysUser[] assignedTo = new SysUser[1];
			final SysUser[] ownedBy = new SysUser[1];
			final Domain[] domain = new Domain[1];
			final SysGroup[] supportGroup = new SysGroup[1];
			final CiService[] exists = new CiService[1];
			final String[] tagAction = new String[1];
			APIExecutionStatus status = new APIExecutionStatus();;
			for (String sparmOffSet : sparmOffSets) {
				result = rest.responseByEndPoint(EndPointSN.CiService().concat(sparmOffSet));
				log.info(tag.concat("(".concat(EndPointSN.CiService().concat(sparmOffSet)).concat(")")));
				resultJson = (JSONObject) parser.parse(result);
				ListSnCiServiceJson.clear();
				if (resultJson.get("result") != null)
					ListSnCiServiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

				ListSnCiServiceJson.stream().forEach(snCiServiceJson -> {

					snCiService[0] = new SnCiService();
					try {
						snCiService[0] = mapper.readValue(snCiServiceJson.toString(), SnCiService.class);
						snCiServices.add(snCiService[0]);
						ciService[0] = new CiService();
						ciService[0].setName(Util.isNull(snCiService[0].getName()));
						ciService[0].setIntegrationId(Util.isNull(snCiService[0].getSys_id()));
						ciService[0].setShortDescription(Util.isNull(snCiService[0].getShort_description()));
						ciService[0].setStatus(Util.isNull(snCiService[0].getStatus()));
						ciService[0].setUpdated(Util.isNull(snCiService[0].getUpdated()));
						ciService[0].setCreatedBy(Util.isNull(snCiService[0].getCreated_by()));
						ciService[0].setAssetTag(Util.isNull(snCiService[0].getAsset_tag()));
						ciService[0].setCategory(Util.isNull(snCiService[0].getCategory()));
						ciService[0].setComments(Util.isNull(snCiService[0].getComments()));
						ciService[0].setCorrelationId(Util.isNull(snCiService[0].getCorrelation_id()));
						ciService[0].setCostCurrency(Util.isNull(snCiService[0].getCost_cc()));
						ciService[0].setCreated(Util.isNull(snCiService[0].getCreated()));
						ciService[0].setHostname(Util.isNull(snCiService[0].getHostname()));
						ciService[0].setImpact(Util.isNull(snCiService[0].getImpact()));
						ciService[0].setInstalled(Util.isNull(snCiService[0].getInstalled()));
						ciService[0].setModelNumber(Util.isNull(snCiService[0].getModel_number()));
						ciService[0].setMonitor(Util.isNull(snCiService[0].getMonitor()));
						ciService[0].setOperationalStatus(Util.isNull(snCiService[0].getOperational_status()));
						ciService[0].setSerialNumber(Util.isNull(snCiService[0].getSerial_number()));
						ciService[0].setSpecialInstruction(Util.isNull(snCiService[0].getSpecial_instruction()));
						ciService[0].setSubcategory(Util.isNull(snCiService[0].getSubcategory()));
						ciService[0].setUpdatedBy(Util.isNull(snCiService[0].getUpdated_by()));

						company[0] = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Company.get(), App.Value()));
						if (company[0] != null) ciService[0].setCompany(company[0]);

						location[0] = Util.filterLocation(locations, Util.getIdByJson((JSONObject) snCiServiceJson, SnTable.Location.get(), App.Value()));
						if (location[0] != null) ciService[0].setLocation(location[0]);

						manufacturer[0] = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snCiServiceJson, "manufacturer", App.Value()));
						if (manufacturer[0] != null) ciService[0].setManufacturer(manufacturer[0]);

						assignedTo[0] = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snCiServiceJson, "assigned_to", App.Value()));
						if (assignedTo[0] != null) ciService[0].setAssignedTo(assignedTo[0]);

						ownedBy[0] = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snCiServiceJson, "owned_by", App.Value()));
						if (ownedBy[0] != null) ciService[0].setOwnedBy(ownedBy[0]);

						domain[0] = Util.filterDomain(domains, Util.getIdByJson((JSONObject) snCiServiceJson, "sys_domain", App.Value()));
						if (domain[0] != null) ciService[0].setDomain(domain[0]);

						supportGroup[0] = Util.filterSysGroup(sysGroups, Util.getIdByJson((JSONObject) snCiServiceJson, "support_group", App.Value()));
						if (supportGroup[0] != null) ciService[0].setSupportGroup(supportGroup[0]);

						exists[0] = ciServiceService.findByIntegrationId(ciService[0].getIntegrationId());
						tagAction[0] = App.CreateConsole();
						if (exists[0] != null) {
							ciService[0].setId(exists[0].getId());
							tagAction[0] = App.UpdateConsole();
						}
						ciServiceService.save(ciService[0]);
						Util.printData(tag, count[0],  tagAction[0].concat(Util.getFieldDisplay(ciService[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
						count[0] = count[0] + 1;
					} catch (Exception e) {
						log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
					}
				});
				apiResponse = mapper.readValue(result, APIResponse.class);
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
			log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
		}
		log.info(App.End());
		return snCiServices;
	}
}
