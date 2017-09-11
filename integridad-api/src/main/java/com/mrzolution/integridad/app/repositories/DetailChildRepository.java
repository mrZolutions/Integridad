package com.mrzolution.integridad.app.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.interfaces.ChildRepository;

import java.util.UUID;

@Repository
@Qualifier(value="MarketChildExhibitorRepository")
public interface DetailChildRepository extends ChildRepository<Bill>, JpaRepository<Detail, UUID>{

    @Query("SELECT d.id FROM Detail d WHERE d.bill = (:id)")
    Iterable<UUID> findByFather(@Param("id") Bill bill);

}
