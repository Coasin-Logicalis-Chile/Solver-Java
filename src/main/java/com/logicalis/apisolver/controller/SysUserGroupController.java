package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.SysGroup;
import com.logicalis.apisolver.model.SysUser;
import com.logicalis.apisolver.model.SysUserGroup;
import com.logicalis.apisolver.model.SysUserGroupFields;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ISysGroupService;
import com.logicalis.apisolver.services.ISysUserGroupService;
import com.logicalis.apisolver.services.ISysUserService;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.SysUserGroupRequest;
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
public class SysUserGroupController {
    @Autowired
    private ISysUserGroupService sysUserGroupService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;

    @GetMapping("/sysUserGroups")
    public List<SysUserGroup> index() {
        return sysUserGroupService.findAll();
    }

    @GetMapping("/sysUserGroup/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        SysUserGroup sysUserGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysUserGroup = sysUserGroupService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysUserGroup == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysUserGroup>(sysUserGroup, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/sysUserGroup")
    public ResponseEntity<?> create(@RequestBody SysUserGroup sysUserGroup) {
        SysUserGroup newSysUserGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newSysUserGroup = sysUserGroupService.save(sysUserGroup);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("sysUserGroup", newSysUserGroup);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/sysUserGroupSN")
    public ResponseEntity<?> update(@RequestBody SysUserGroupRequest sysUserGroupRequest) {
        SysUserGroup currentSysUserGroup = new SysUserGroup();
        SysUserGroup sysUserGroupUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentSysUserGroup == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysUserGroupRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            SysUserGroup sysUserGroup = new SysUserGroup();
            String tagAction = App.CreateConsole();
            String tag = "[SysUserGroup] ";
            sysUserGroup.setActive(sysUserGroupRequest.getActive());
            sysUserGroup.setIntegrationId(sysUserGroupRequest.getSys_id());
            if (Util.hasData(sysUserGroupRequest.getGroup())) {
                SysGroup sysGroup = sysGroupService.findByIntegrationId(sysUserGroupRequest.getGroup());
                if (sysGroup != null)
                    sysUserGroup.setSysGroup(sysGroup);
            }
            if (Util.hasData(sysUserGroupRequest.getUser())) {
                SysUser sysUser = sysUserService.findByIntegrationId(sysUserGroupRequest.getUser());
                if (sysUser != null)
                    sysUserGroup.setSysUser(sysUser);
            }
            SysUserGroup exists = sysUserGroupService.findByIntegrationId(sysUserGroup.getIntegrationId());
            if (exists != null) {
                sysUserGroup.setId(exists.getId());
            }
            sysUserGroupUpdated = sysUserGroupService.save(sysUserGroup);
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUserGroup", sysUserGroupUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/sysUserGroup/{id}")
    public ResponseEntity<?> update(@RequestBody SysUserGroup sysUserGroup, @PathVariable Long id) {
        SysUserGroup currentSysUserGroup = sysUserGroupService.findById(id);
        SysUserGroup sysUserGroupUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentSysUserGroup == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentSysUserGroup.setSysGroup(sysUserGroup.getSysGroup());
            sysUserGroupUpdated = sysUserGroupService.save(currentSysUserGroup);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUserGroup", sysUserGroupUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sysUserGroup/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            sysUserGroupService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/sysUserGroup/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        SysUserGroup sysUserGroup = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysUserGroup = sysUserGroupService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysUserGroup == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysUserGroup>(sysUserGroup, HttpStatus.OK);
    }

    @GetMapping("/findUserForGroupByFilters")
    public ResponseEntity<List<SysUserGroupFields>> findUserForGroupByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {
        List<SysUserGroupFields> sysUserGroupsFields = sysUserGroupService.findUserForGroupByFilters(company);
        ResponseEntity<List<SysUserGroupFields>> pageResponseEntity = new ResponseEntity<>(sysUserGroupsFields, HttpStatus.OK);
        return pageResponseEntity;
    }
}

