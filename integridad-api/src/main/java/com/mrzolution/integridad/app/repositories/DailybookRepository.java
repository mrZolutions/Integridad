package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Dailybook;
import com.mrzolution.integridad.app.domain.DebtsToPay;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DailybookRepository")
public interface DailybookRepository extends CrudRepository<Dailybook, UUID> {
    Iterable<Dailybook> findByDebtsToPay(DebtsToPay debtsToPay);
}
