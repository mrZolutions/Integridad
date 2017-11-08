package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CashierRepository")
public interface CashierRepository extends CrudRepository<Cashier, UUID>{
	
	Iterable<Cashier> findBySubsidiary(Subsidiary subsidiary);

}
