package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
public class CashierServices {
    @Autowired
    CashierRepository cashierRepository;

    public Iterable<Cashier> getAllBySubsidiaryActivesLazy(UUID subId) {
	Iterable<Cashier> actives = cashierRepository.findBySubsidiaryId(subId);
	actives.forEach(cashier -> {
            cashier.setFatherListToNull();
            cashier.setListsNull();
	});
        log.info("CashierServices getAllBySubsiduaryActivesLazy DONE: {}", subId);
	return actives;
    }

    public Cashier updateCashier(Cashier cashier) throws BadRequestException {
    	if (cashier.getId() == null) {
            throw new BadRequestException("Invalid Cashier");
	}
	log.info("CashierServices updateCashier: {}", cashier.getId());
	cashier.setListsNull();
	Cashier updated = cashierRepository.save(cashier);
	log.info("CashierServices updateCashier DONE id: {}", updated.getId());
	return updated;
    }
}