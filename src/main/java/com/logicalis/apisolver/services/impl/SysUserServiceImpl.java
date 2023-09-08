
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ISysUserDAO;
import com.logicalis.apisolver.model.SysUser;
import com.logicalis.apisolver.model.SysUserFields;
import com.logicalis.apisolver.services.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements ISysUserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private ISysUserDAO dao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // SysUser a = dao.findByEmailAndSolver(userName, true);
        SysUser user = dao.findByEmailAndSolver(userName, true);//dao.findByUserName(userName);

        if (user == null) {
            logger.error("Error en el login: no existe el usuario '" + userName + "' en el sistema!");
            throw new UsernameNotFoundException("Error en el login: no existe el usuario '" + userName + "' en el sistema!");
        }

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new User(userName, user.getPassword(), user.getActive(), true, true, true, authorities);
    }

    @Override
    public List<SysUser> findAll() {
        return (List<SysUser>) dao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SysUser> findAll(Pageable pageRequest) {
        return (Page<SysUser>) dao.findAll(pageRequest);
    }

    @Override
    @Transactional
    public SysUser save(SysUser sysUser) {
        return dao.save(sysUser);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUser findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUser findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public SysUser findByEmail(String email) {
        return dao.findByEmail(email);
    }

    /*@Override
    public SysUserFields findByEmailAndSolver(String email) {
        return dao.findByEmailAndSolver(email);
    }*/
    @Override
    public SysUser getCodeByEmailAndSolver(String email, boolean solver) {
        return dao.getCodeByEmailAndSolver(email, solver);
    }

    @Override
    public SysUser findByEmailAndSolver(String email, boolean solver) {
        return dao.findByEmailAndSolver(email, solver);
    }

    @Override
    public SysUser findByEmailAndSolverAndCode(String email, boolean solver, String code) {
        return dao.findByEmailAndSolverAndCode(email, solver, code);
    }

    @Override
    public SysUser findByEmailAndPassword(String email, String password) {
        return dao.findByEmailAndPassword(email, password);
    }

    @Override
    public List<String> findMenusByEmail(String email) {
        return dao.findMenusByEmail(email);
    }

    @Override
    public SysUser findByUserName(String userName) {
        return dao.findByUserName(userName);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SysUser> findAllByNameLike(String name, Pageable pageRequest) {
        return (Page<SysUser>) dao.findAllByNameLike(name, pageRequest);
    }

    @Override
    public List<SysUserFields> findUserGroupsByFilters(Long company) {
        return dao.findUserGroupsByFilters(company);
    }

    @Override
    public List<SysUserFields> findSysUsersByMySysGroups(Long company, Long sysUser) {
        return dao.findSysUsersByMySysGroups(company, sysUser);
    }

}
