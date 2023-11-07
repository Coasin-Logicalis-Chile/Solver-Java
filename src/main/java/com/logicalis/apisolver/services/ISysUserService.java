package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.SysUser;
import com.logicalis.apisolver.model.SysUserFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISysUserService {

    public List<SysUser> findAll();

    public Page<SysUser> findAll(Pageable pageRequest);

    public SysUser save(SysUser sysUser);

    public SysUser findById(Long id);

    public SysUser findByIntegrationId(String integrationId);

    public SysUser findByUserName(String userName);

    public void delete(Long id);

    public SysUser findByEmail(String email);

    public SysUser getCodeByEmailAndSolver(String email, boolean solver);

    public SysUser findByEmailAndSolver(String email, boolean solver);

    public SysUser findByEmailAndSolverAndCode(String email, boolean solver, String code);

    public SysUser findByEmailAndPassword(String email, String password);

    public List<String> findMenusByEmail(String email);

    public Page<SysUser> findAllByNameLike(String name, Pageable pageRequest);

    public List<SysUserFields> findUserGroupsByFilters(Long company);

    public List<SysUserFields> findSysUsersByMySysGroups(Long company, Long sysUser);
}
