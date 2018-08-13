package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CuentaContable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CuentaContableRepository extends CrudRepository<CuentaContable, UUID> {
}
