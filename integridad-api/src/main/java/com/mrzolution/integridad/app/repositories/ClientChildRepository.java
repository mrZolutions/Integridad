package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.interfaces.ChildRepository;

@Repository
@Qualifier(value="ClientChildRepository")
public interface ClientChildRepository extends ChildRepository<UserClient>, JpaRepository<Client, UUID>{

    @Query("SELECT s.id FROM Client s WHERE s.userClient = (:id)")
    Iterable<UUID> findByFather(@Param("id") UserClient userClient);

}
