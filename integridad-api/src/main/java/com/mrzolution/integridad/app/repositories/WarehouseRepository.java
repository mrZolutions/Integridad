package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.Warehouse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Qualifier(value="WarehouseRepository")
public interface WarehouseRepository extends CrudRepository<Warehouse, UUID> {	
    Iterable<Warehouse> findWarehouseBySubsidiary(Subsidiary subsidiary);

    @Query("SELECT w FROM Warehouse w WHERE w.subsidiary.userClient.id = (:id) AND w.active = true ORDER BY w.codeWarehouse")
    Iterable<Warehouse> findWarehouseByUserClientId(@Param("id") UUID id);
}
