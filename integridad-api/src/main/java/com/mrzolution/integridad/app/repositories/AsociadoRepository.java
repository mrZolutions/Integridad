package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Asociado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="AsociadoRepository")
public interface AsociadoRepository extends CrudRepository<Asociado, UUID>{

	Iterable<Asociado> findByActive(boolean active);

}
