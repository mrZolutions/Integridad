package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.DetailRetention;
import com.mrzolution.integridad.app.domain.Retention;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="DetailRetentionRepository")
public interface DetailRetentionRepository extends CrudRepository<DetailRetention, UUID>{
	
	Iterable<DetailRetention> findByRetention(Retention retention);

}
