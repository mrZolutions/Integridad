package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.UserType;

import org.springframework.data.repository.CrudRepository;

@Repository
@Qualifier(value="UserTypeRepository")
public interface UserTypeRepository extends CrudRepository<UserType, UUID>{

}
