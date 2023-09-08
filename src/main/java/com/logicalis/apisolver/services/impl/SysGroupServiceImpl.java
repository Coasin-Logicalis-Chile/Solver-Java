
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISysGroupDAO;
import com.logicalis.apisolver.model.SysGroup;
import com.logicalis.apisolver.model.SysGroupFields;
import com.logicalis.apisolver.services.ISysGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysGroupServiceImpl implements ISysGroupService {

    @Autowired
    private ISysGroupDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<SysGroup> findAll() {
        return (List<SysGroup>) dao.findAll();
    }

    @Override
    public SysGroup save(SysGroup sysGroup) {
        return dao.save(sysGroup);
    }

    @Override
    public SysGroup findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<SysGroup> findByActive(boolean active) {
        return null;
    }

    @Override
    public SysGroup findTopByActive(boolean active) {
        return (SysGroup) dao.findTopByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public SysGroup findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public SysGroup findByActiveAndName(boolean active, String name) {
        return dao.findByActiveAndName(active, name);
    }

    @Override
    public List<SysGroupFields> findSysGroupsByFilters(Long company) {
        return (List<SysGroupFields>) dao.findSysGroupsByFilters(company);
    }

    @Override
    public List<SysGroupFields> findSysGroupsByFilters(Long company, Long sysUser) {
        return (List<SysGroupFields>) dao.findSysGroupsByFilters(company, sysUser);
    }
}
