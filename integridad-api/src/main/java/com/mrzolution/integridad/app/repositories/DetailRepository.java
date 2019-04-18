package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Qualifier(value="DetailRepository")
public interface DetailRepository extends CrudRepository<Detail, UUID> {	
    Iterable<Detail> findByBill(Bill bill);
    
    @Query("SELECT d FROM Detail d JOIN d.bill b JOIN b.client cl WHERE cl.userClient.id = (:userClientId) AND (d.adicional != null OR d.adicional != '') AND b.active = true AND b.typeDocument = 1 ORDER BY b.stringSeq")
    Iterable<Detail> findDetailByUserClientIdAndAdicional(@Param("userClientId") UUID id);
}
