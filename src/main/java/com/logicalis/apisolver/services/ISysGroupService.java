
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.SysGroup;
import com.logicalis.apisolver.model.SysGroupFields;

import java.util.List;

public interface ISysGroupService {

    public List<SysGroup> findAll();

    public SysGroup save(SysGroup sysGroup);

    public SysGroup findById(Long id);

    public void delete(Long id);

    public List<SysGroup> findByActive(boolean active);

    public SysGroup findTopByActive(boolean active);

    public SysGroup findByIntegrationId(String integrationId);

    public SysGroup findByActiveAndName(boolean Active, String name);

    public List<SysGroupFields> findSysGroupsByFilters(Long company);

    public List<SysGroupFields> findSysGroupsByFilters(Long company, Long sysUser);

}
