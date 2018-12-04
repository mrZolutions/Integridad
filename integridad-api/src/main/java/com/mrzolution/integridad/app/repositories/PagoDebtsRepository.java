package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.PagoDebts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="PagoDebtsRepository")
public interface PagoDebtsRepository extends CrudRepository<PagoDebts, UUID> {
    Iterable<PagoDebts> findByDebtsToPay (DebtsToPay debtsToPay);
}
