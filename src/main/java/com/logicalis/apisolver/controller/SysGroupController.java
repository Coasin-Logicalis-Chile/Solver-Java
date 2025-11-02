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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SysGroupController {
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private ICompanyService companyService;

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
            log.warn("sysUserGroup es NULL.");
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

    @GetMapping("/findSysGroupsByFilters")
    public ResponseEntity<List<SysGroupFields>> findSysGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company) {
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
        List<SysGroupFields> sysGroups = sysGroupService.findSysGroupsByFilters(company);
        ResponseEntity<List<SysGroupFields>> pageResponseEntity = new ResponseEntity<>(sysGroups, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/mySysGroups")
    public ResponseEntity<List<SysGroupFields>> findSysGroupsByFilters(@NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
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
        List<SysGroupFields> sysGroups = sysGroupService.findSysGroupsByFilters(company, sysUser);
        ResponseEntity<List<SysGroupFields>> pageResponseEntity = new ResponseEntity<>(sysGroups, HttpStatus.OK);
        return pageResponseEntity;
    }

    @PutMapping("/sysGroupSN")
    public ResponseEntity<?> update(@RequestBody SysGroupRequest sysGroupRequest) {
        SysGroup currentSysGroup = new SysGroup();
        SysGroup sysGroupUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(currentSysGroup)) {
            log.warn("sysUserGroup es NULL.");
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysGroupRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            log.debug("Actualizando sysGroup. sys_id Group: {}", sysGroupRequest.getSys_id());
            SysGroup sysGroup = new SysGroup();
            String tagAction = App.CreateConsole();
            String tag = "[SysGroup] ";
            sysGroup.setActive(sysGroupRequest.getActive());
            sysGroup.setName(sysGroupRequest.getName());
            sysGroup.setIntegrationId(sysGroupRequest.getSys_id());
            sysGroup.setSolver(sysGroupRequest.getU_solver());
            log.info("Estableciendo company. Sys_id company {}", sysGroupRequest.getCompany());
            Company company = companyService.findByIntegrationId(sysGroupRequest.getCompany());
            sysGroup.setCompany(company);
            sysGroup.setDomain(company.getDomain());
            SysGroup exists = sysGroupService.findByIntegrationId(sysGroup.getIntegrationId());
            if (exists != null) {
                log.info("sysGroup existe en la BD: {}", exists.getIntegrationId());
                sysGroup.setId(exists.getId());
            }else{
                log.info("sysGroup no existe en la BD: {}",  sysGroupRequest.getSys_id());
            }
            log.info("Guardando Group en la Base de Datos: {}", sysGroup.getIntegrationId());
            sysGroupUpdated = sysGroupService.save(sysGroup);
            log.info("Group Actualizado correctamente: sys_id Group: {}", sysGroup.getIntegrationId());
        } catch (DataAccessException e) {
            log.error("Error al actualizar Group: {}. Error: {}", sysGroupRequest.getSys_id(), e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysGroup", sysGroupUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}

