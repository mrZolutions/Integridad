package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.Kardex;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="KardexRepository")
public interface KardexRepository extends CrudRepository<Kardex, UUID> {
    Iterable<Kardex> findByBill(Bill bill);
    
    @Query("SELECT k FROM Kardex k WHERE k.bill.id = (:id)")
    Iterable<Kardex> findByBillId(@Param("id") UUID id);
    
    Iterable<Kardex> findByCellar(Cellar cellar);
    
    @Query("SELECT k FROM Kardex k WHERE k.cellar.id = (:id)")
    Iterable<Kardex> findByCellarId(@Param("id") UUID id);
    
    Iterable<Kardex> findByConsumption(Consumption consumption);
    
    @Query("SELECT k FROM Kardex k WHERE k.consumption.id = (:id)")
    Iterable<Kardex> findByConsumptionId(@Param("id") UUID id);
    
    @Query("SELECT k FROM Kardex k WHERE k.userClientId = (:id) AND k.product.id = (:provID) AND k.dateRegister >= (:dateOne) AND k.dateRegister <= (:dateTwo) AND k.active = true ORDER BY k.dateRegister")
    Iterable<Kardex> findKardexActivesByUserClientIdAndProductIdAndDates(@Param("id") String id, @Param("provID") UUID provID, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}