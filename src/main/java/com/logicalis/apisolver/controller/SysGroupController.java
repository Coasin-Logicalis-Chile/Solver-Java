package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Company;
import com.logicalis.apisolver.model.SysGroup;
import com.logicalis.apisolver.model.SysGroupFields;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ICompanyService;
import com.logicalis.apisolver.services.ISysGroupService;
import com.logicalis.apisolver.view.SysGroupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SysGroupController {
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private ICompanyService companyService;

    @GetMapping("/sysGroups")
    public List<SysGroup> index() {
        return sysGroupService.findAll();
    }

    @GetMapping("/sysGroupByByActiveAndName/{name}")
    public ResponseEntity<?> findByActiveAndName(@PathVariable String name) {
        SysGroup sysGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysGroup = sysGroupService.findByActiveAndName(true, name);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysGroup == null) {
            response.put("mensaje", Messages.notExist.get(name));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysGroup>(sysGroup, HttpStatus.OK);
    }

    @GetMapping("/sysGroup/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        SysGroup sysGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysGroup = sysGroupService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysGroup == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysGroup>(sysGroup, HttpStatus.OK);
    }

    @GetMapping("/sysGroup/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        SysGroup sysGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysGroup = sysGroupService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysGroup == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysGroup>(sysGroup, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/sysGroup")
    public ResponseEntity<?> create(@RequestBody SysGroup sysGroup) {
        SysGroup newSysGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newSysGroup = sysGroupService.save(sysGroup);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("sysGroup", newSysGroup);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/sysGroup/{id}")
    public ResponseEntity<?> update(@RequestBody SysGroup sysGroup, @PathVariable Long id) {
        SysGroup currentSysGroup = sysGroupService.findById(id);
        SysGroup sysGroupUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentSysGroup == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentSysGroup.setName(sysGroup.getName());
            sysGroupUpdated = sysGroupService.save(currentSysGroup);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysGroup", sysGroupUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sysGroup/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            sysGroupService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }


    @GetMapping("/findSysGroupsByFilters")
    public ResponseEntity<List<SysGroupFields>> findSysGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {
        List<SysGroupFields> sysGroups = sysGroupService.findSysGroupsByFilters(company);
        ResponseEntity<List<SysGroupFields>> pageResponseEntity = new ResponseEntity<>(sysGroups, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/mySysGroups")
    public ResponseEntity<List<SysGroupFields>> findSysGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                       @NotNull @RequestParam(value = "sysUser", required = true, defaultValue = "0") Long sysUser) {
        List<SysGroupFields> sysGroups = sysGroupService.findSysGroupsByFilters(company, sysUser);
        ResponseEntity<List<SysGroupFields>> pageResponseEntity = new ResponseEntity<>(sysGroups, HttpStatus.OK);
        return pageResponseEntity;
    }

    @PutMapping("/sysGroupSN")
    public ResponseEntity<?> update(@RequestBody SysGroupRequest sysGroupRequest) {
        SysGroup currentSysGroup = new SysGroup();
        SysGroup sysGroupUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentSysGroup == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysGroupRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            SysGroup sysGroup = new SysGroup();
            String tagAction = App.CreateConsole();
            String tag = "[SysGroup] ";
            sysGroup.setActive(sysGroupRequest.getActive());
            sysGroup.setName(sysGroupRequest.getName());
            sysGroup.setIntegrationId(sysGroupRequest.getSys_id());
            sysGroup.setSolver(sysGroupRequest.getU_solver());
            Company company = companyService.findByIntegrationId(sysGroupRequest.getCompany());
            sysGroup.setCompany(company);
            sysGroup.setDomain(company.getDomain());
            SysGroup exists = sysGroupService.findByIntegrationId(sysGroup.getIntegrationId());
            if (exists != null) {
                sysGroup.setId(exists.getId());
            }
            sysGroupUpdated = sysGroupService.save(sysGroup);
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysGroup", sysGroupUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}

