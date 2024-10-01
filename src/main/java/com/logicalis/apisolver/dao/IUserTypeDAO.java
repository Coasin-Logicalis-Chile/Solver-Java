package com.logicalis.apisolver.dao;
import com.logicalis.apisolver.model.UserType;
import org.springframework.data.repository.CrudRepository;

public interface IUserTypeDAO extends CrudRepository<UserType, Long> {

    //public UserType findById(Long ID);
    public UserType findByName(String name);
}
