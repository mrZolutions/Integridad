package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.Product;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="GroupLineRepository")
public interface GroupLineRepository extends CrudRepository<GroupLine, UUID>{
	
	Iterable<GroupLine> findByActive(boolean active);
	
	@Query("SELECT p FROM GroupLine p WHERE p.line.id = (:id) and p.active = true")
	Iterable<GroupLine> findByLineIdAndActive(@Param("id") UUID lineId);

}
