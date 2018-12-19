package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;

@Repository
@Qualifier(value="DetailRepository")
public interface DetailRepository extends CrudRepository<Detail, UUID> {	
    Iterable<Detail> findByBill(Bill bill);
}
