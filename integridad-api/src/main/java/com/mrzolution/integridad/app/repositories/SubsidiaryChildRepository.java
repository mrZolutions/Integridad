package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.interfaces.ChildRepository;

@Repository
@Qualifier(value="SubsidiaryChildRepository")
public interface SubsidiaryChildRepository extends ChildRepository<UserClient>, JpaRepository<Subsidiary, UUID>{

    @Query("SELECT s.id FROM Subsidiary s WHERE s.userClient = (:id)")
    Iterable<UUID> findByFather(@Param("id") UserClient userClient);

}
