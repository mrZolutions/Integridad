package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.Warehouse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="WarehouseRepository")
public interface WarehouseRepository extends CrudRepository<Warehouse, UUID>{
	
	Iterable<Warehouse> findBySubsidiary(Subsidiary subsidiary);

}
