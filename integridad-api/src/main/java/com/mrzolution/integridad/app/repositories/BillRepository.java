package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.UserIntegridad;

@Repository
@Qualifier(value="BillRepository")
public interface BillRepository extends CrudRepository<Bill, UUID>{
	
	Iterable<Bill> findByClient(Client client);
	
	Iterable<Bill> findByUserIntegridad(UserIntegridad user);

}
