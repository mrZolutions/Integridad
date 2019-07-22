package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.Kardex;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="KardexRepository")
public interface KardexRepository extends CrudRepository<Kardex, UUID> {
    Iterable<Kardex> findByBill(Bill bill);
    
    Iterable<Kardex> findByCellar(Cellar cellar);
    
    Iterable<Kardex> findByConsumption(Consumption consumption);
}