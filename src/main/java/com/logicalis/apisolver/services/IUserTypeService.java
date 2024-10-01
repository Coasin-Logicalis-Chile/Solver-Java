package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.UserType;

import java.util.List;

public interface IUserTypeService {

    public List<UserType> findAll();

    public UserType save(UserType userType);

    public UserType findById(Long id);

    UserType findByName(String name);
}

