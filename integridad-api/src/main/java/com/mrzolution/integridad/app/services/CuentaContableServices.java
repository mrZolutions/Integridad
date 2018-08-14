package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CuentaContableRepository;
import com.mrzolution.integridad.app.repositories.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class CuentaContableServices {

	@Autowired
	CuentaContableRepository cuentaContableRepository;

	public CuentaContable create(CuentaContable cuentaContable){
		if(cuentaContable.getCode() == null){
			throw new BadRequestException("Debe tener el codigo de contabilidad");
		}
		log.info("CuentaContableServices create: {}", cuentaContable.getCode());
		cuentaContable.setActive(true);
		CuentaContable saved = cuentaContableRepository.save(cuentaContable);
		log.info("CuentaContableServices created id: {}", saved.getId());
		return saved;
	}

//	public void update(Provider provider) throws BadRequestException{
//		if(provider.getId() == null){
//			throw new BadRequestException("Invalid Provider");
//		}
//		log.info("ProviderServices update: {}", provider.getName());
//		provider.setListsNull();
//		Provider updated = providerRepository.save(provider);
//		log.info("ProviderServices updated id: {}", updated.getId());
//	}
//
//	public Provider getById(UUID id){
//		log.info("ProviderServices getById: {}", id);
//		Provider retrieved = providerRepository.findOne(id);
//		if(retrieved != null){
//			log.info("ProviderServices retrieved id: {}", retrieved.getId());
//		} else {
//			log.info("ProviderServices retrieved id NULL: {}", id);
//		}
//
////		populateChildren(retrieved);
//		return retrieved;
//	}
//
//	public Iterable<Provider> getAllLazy(){
//		log.info("ProviderServices getAllLazy");
//		Iterable<Provider> providers = providerRepository.findByActive(true);
//		for (Provider provider : providers) {
//			provider.setListsNull();
//			provider.setFatherListToNull();
//		}
//		log.info("ProviderServices getAllLazy size retrieved: {}", Iterables.size(providers));
//		return providers;
//	}
//
	public Iterable<CuentaContable> getLazyByUserClient(UUID id){
		log.info("CuentaContableServices getLazyByUserClient");
		Iterable<CuentaContable> cuentas = cuentaContableRepository.findByUserClientId(id);
		for (CuentaContable cuenta : cuentas) {
			cuenta.setListsNull();
			cuenta.setFatherListToNull();
		}
		log.info("CuentaContable getLazyByUserClient size retrieved: {}", Iterables.size(cuentas));
		return cuentas;
	}
}
