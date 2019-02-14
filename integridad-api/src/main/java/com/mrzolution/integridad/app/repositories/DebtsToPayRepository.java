package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.Provider;
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
@Qualifier(value = "DebtsToPayRepository")
public interface DebtsToPayRepository extends CrudRepository<DebtsToPay, UUID> {
    Iterable<DebtsToPay> findByProvider(Provider provider);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.provider.id = (:id) AND dp.active = true ORDER BY dp.billNumber")
    Iterable<DebtsToPay> findDebtsToPayByProviderId(@Param("id") UUID id);
}
