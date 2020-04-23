package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.OptionConfigCuentas;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 *
 * @author daniel
 */

@Repository
@Qualifier(value="ConfigCuentasRepository")
public interface OptionConfigCuentasRepository extends CrudRepository<OptionConfigCuentas, UUID> {

    OptionConfigCuentas findByCode(String code);
}
