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
public interface ClientRepository extends CrudRepository<Client, UUID> {
    Iterable<Client> findByActive(boolean active);

    @Query("SELECT c FROM Client c WHERE c.userClient.id = (:id) AND c.active = true ORDER BY c.name")
    Iterable<Client> findActivesByUserClientId(@Param("id") UUID id);
        
    @Query("SELECT c FROM Client c WHERE c.userClient.id = (:id) AND c.identification = (:ident) AND c.active = true")
    Iterable<Client> findClientByUserClientAndIdentification(@Param("id") UUID id, @Param("ident") String ident);
}