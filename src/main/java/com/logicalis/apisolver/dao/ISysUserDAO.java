package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.SysUser;
import com.logicalis.apisolver.model.SysUserFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ISysUserDAO extends PagingAndSortingRepository<SysUser, Long> {

    public SysUser findByEmail(String email);
   /* @Query(value = "SELECT DISTINCT a.id,\n" +
            "a.integration_id as integrationId,\n" +
            "a.name\n" +
            "FROM sys_user a\n" +
            "WHERE a.email = ?1\n" +
            "AND a.solver IS TRUE\n" +
            "AND a.active IS TRUE\n" +
            "limit (1)", nativeQuery = true)
    public SysUserFields findByEmailAndSolver(String email);*/

    public SysUser findByEmailAndSolver(String email, boolean solver);

    public SysUser findByEmailAndSolverAndCode(String email, boolean solver, String code);

    public SysUser getCodeByEmailAndSolver(String email, boolean solver);

    public SysUser findByIntegrationId(String integrationId);

    public SysUser findByEmailAndPassword(String email, String password);

    public Page<SysUser> findAllByNameLike(String name, Pageable pageRequest);

    @Query(value = "--select distinct concat('/',a.level_one,'/',a.state) url from menu a\n" +
            "\t--inner join profile_menu b on a.id = b.id_menu \n" +
            "\t--inner join profile c on b.id_profile = c.id\n" +
            "\t--inner join sys_user_profile d on d.id_profile = c.id\n" +
            "\t--inner join sys_user e on d.id_user = e.id\n" +
            "\t--where e.email = ?1\n" +
            "\t--and a.level_one is not null\n" +
            "\t--and a.state is not null\n" +
            "\t--union\n" +
            "\tselect distinct concat('/',a.level_one,'/',a.state) url from menu a\n" +
            "\tinner join profile_menu b on a.id = b.id_menu \n" +
            "\tinner join profile c on b.id_profile = c.id\n" +
            "\tinner join rol_profile d on c.id = d.id_profile\n" +
            "\tinner join rol e on d.id_rol = e.id\n" +
            "\tinner join sys_user_rol f on e.id = f.id_rol\n" +
            "\tinner join sys_user g on f.id_user = g.id\n" +
            "\twhere g.email = ?1\n" +
            "\tand a.level_one is not null\n" +
            "\tand a.state is not null", nativeQuery = true)
    public List<String> findMenusByEmail(String email);

    public SysUser findByUserName(String userName);

    @Query(value = "SELECT DISTINCT b.id,\n" +
            "b.integration_id as integrationId,\n" +
            "b.name\n" +
            "FROM sys_user_group a\n" +
            "INNER JOIN sys_user b ON a.sys_user = b.id AND b.active IS TRUE\n" +
            "INNER JOIN sys_group c ON a.sys_group = c.id AND c.active IS TRUE\n" +
            "INNER JOIN company d ON c.company = d.id\n" +
            "WHERE (?1 = 0 OR d.id = ?1)\n" +
            "AND a.active IS TRUE\n" +
            "ORDER  BY b.name ASC", nativeQuery = true)
    public List<SysUserFields> findUserGroupsByFilters(Long company);

    @Query(value = "SELECT DISTINCT\n" +
            "a.id,\n" +
            "a.integration_id as integrationId,\n" +
            "a.name,\n" +
            "c.sys_group as sysGroup\n" +
            "FROM sys_user a\n" +
            "INNER JOIN company b ON a.company = b.id\n" +
            "INNER JOIN sys_user_group c ON a.id = c.sys_user\n" +
            "WHERE a.company = ?1\n" +
            "AND EXISTS (SELECT  'x'\n" +
            "            FROM sys_user_group a\n" +
            "            WHERE  a.sys_group = c.sys_group\n" +
            "                   AND a.sys_user = ?2)\n" +
            "ORDER  BY a.name ASC", nativeQuery = true)
    public List<SysUserFields> findSysUsersByMySysGroups(Long company, Long sysUser);
}




