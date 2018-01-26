package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProviderRepository")
public interface ProviderRepository extends CrudRepository<Provider, UUID>{

	Iterable<Provider> findByActive(boolean active);
	Iterable<Provider> findByProviderType(String type);

//	@Query("SELECT c FROM Client c WHERE c.userClient.id = (:id) and c.active = true")
//	Iterable<Client> findActivesByUserClientId(@Param("id") UUID id);
}
