package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Line;
import com.mrzolution.integridad.app.domain.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="LineRepository")
public interface LineRepository extends CrudRepository<Line, UUID>{

	ProductType findByCode(String code);


	Iterable<Line> findByActive(boolean active);

	@Query("SELECT p FROM Line p WHERE p.userClient.id = (:id) and p.active = true")
	Iterable<Line> findByUserClienteIdAndActive(@Param("id") UUID lineId);

}
