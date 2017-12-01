package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.repositories.BrandRepository;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CashierServices {

	@Autowired
	CashierRepository cashierRepository;


	public Iterable<Cashier> getAllBySubsiduaryActivesLazy(UUID subId){
		log.info("CashierServices getAllBySubsiduaryActivesLazy");
		Iterable<Cashier> actives = cashierRepository.findBySubsidiaryId(subId);
		actives.forEach(cashier -> {
			cashier.setFatherListToNull();
			cashier.setListsNull();
		});
		return actives;

	}
}
