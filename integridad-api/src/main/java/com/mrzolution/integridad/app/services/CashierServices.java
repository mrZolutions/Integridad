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


	public Iterable<Cashier> getAllBySubsiduaryActivesLazy(Subsidiary sub){
		log.info("CashierServices getAllBySubsiduaryActivesLazy");
		Iterable<Cashier> actives = cashierRepository.findBySubsidiary(sub);
		actives.forEach(brand -> {
			brand.setFatherListToNull();
			brand.setListsNull();
		});
		return actives;

	}
}
