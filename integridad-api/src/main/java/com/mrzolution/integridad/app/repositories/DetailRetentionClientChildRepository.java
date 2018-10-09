package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailRetentionClientChildRepository")
public interface DetailRetentionClientChildRepository extends ChildRepository<RetentionClient>, JpaRepository<DetailRetentionClient, UUID>{
    @Query("SELECT d.id FROM DetailRetentionClient d WHERE d.retentionClient = (:id)")
    Iterable<UUID> findByFather(@Param("id") RetentionClient retentionClient);
}
