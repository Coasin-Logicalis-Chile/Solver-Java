package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.SysUserCode;
import com.logicalis.apisolver.view.SysUserRequest;
import com.logicalis.apisolver.view.SysUserSolver;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SysUserController {
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Rest rest;

    @GetMapping("/sysUsers")
    public List<SysUser> index() {
        return sysUserService.findAll();
    }

    @GetMapping("/sys_user/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        SysUser sysUser = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysUser = sysUserService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysUser == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysUser>(sysUser, HttpStatus.OK);
    }

    @GetMapping("/sysUserByIntegrationId/{integrationId}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integrationId) {
        SysUser sysUser = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysUser = sysUserService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (Objects.isNull(sysUser)) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysUser>(sysUser, HttpStatus.OK);
    }

    @GetMapping("/sysUser/{userName}")
    public ResponseEntity<?> userName(@PathVariable String userName) {
        SysUser sysUser = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysUser = sysUserService.findByUserName(userName);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (Objects.isNull(sysUser)) {
            response.put("mensaje", Messages.notExist.get(userName));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysUser>(sysUser, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/sysUser")
    public ResponseEntity<?> create(@RequestBody SysUser sysUser) {
        SysUser newSysUser = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newSysUser = sysUserService.save(sysUser);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("sysUser", newSysUser);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/resetPassword/{id}")
    public ResponseEntity<?> update(@RequestBody JSONObject sysUserSolver, @PathVariable Long id) {
        SysUser currentSysUser = null;
        Map<String, Object> response = new HashMap<>();
        try {
            currentSysUser = sysUserService.findById(Util.parseIdJson(sysUserSolver.toString(), "params", "id"));
            if(passwordEncoder.matches(Util.parseJson(sysUserSolver.toString(), "params", "password"), currentSysUser.getPassword())){
                 response.put("mensaje", Messages.PasswordFailed.get());
                 response.put("repetida", true);
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }else{
                currentSysUser.setPassword(passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password")));
                currentSysUser = sysUserService.save(currentSysUser);
                response.put("mensaje", Messages.createOK.get());
                response.put("repetida", false);
                response.put("sysUser", currentSysUser);
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/sysUserSN")
    public ResponseEntity<?> update(@RequestBody SysUserRequest sysUserRequest) {
        SysUser currentSysUser = new SysUser();
        SysUser sysUserUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(currentSysUser)) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysUserRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            SysUser sysUser = new SysUser();
            String tagAction = App.CreateConsole();
            String tag = "[SysUser] ";
            sysUser.setActive(sysUserRequest.getActive());
            sysUser.setEmail(sysUserRequest.getEmail().toLowerCase(Locale.ROOT));
            sysUser.setEmployeeNumber(sysUserRequest.getEmployee_number());
            sysUser.setFirstName(sysUserRequest.getFirst_name());
            sysUser.setLastName(sysUserRequest.getLast_name());
            sysUser.setName(sysUserRequest.getName());
            sysUser.setUserName(sysUserRequest.getUser_name());
            sysUser.setIntegrationId(sysUserRequest.getSys_id());
            sysUser.setVip(sysUserRequest.getVip());
            sysUser.setCode(sysUserRequest.getU_solver_code());
            sysUser.setUserType(sysUserRequest.getU_user_type());
            sysUser.setSolver(sysUserRequest.getU_solver());
            String pass = passwordEncoder.encode(sysUserRequest.getU_solver_password());
            sysUser.setPassword(pass);
            Company company = companyService.findByIntegrationId(sysUserRequest.getCompany());
            sysUser.setCompany(company);
            sysUser.setDomain(company.getDomain());

            sysUser.setManager(null);
            if (Util.hasData(sysUserRequest.getManager())) {
                SysUser manager = sysUserService.findByIntegrationId(sysUserRequest.getManager());
                sysUser.setManager(manager.getIntegrationId());
            }
            sysUser.setDepartment(null);
            if (Util.hasData(sysUserRequest.getDepartment())) {
                Department department = departmentService.findByIntegrationId(sysUserRequest.getDepartment());
                if (department != null)
                    sysUser.setDepartment(department);
            }
            sysUser.setLocation(null);
            if (Util.hasData(sysUserRequest.getLocation())) {
                Location location = locationService.findByIntegrationId(sysUserRequest.getLocation());
                if (location != null)
                    sysUser.setLocation(location);
            }
            SysUser exists = sysUserService.findByIntegrationId(sysUser.getIntegrationId());
            if (!Objects.isNull(exists)) {
                sysUser.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            sysUserUpdated = sysUserService.save(sysUser);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(sysUser)), Util.getFieldDisplay(sysUser.getCompany()), Util.getFieldDisplay(sysUser.getDomain()));
        } catch (DataAccessException e) {
            log.error("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUser", sysUserUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/sysUserCode/{id}")
    public ResponseEntity<?> update(@RequestBody SysUserSolver sysUserSolver, @PathVariable Long id) {
        SysUser currentSysUser = new SysUser();
        SysUser sysUserCurrent, sysUserUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(currentSysUser)) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysUserSolver.getId().toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            sysUserCurrent = sysUserService.findById(id);
            String code = String.valueOf(new Random().nextInt(9999999));
            sysUserCurrent.setCode(code);
            sysUserUpdated = sysUserService.save(sysUserCurrent);
        } catch (DataAccessException e) {
            log.error("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUser", sysUserUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/sysUserCodeUpdate/{id}")
    public ResponseEntity<?> update(@RequestBody SysUserCode sysUserCode, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        SysUser sysUserCurrent = null;
        if (Objects.isNull(id)) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            sysUserCurrent = sysUserService.findById(id);
            rest.putSysUser(EndPointSN.PutSysUser(), sysUserCurrent);
        } catch (DataAccessException e) {
            log.error("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUser", sysUserCurrent);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/sysUser/{id}")
    public ResponseEntity<?> update(@RequestBody SysUser sysUser, @PathVariable Long id) {
        SysUser currentSysUser = sysUserService.findById(id);
        SysUser sysUserUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(currentSysUser)) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentSysUser.setName(sysUser.getName());
            sysUserUpdated = sysUserService.save(currentSysUser);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysUser", sysUserUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sysUser/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            sysUserService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/sysUsersByMySysGroups")
    public ResponseEntity<List<SysUserFields>> findSysUsersByMySysGroups(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                         @NotNull @RequestParam(value = "sysUser", required = true, defaultValue = "0") Long sysUser) {
        List<SysUserFields> sysUsers = sysUserService.findSysUsersByMySysGroups(company, sysUser);
        ResponseEntity<List<SysUserFields>> pageResponseEntity = new ResponseEntity<>(sysUsers, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/sysUserByEmailAndSolverAndCode")
    public ResponseEntity<SysUser> getCodeByEmailAndSolver(@NotNull @RequestParam(value = "email", required = true, defaultValue = "0") String email,
                                                           @NotNull @RequestParam(value = "solver", required = true, defaultValue = "true") boolean solver,
                                                           @NotNull @RequestParam(value = "code", required = true, defaultValue = "") String code) {
        log.info(code);
        SysUser sysUser = sysUserService.findByEmailAndSolver(email, solver);
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        SysUser currentRecord = null;
        boolean flag = code.equals(sysUser.getCode()); //bcrypt.matches(code, sysUser.getCode());
        if (flag) {
            currentRecord = new SysUser();
            currentRecord.setId(sysUser.getId());
            currentRecord.setEmail(sysUser.getEmail());
            currentRecord.setLocked(sysUser.getLocked());
            currentRecord.setCode(sysUser.getCode());
            currentRecord.setIntegrationId(sysUser.getIntegrationId());
        }
        ResponseEntity<SysUser> pageResponseEntity = new ResponseEntity<>(currentRecord, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/sysUserByEmailAndSolver")
    public ResponseEntity<SysUser> findByEmailAndSolver(@NotNull @RequestParam(value = "email", required = true, defaultValue = "0") String email,
                                                        @NotNull @RequestParam(value = "solver", required = true, defaultValue = "true") boolean solver) throws Exception {
        SysUser sysUser = sysUserService.findByEmailAndSolver(email, solver);
        ResponseEntity<SysUser> pageResponseEntity = new ResponseEntity<>(sysUser, HttpStatus.OK);
        return pageResponseEntity;
    }

    public void save(SysUser sysUser) throws Exception {
        sysUserService.save(sysUser);
        this.update(sysUser, sysUser.getId());
    }

    @GetMapping("/findUserGroupsByFilters")
    public ResponseEntity<List<SysUserFields>> findUserGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {
        List<SysUserFields> sysUserFields = sysUserService.findUserGroupsByFilters(company);
        ResponseEntity<List<SysUserFields>> pageResponseEntity = new ResponseEntity<>(sysUserFields, HttpStatus.OK);
        return pageResponseEntity;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}
