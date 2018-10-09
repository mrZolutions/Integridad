package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.RetentionClient;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailRetentionClientRepository")
public interface DetailRetentionClientRepository extends CrudRepository<DetailRetentionClient, UUID>{
    Iterable<DetailRetentionClient> findByRetentionClient(RetentionClient retentionClient);
}
