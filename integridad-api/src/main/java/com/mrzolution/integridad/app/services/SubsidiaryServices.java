package com.mrzolution.integridad.app.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.repositories.SubsidiaryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubsidiaryServices {
	
	@Autowired
	SubsidiaryRepository subsidiaryRepository;
	@Autowired
	CashierRepository cashierRepository;

	public Subsidiary create(Subsidiary subsidiary){
		log.info("SubsidiaryServices create: {}", subsidiary.getName());
		subsidiary.setDateCreated(new Date().getTime());
		subsidiary.setActive(true);

		List<Cashier> cashierList = subsidiary.getCashiers();
		subsidiary.setCashiers(null);

		if(cashierList == null || cashierList.isEmpty()){
			throw new BadRequestException("La sucursal debe tener por lo menos una caja");
		}

		Subsidiary saved = subsidiaryRepository.save(subsidiary);

		cashierList.forEach(cashier->{
			cashier.setSubsidiary(saved);
			cashierRepository.save(cashier);
			cashier.setSubsidiary(null);
		});

		log.info("SubsidiaryServices created: {}", saved.getId());

		saved.setCashiers(cashierList);
		return saved;
	}
	
	public Iterable<Subsidiary> getAllActivesByUserClientId(UUID userClientId){
		log.info("SubsidiaryServices getAllActivesByUserClientId: {}", userClientId);
		Iterable<Subsidiary> subsidiaries = subsidiaryRepository.findByUserClientIdAndActive(userClientId, true);
		subsidiaries.forEach(subsidiary->{
			subsidiary.setFatherListToNull();
			subsidiary.setListsNull();
		});
		log.info("SubsidiaryServices getAllActivesByUserClientId size retrieved: {}", Iterables.size(subsidiaries));
		return subsidiaries;
	}

	public Subsidiary getById(UUID id){
		log.info("SubsidiaryServices getById: {}", id);
		Subsidiary subsidiary = subsidiaryRepository.findOne(id);
		subsidiary.setFatherListToNull();
		subsidiary.setListsNull();

		return subsidiary;
	}
}
