package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.UserIntegridad;

import org.springframework.data.repository.CrudRepository;

@Repository
@Qualifier(value="UserIntegridadRepository")
public interface UserIntegridadRepository extends CrudRepository<UserIntegridad, UUID>{

	UserIntegridad findByEmailIgnoreCaseAndActive(String email, boolean active);

	UserIntegridad findByIdAndValidation(UUID userId, String validation);

}
