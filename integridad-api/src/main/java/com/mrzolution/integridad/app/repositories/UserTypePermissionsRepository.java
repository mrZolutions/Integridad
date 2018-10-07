package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.domain.UserTypePermissions;

import org.springframework.data.repository.CrudRepository;

@Repository
@Qualifier(value="UserTypePermissionsRepository")
public interface UserTypePermissionsRepository extends CrudRepository<UserTypePermissions, UUID>{

	Iterable<UserTypePermissions> findByUserType(UserType userType);
}
