package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserClient;

@Repository
@Qualifier(value="SubsidiaryRepository")
public interface SubsidiaryRepository extends CrudRepository<Subsidiary, UUID>{

	Iterable<Subsidiary> findByUserClient(UserClient client);
	@Query("SELECT s FROM Subsidiary s WHERE s.userClient.id = (:id) and s.active = (:active)")
	Iterable<Subsidiary> findByUserClientIdAndActive(@Param("id") UUID clientId, @Param("active") boolean active);

}
