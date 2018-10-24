package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Client;

@Repository
@Qualifier(value="ClientRepository")
public interface ClientRepository extends CrudRepository<Client, UUID>{

	Iterable<Client> findByActive(boolean active);
        
        Iterable<Client> findByIdentificacion(String identification);

	@Query("SELECT c FROM Client c WHERE c.userClient.id = (:id) and c.active = true")
	Iterable<Client> findActivesByUserClientId(@Param("id") UUID id);
}
