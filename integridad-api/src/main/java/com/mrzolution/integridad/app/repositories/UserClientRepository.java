package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.UserClient;

@Repository
@Qualifier(value="UserClientRepository")
public interface UserClientRepository extends CrudRepository<UserClient, UUID>{

	Iterable<UserClient> findByActive(boolean active);

}
