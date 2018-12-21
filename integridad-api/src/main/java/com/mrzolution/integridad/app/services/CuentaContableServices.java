package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CuentaContableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CuentaContableServices {

    @Autowired
    CuentaContableRepository cuentaContableRepository;

    public CuentaContable create(CuentaContable cuentaContable) {
	if(cuentaContable.getCode() == null){
            throw new BadRequestException("Debe tener el codigo de contabilidad");
	}
	log.info("CuentaContableServices create: {}", cuentaContable.getCode());
	cuentaContable.setActive(true);
	CuentaContable saved = cuentaContableRepository.save(cuentaContable);
	log.info("CuentaContableServices created id: {}", saved.getId());
	return saved;
    }
    
    public void update(CuentaContable cuentaContable) throws BadRequestException {
        if (cuentaContable.getId() == null) {
            throw new BadRequestException("Cuenta Contable Inválida");
        }
        log.info("CuentaContableServices update: {}", cuentaContable.getDescription());
        cuentaContable.setListsNull();
        CuentaContable updated = cuentaContableRepository.save(cuentaContable);
        log.info("CuentaContableServices updated DONE id: {}", updated.getId());
    }
  
    public Iterable<CuentaContable> getAll() {
        Iterable<CuentaContable> result = cuentaContableRepository.findAll();
        result.forEach(res -> {res.setFatherListToNull(); res.setListsNull();});
        return result;
    }

    public Iterable<CuentaContable> getLazyByUserClient(UUID id) {
	log.info("CuentaContableServices getLazyByUserClient");
	Iterable<CuentaContable> cuentas = cuentaContableRepository.findByUserClientId(id);
	for (CuentaContable cuenta : cuentas) {
            cuenta.setListsNull();
            cuenta.setFatherListToNull();
	}
	log.info("CuentaContable getLazyByUserClient size retrieved: {}", Iterables.size(cuentas));
	return cuentas;
    }
    
    public Iterable<CuentaContable> getByType(String typ) {
        Iterable<CuentaContable> cuentasType = cuentaContableRepository.findByType(typ);
        cuentasType.forEach(ctasType -> {
            ctasType.setListsNull();
            ctasType.setFatherListToNull();
        });
        return cuentasType;
    }
    
}
