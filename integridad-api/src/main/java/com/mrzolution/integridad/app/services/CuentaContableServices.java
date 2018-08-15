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
  
  public Iterable<CuentaContable> getAll() {
        Iterable<CuentaContable> result = cuentaContableRepository.findAll();
        result.forEach(res -> {res.setFatherListToNull(); res.setListsNull();});
        return result;
    }

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
