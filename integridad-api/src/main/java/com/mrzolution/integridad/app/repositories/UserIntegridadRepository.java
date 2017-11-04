package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import com.mrzolution.integridad.app.domain.Subsidiary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.domain.UserType;

import org.springframework.data.repository.CrudRepository;

@Repository
@Qualifier(value="UserIntegridadRepository")
public interface UserIntegridadRepository extends CrudRepository<UserIntegridad, UUID>{

	UserIntegridad findByEmailIgnoreCaseAndActive(String email, boolean active);

	UserIntegridad findByIdAndValidation(UUID userId, String validation);
	
	Iterable<UserIntegridad> findByUserType(UserType userType);

	Iterable<UserIntegridad> findByUserTypeAndActive(UserType userType, boolean active);

	Iterable<UserIntegridad> findByUserTypeAndActiveAndSubsidiary(UserType userType, boolean active, Subsidiary subsidiary);

	Iterable<UserIntegridad> findByActive(boolean active);

}
