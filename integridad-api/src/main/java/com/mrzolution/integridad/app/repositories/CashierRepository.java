package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CashierRepository")
public interface CashierRepository extends CrudRepository<Cashier, UUID> {
    @Query("SELECT c FROM Cashier c WHERE c.subsidiary.id = (:id) AND c.active = true")
    Iterable<Cashier> findBySubsidiaryId(@Param("id") UUID id);

    Iterable<Cashier> findBySubsidiary(Subsidiary subsidiary);
}
