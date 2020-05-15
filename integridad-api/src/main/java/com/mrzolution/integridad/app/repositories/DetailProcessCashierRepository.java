package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DetailProcessCashier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="DetailProcessCashierRepository")
public interface DetailProcessCashierRepository extends CrudRepository<DetailProcessCashier, UUID>{ }
