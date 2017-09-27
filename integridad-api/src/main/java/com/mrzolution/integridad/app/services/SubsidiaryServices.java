package com.mrzolution.integridad.app.services;

import java.util.UUID;

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
}
