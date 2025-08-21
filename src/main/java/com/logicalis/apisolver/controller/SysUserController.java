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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.logicalis.apisolver.model.UserType;
import com.logicalis.apisolver.services.IUserTypeService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    @Autowired
    private  IUserTypeService userTypeService;

    // Cached list to fallback in case of database issues
    private static List<SysUser> cachedSysUsers = new ArrayList<>();
    private static long lastSysUsersCacheTime = 0;
    private static final long CACHE_VALIDITY_MS = 5 * 60 * 1000; // 5 minutes
    
    @GetMapping("/sysUsers")
    public ResponseEntity<?> index() {
        try {
            List<SysUser> sysUsers = sysUserService.findAll();
            
            // Update cache if we got valid data
            if (sysUsers != null && !sysUsers.isEmpty()) {
                cachedSysUsers = new ArrayList<>(sysUsers);
                lastSysUsersCacheTime = System.currentTimeMillis();
                log.debug("SysUsers cache updated with {} users", sysUsers.size());
            }
            
            return ResponseEntity.ok(sysUsers);
            
        } catch (Exception e) {
            log.error("Error fetching sysUsers, attempting to return cached data: {}", e.getMessage());
            
            // Return cached data if available and not too old
            if (!cachedSysUsers.isEmpty() && 
                (System.currentTimeMillis() - lastSysUsersCacheTime) < CACHE_VALIDITY_MS) {
                log.warn("Returning cached sysUsers data due to database error");
                return ResponseEntity.ok(cachedSysUsers);
            }
            
            // If no cache or cache too old, return error with empty fallback
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error loading users, please try again");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("fallback", new ArrayList<>());
            
            log.error("No valid cache available for sysUsers, returning empty list");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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

            if (currentSysUser.getCompany().getPasswordExpiration()){
                String[] partes = currentSysUser.getPassword().split("\\$2a\\$10\\$");
                int cantidad = partes.length - 1;

                // Validación de que no se repita las contraseñas
                for (String parte : partes) {
                    if (!parte.isEmpty()) {
                        String passwordreset="$2a$10$"+parte;
                        if(passwordEncoder.matches(Util.parseJson(sysUserSolver.toString(), "params", "password"), passwordreset)){
                            log.info("Password repetida, Response Fallido");
                            response.put("mensaje", Messages.PasswordFailed.get());
                            response.put("repetida", true);
                            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
                        }
                    }
                }
                String password;
                if (cantidad < 3){
                    password = currentSysUser.getPassword().concat(passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password")));
                }else{
                    password = "$2a$10$"+partes[cantidad-1]+"$2a$10$"+partes[cantidad] + passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password"));
                }

                currentSysUser.setPassword(password);

                LocalDateTime fechaActual = LocalDateTime.now();
                Date date = Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant());
                currentSysUser.setdateUpdatePassword(date);

                currentSysUser = sysUserService.save(currentSysUser);
                response.put("mensaje", Messages.createOK.get());
                response.put("repetida", false);
                response.put("sysUser", currentSysUser);
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

            }else{

                String[] partes = currentSysUser.getPassword().split("\\$2a\\$10\\$");
                int cantidad = partes.length - 1;
                String lastPassword = "$2a$10$"+partes[cantidad];

                if(passwordEncoder.matches(Util.parseJson(sysUserSolver.toString(), "params", "password"), lastPassword)){
                    log.info("Password Actual, insertar una contraseña diferente");
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
        List<UserType> allUserType =userTypeService.findAll();
        boolean condicionCumplidaTipoUsuario = false;

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
            log.info("Estableciendo nuevos atributos al Objeto sysUser: {}", sysUserRequest.getSys_id());
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

            for (UserType userT : allUserType){

                if (sysUserRequest.getU_user_type() == null){
                    sysUser.setUserType(0L);
                    condicionCumplidaTipoUsuario = true;
                    break;
                }

                if (userT.getName().toLowerCase().equals(sysUserRequest.getU_user_type().toLowerCase())){
                    sysUser.setUserType(userT.getId());
                    condicionCumplidaTipoUsuario = true;
                    break;
                }
            }
            // En caso no coincida con ninguno de los tipos de variables que estan en la tabla se asignara "0" - No asignado
            if (!condicionCumplidaTipoUsuario) {
                sysUser.setUserType(0L);
            }

            sysUser.setSolver(sysUserRequest.getU_solver());
            String pass = passwordEncoder.encode(sysUserRequest.getU_solver_password());
            sysUser.setPassword(pass);
            Company company = companyService.findByIntegrationId(sysUserRequest.getCompany());
            sysUser.setCompany(company);
            sysUser.setDomain(company.getDomain());

            sysUser.setManager(null);
            if (Util.hasData(sysUserRequest.getManager())) {
                SysUser manager = sysUserService.findByIntegrationId(sysUserRequest.getManager());
                if (manager != null){
                    sysUser.setManager(manager.getIntegrationId());
                }else{
                    log.info("Variable manager is null");
                }
            }
            sysUser.setDepartment(null);
            if (Util.hasData(sysUserRequest.getDepartment())) {
                Department department = departmentService.findByIntegrationId(sysUserRequest.getDepartment());
                if (department != null){
                    sysUser.setDepartment(department);
                }
            }
            sysUser.setLocation(null);
            if (Util.hasData(sysUserRequest.getLocation())) {
                Location location = locationService.findByIntegrationId(sysUserRequest.getLocation());
                if (location != null){
                    sysUser.setLocation(location);
                }
            }
            SysUser exists = sysUserService.findByIntegrationId(sysUser.getIntegrationId());
            if (!Objects.isNull(exists)) {
                sysUser.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            log.info("Guardando Usuario en la Base de Datos: {}", sysUser.getIntegrationId());
            sysUserUpdated = sysUserService.save(sysUser);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(sysUser)), Util.getFieldDisplay(sysUser.getCompany()), Util.getFieldDisplay(sysUser.getDomain()));
            log.info("Usuario Actualizado correctamente: {}", sysUser.getIntegrationId());
        } catch (DataAccessException e) {
            log.error("Error al actualizar Usuario: {}", e.getMessage());
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
            response.put("mensaje", "Error: El registro con el id: no existe en la base de datos.");
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
        log.info("sysUserByEmailAndSolverAndCode");
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
        log.info("sysUserByEmailAndSolver");
        SysUser sysUser = sysUserService.findByEmailAndSolver(email, solver);
        ResponseEntity<SysUser> pageResponseEntity = new ResponseEntity<>(sysUser, HttpStatus.OK);
        return pageResponseEntity;
    }

    public void save(SysUser sysUser) throws Exception {
        sysUserService.save(sysUser);
        this.update(sysUser, sysUser.getId());
    }

    // Cache for user group filters by company  
    private static Map<Long, List<SysUserFields>> cachedUserGroupsByCompany = new HashMap<>();
    private static Map<Long, Long> userGroupsCacheTime = new HashMap<>();
    
    @GetMapping("/findUserGroupsByFilters")
    public ResponseEntity<?> findUserGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {
        
        try {
            List<SysUserFields> sysUserFields = sysUserService.findUserGroupsByFilters(company);
            
            // Update cache if we got valid data
            if (sysUserFields != null && !sysUserFields.isEmpty()) {
                cachedUserGroupsByCompany.put(company, new ArrayList<>(sysUserFields));
                userGroupsCacheTime.put(company, System.currentTimeMillis());
                log.debug("UserGroups cache updated for company {} with {} users", company, sysUserFields.size());
            }
            
            return ResponseEntity.ok(sysUserFields);
            
        } catch (Exception e) {
            log.error("Error fetching user groups for company {}, attempting to return cached data: {}", company, e.getMessage());
            
            // Check if we have cached data for this company
            List<SysUserFields> cachedData = cachedUserGroupsByCompany.get(company);
            Long cacheTime = userGroupsCacheTime.get(company);
            
            if (cachedData != null && !cachedData.isEmpty() && cacheTime != null &&
                (System.currentTimeMillis() - cacheTime) < CACHE_VALIDITY_MS) {
                log.warn("Returning cached user groups data for company {} due to database error", company);
                return ResponseEntity.ok(cachedData);
            }
            
            // Try to get cached data for ANY company as fallback
            if (cachedUserGroupsByCompany.isEmpty()) {
                log.error("No cached user groups data available for company {}, returning empty list", company);
            } else {
                log.warn("Attempting to provide fallback data from other companies");
                // Return the most recent cache from any company as last resort
                Optional<Map.Entry<Long, List<SysUserFields>>> mostRecentEntry = 
                    cachedUserGroupsByCompany.entrySet().stream()
                        .filter(entry -> userGroupsCacheTime.get(entry.getKey()) != null)
                        .max(Map.Entry.comparingByValue((a, b) -> 
                            Long.compare(userGroupsCacheTime.get(b), userGroupsCacheTime.get(a))));
                            
                if (mostRecentEntry.isPresent()) {
                    List<SysUserFields> fallbackData = mostRecentEntry.get().getValue();
                    Long fallbackCompany = mostRecentEntry.get().getKey();
                    log.warn("Returning fallback user groups data from company {} for requested company {}", 
                            fallbackCompany, company);
                    return ResponseEntity.ok(fallbackData);
                }
            }
            
            // If no cache available, return error with empty fallback
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error loading user groups, please try again");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("fallback", new ArrayList<>());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }
    
    // Cache warming endpoint for system monitoring and maintenance
    @GetMapping("/cache/warm-user-data")
    public ResponseEntity<?> warmUserDataCache() {
        Map<String, Object> result = new HashMap<>();
        int warmedCaches = 0;
        
        try {
            // Warm up sysUsers cache
            List<SysUser> sysUsers = sysUserService.findAll();
            if (sysUsers != null && !sysUsers.isEmpty()) {
                cachedSysUsers = new ArrayList<>(sysUsers);
                lastSysUsersCacheTime = System.currentTimeMillis();
                warmedCaches++;
                log.info("Warmed sysUsers cache with {} records", sysUsers.size());
            }
            
            // Attempt to warm user group caches for known companies
            // This is a basic approach - in production you might want to get active company IDs first
            List<Long> commonCompanyIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
            
            for (Long companyId : commonCompanyIds) {
                try {
                    List<SysUserFields> userGroups = sysUserService.findUserGroupsByFilters(companyId);
                    if (userGroups != null && !userGroups.isEmpty()) {
                        cachedUserGroupsByCompany.put(companyId, new ArrayList<>(userGroups));
                        userGroupsCacheTime.put(companyId, System.currentTimeMillis());
                        warmedCaches++;
                        log.debug("Warmed user groups cache for company {} with {} records", companyId, userGroups.size());
                    }
                } catch (Exception e) {
                    log.debug("Could not warm cache for company {}: {}", companyId, e.getMessage());
                }
            }
            
            result.put("mensaje", "Cache warming completed successfully");
            result.put("warmedCaches", warmedCaches);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error during cache warming: {}", e.getMessage());
            result.put("mensaje", "Cache warming partially failed");
            result.put("error", e.getMessage());
            result.put("warmedCaches", warmedCaches);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    // Cache status endpoint for monitoring
    @GetMapping("/cache/status")
    public ResponseEntity<?> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("sysUsersCache", Map.of(
            "size", cachedSysUsers.size(),
            "lastUpdated", lastSysUsersCacheTime,
            "ageMs", System.currentTimeMillis() - lastSysUsersCacheTime,
            "valid", (System.currentTimeMillis() - lastSysUsersCacheTime) < CACHE_VALIDITY_MS
        ));
        
        Map<String, Object> userGroupsCacheStatus = new HashMap<>();
        for (Map.Entry<Long, List<SysUserFields>> entry : cachedUserGroupsByCompany.entrySet()) {
            Long companyId = entry.getKey();
            Long cacheTime = userGroupsCacheTime.get(companyId);
            userGroupsCacheStatus.put("company_" + companyId, Map.of(
                "size", entry.getValue().size(),
                "lastUpdated", cacheTime != null ? cacheTime : 0,
                "ageMs", cacheTime != null ? System.currentTimeMillis() - cacheTime : -1,
                "valid", cacheTime != null && (System.currentTimeMillis() - cacheTime) < CACHE_VALIDITY_MS
            ));
        }
        
        status.put("userGroupsCache", userGroupsCacheStatus);
        status.put("cacheValidityMs", CACHE_VALIDITY_MS);
        status.put("currentTime", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

}
