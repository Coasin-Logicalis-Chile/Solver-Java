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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@CrossOrigin(origins = { "${app.api.settings.cross-origin.urls}", "*" })
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
    private IUserTypeService userTypeService;

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

            if (currentSysUser.getCompany().getPasswordExpiration()) {
                String[] partes = currentSysUser.getPassword().split("\\$2a\\$10\\$");
                int cantidad = partes.length - 1;

                // Validación de que no se repita las contraseñas
                for (String parte : partes) {
                    if (!parte.isEmpty()) {
                        String passwordreset = "$2a$10$" + parte;
                        if (passwordEncoder.matches(Util.parseJson(sysUserSolver.toString(), "params", "password"),
                                passwordreset)) {
                            log.info("Password repetida, Response Fallido");
                            response.put("mensaje", Messages.PasswordFailed.get());
                            response.put("repetida", true);
                            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
                        }
                    }
                }
                String password;
                if (cantidad < 3) {
                    password = currentSysUser.getPassword().concat(
                            passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password")));
                } else {
                    password = "$2a$10$" + partes[cantidad - 1] + "$2a$10$" + partes[cantidad]
                            + passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password"));
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

            } else {

                String[] partes = currentSysUser.getPassword().split("\\$2a\\$10\\$");
                int cantidad = partes.length - 1;
                String lastPassword = "$2a$10$" + partes[cantidad];

                if (passwordEncoder.matches(Util.parseJson(sysUserSolver.toString(), "params", "password"),
                        lastPassword)) {
                    log.info("Password Actual, insertar una contraseña diferente");
                    response.put("mensaje", Messages.PasswordFailed.get());
                    response.put("repetida", true);
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
                } else {
                    currentSysUser.setPassword(
                            passwordEncoder.encode(Util.parseJson(sysUserSolver.toString(), "params", "password")));
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
        List<UserType> allUserType = userTypeService.findAll();
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

            for (UserType userT : allUserType) {

                if (sysUserRequest.getU_user_type() == null) {
                    sysUser.setUserType(0L);
                    condicionCumplidaTipoUsuario = true;
                    break;
                }

                if (userT.getName().toLowerCase().equals(sysUserRequest.getU_user_type().toLowerCase())) {
                    sysUser.setUserType(userT.getId());
                    condicionCumplidaTipoUsuario = true;
                    break;
                }
            }
            // En caso no coincida con ninguno de los tipos de variables que estan en la
            // tabla se asignara "0" - No asignado
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
                if (manager != null) {
                    sysUser.setManager(manager.getIntegrationId());
                } else {
                    log.info("Variable manager is null");
                }
            }
            sysUser.setDepartment(null);
            if (Util.hasData(sysUserRequest.getDepartment())) {
                Department department = departmentService.findByIntegrationId(sysUserRequest.getDepartment());
                if (department != null) {
                    sysUser.setDepartment(department);
                }
            }
            sysUser.setLocation(null);
            if (Util.hasData(sysUserRequest.getLocation())) {
                Location location = locationService.findByIntegrationId(sysUserRequest.getLocation());
                if (location != null) {
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
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(sysUser)),
                    Util.getFieldDisplay(sysUser.getCompany()), Util.getFieldDisplay(sysUser.getDomain()));
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
    Map<String, Object> response = new HashMap<>();
    SysUser sysUserUpdated;

    try {
        SysUser sysUserCurrent = sysUserService.findById(id);
        if (sysUserCurrent == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(String.valueOf(id)));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        sysUserCurrent.setCode(code);
        sysUserUpdated = sysUserService.save(sysUserCurrent);

    } catch (DataAccessException e) {
        log.error("error " + e.getMessage());
        response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Map<String, Object> body = new HashMap<>();
    Map<String, Object> sysUserMap = new HashMap<>();
    sysUserMap.put("id", sysUserUpdated.getId());
    body.put("sysUser", sysUserMap);

    return ResponseEntity.ok(body);
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

    @GetMapping("/sysUsersByMySysGroups")
    public ResponseEntity<List<SysUserFields>> findSysUsersByMySysGroups(
            @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
            @NotNull @RequestParam(value = "sysUser", required = true, defaultValue = "0") Long sysUser) {

        // ================== LECTURA DE CLAIMS DESDE AUTH.DETAILS ==================
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (auth != null && auth.getDetails() instanceof Map)
                ? (Map<String, Object>) auth.getDetails()
                : java.util.Collections.emptyMap();

        Long companyIdFromToken = null;
        Object companyObj = claims.get("company");

        if (companyObj != null) {
            // Caso A: viene como POJO Company (lo que muestra tu depurador)
            try {
                // Si tienes la clase a mano, mejor casteo directo:
                // companyIdFromToken = ((Company) companyObj).getId();
                // Si no, usa reflexión para no acoplarte:
                java.lang.reflect.Method m = companyObj.getClass().getMethod("getId");
                Object idObj = m.invoke(companyObj);
                if (idObj != null) {
                    companyIdFromToken = Long.valueOf(idObj.toString());
                }
            } catch (NoSuchMethodException ignored) {
                // Caso B: a veces podría venir como Map (JSON)
                if (companyObj instanceof Map) {
                    Object id = ((Map<?, ?>) companyObj).get("id");
                    if (id != null) {
                        try {
                            companyIdFromToken = Long.valueOf(id.toString());
                        } catch (NumberFormatException ignored2) {
                        }
                    }
                } else {
                    try {
                        companyIdFromToken = Long.valueOf(companyObj.toString());
                    } catch (NumberFormatException ignored3) {
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener company.id del token", e);
            }
        }

        // ================== VALIDACIÓN ==================
        if (companyIdFromToken == null) {
            throw new org.springframework.security.access.AccessDeniedException("El token no contiene company.id");
        }
        if (!companyIdFromToken.equals(company)) {
            company = companyIdFromToken;
        }

        List<SysUserFields> sysUsers = sysUserService.findSysUsersByMySysGroups(company, sysUser);
        ResponseEntity<List<SysUserFields>> pageResponseEntity = new ResponseEntity<>(sysUsers, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/sysUserByEmailAndSolverAndCode")
    public ResponseEntity<SysUser> getCodeByEmailAndSolver(
            @NotNull @RequestParam(value = "email", required = true, defaultValue = "0") String email,
            @NotNull @RequestParam(value = "solver", required = true, defaultValue = "true") boolean solver,
            @NotNull @RequestParam(value = "code", required = true, defaultValue = "") String code) {
        log.info("sysUserByEmailAndSolverAndCode");
        log.info(code);
        SysUser sysUser = sysUserService.findByEmailAndSolver(email, solver);
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        SysUser currentRecord = null;
        boolean flag = code.equals(sysUser.getCode()); // bcrypt.matches(code, sysUser.getCode());
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
public ResponseEntity<Map<String, String>> findByEmailAndSolver(
        @NotNull @RequestParam("email") String email,
        @NotNull @RequestParam("solver") boolean solver)
        throws Exception {

    log.info("sysUserByEmailAndSolver");
    SysUser sysUser = sysUserService.findByEmailAndSolver(email, solver);

    if (sysUser == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Map<String, String> response = new HashMap<>();
    response.put("id", String.valueOf(sysUser.getId()));
    return ResponseEntity.ok(response);

}


    public void save(SysUser sysUser) throws Exception {
        sysUserService.save(sysUser);
        this.update(sysUser, sysUser.getId());
    }

    @GetMapping("/findUserGroupsByFilters")
    public ResponseEntity<List<SysUserFields>> findUserGroupsByFilters(
            @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {

           // ================== LECTURA DE CLAIMS DESDE AUTH.DETAILS ==================
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

@SuppressWarnings("unchecked")
Map<String, Object> claims = (auth != null && auth.getDetails() instanceof Map)
        ? (Map<String, Object>) auth.getDetails()
        : java.util.Collections.emptyMap();

Long companyIdFromToken = null;
Object companyObj = claims.get("company");

if (companyObj != null) {
    // Caso A: viene como POJO Company (lo que muestra tu depurador)
    try {
        // Si tienes la clase a mano, mejor casteo directo:
        // companyIdFromToken = ((Company) companyObj).getId();
        // Si no, usa reflexión para no acoplarte:
        java.lang.reflect.Method m = companyObj.getClass().getMethod("getId");
        Object idObj = m.invoke(companyObj);
        if (idObj != null) {
            companyIdFromToken = Long.valueOf(idObj.toString());
        }
    } catch (NoSuchMethodException ignored) {
        // Caso B: a veces podría venir como Map (JSON)
        if (companyObj instanceof Map) {
            Object id = ((Map<?, ?>) companyObj).get("id");
            if (id != null) {
                try { companyIdFromToken = Long.valueOf(id.toString()); } catch (NumberFormatException ignored2) {}
            }
        } else {
            try { companyIdFromToken = Long.valueOf(companyObj.toString()); } catch (NumberFormatException ignored3) {}
        }
    } catch (Exception e) {
        log.warn("No se pudo obtener company.id del token", e);
    }
}

// ================== VALIDACIÓN ==================
if (companyIdFromToken == null) {
    throw new org.springframework.security.access.AccessDeniedException("El token no contiene company.id");
}
if (!companyIdFromToken.equals(company)) {
    company = companyIdFromToken;
}

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
