package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CashierChildRepository")
public interface CashierChildRepository extends ChildRepository<Subsidiary>, JpaRepository<Cashier, UUID>{

    @Query("SELECT s.id FROM Cashier s WHERE s.subsidiary = (:id)")
    Iterable<UUID> findByFather(@Param("id") Subsidiary subsidiary);

}
