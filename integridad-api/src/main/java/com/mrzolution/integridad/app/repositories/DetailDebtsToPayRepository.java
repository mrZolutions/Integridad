package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.DetailDebtsToPay;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailDebtsToPayRepository")
public interface DetailDebtsToPayRepository extends CrudRepository<DetailDebtsToPay, UUID>{
    Iterable<DetailDebtsToPay> findByDebtsToPay(DebtsToPay debtsToPay);
}
