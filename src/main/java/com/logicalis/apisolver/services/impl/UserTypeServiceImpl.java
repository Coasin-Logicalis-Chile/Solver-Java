package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IUserTypeDAO;
import com.logicalis.apisolver.model.UserType;
import com.logicalis.apisolver.services.IUserTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeServiceImpl implements IUserTypeService {

    @Autowired
    private IUserTypeDAO dao;

    @Override
    public List<UserType> findAll() { return (List<UserType>) dao.findAll(); }

    @Override
    public UserType save(UserType userType) { return dao.save(userType); }

    @Override
    public UserType findById(Long id) { return dao.findById(id).orElse(null); }

    @Override
    public UserType findByName(String name) { return (UserType) dao.findByName(name); }
}
