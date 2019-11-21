package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.SwimmingPool;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="SwimmingPoolRepository")
public interface SwimmingPoolRepository extends CrudRepository<SwimmingPool, UUID> {
    Iterable<SwimmingPool> findByClient(Client client);
    
    Iterable<SwimmingPool> findByUserIntegridad(UserIntegridad user);
    
    @Query("SELECT s FROM SwimmingPool s WHERE s.client.id = (:id) AND s.active = true ORDER BY s.fecha")
    Iterable<SwimmingPool> findSwimmPoolByClientId(@Param("id") UUID id);
    
    @Query("SELECT s FROM SwimmingPool s WHERE s.subsidiary.id = (:subId) AND s.barCode = (:barCode) AND s.active = true")
    SwimmingPool findSwimmPoolBySubIdAndBarCodeActive(@Param("subId") UUID subId, @Param("barCode") String barCode);
    
    @Query("SELECT s FROM SwimmingPool s WHERE s.subsidiary.id = (:subId) AND s.barCode = (:barCode)")
    SwimmingPool findSwimmPoolBySubIdAndBarCodeAll(@Param("subId") UUID subId, @Param("barCode") String barCode);
}