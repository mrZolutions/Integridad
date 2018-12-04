package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PagoDebts;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="CreditsDebtsRepository")
public interface CreditsDebtsRepository extends CrudRepository<CreditsDebts, UUID> {
    Iterable<CreditsDebts> findByPagoDebts (PagoDebts pagoDebts);
}
