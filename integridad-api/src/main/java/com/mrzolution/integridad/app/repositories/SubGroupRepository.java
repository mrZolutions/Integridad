package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.SubGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="SubGroupRepository")
public interface SubGroupRepository extends CrudRepository<SubGroup, UUID>{
	
	Iterable<GroupLine> findByActive(boolean active);
	
	@Query("SELECT p FROM SubGroup p WHERE p.groupLine.id = (:id) and p.active = true")
	Iterable<SubGroup> findByGroupLineIdAndActive(@Param("id") UUID groupLineId);

	@Query("SELECT p FROM SubGroup p WHERE p.groupLine.line.userClient.id = (:id) and p.name LIKE (%:nameSub)")
	SubGroup findByNameAndUserClient(@Param("nameSub")String name, @Param("id") UUID userClientId);

}
