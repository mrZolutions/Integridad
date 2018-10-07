package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.DetailRetention;
import com.mrzolution.integridad.app.domain.Retention;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="DetailRetentionChildRepository")
public interface DetailRetentionChildRepository extends ChildRepository<Retention>, JpaRepository<DetailRetention, UUID>{

    @Query("SELECT d.id FROM DetailRetention d WHERE d.retention = (:id)")
    Iterable<UUID> findByFather(@Param("id") Retention retention);

}
