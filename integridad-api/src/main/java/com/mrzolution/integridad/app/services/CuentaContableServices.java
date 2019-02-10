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

    public CuentaContable createCuentaContable(CuentaContable cuentaContable) {
	if(cuentaContable.getCode() == null){
            throw new BadRequestException("Debe tener el codigo de contabilidad");
	}
	cuentaContable.setActive(true);
	CuentaContable saved = cuentaContableRepository.save(cuentaContable);
	log.info("CuentaContableServices createCuentaContable DONE id: {}", saved.getId());
	return saved;
    }
    
    public void updateCuentaContable(CuentaContable cuentaContable) throws BadRequestException {
        if (cuentaContable.getId() == null) {
            throw new BadRequestException("Cuenta Contable Inválida");
        }
        log.info("CuentaContableServices updateCuentaContable: {}", cuentaContable.getDescription());
        cuentaContable.setListsNull();
        CuentaContable updated = cuentaContableRepository.save(cuentaContable);
        log.info("CuentaContableServices updateCuentaContable DONE id: {}", updated.getId());
    }
  
    public Iterable<CuentaContable> getAll() {
        Iterable<CuentaContable> result = cuentaContableRepository.findAll();
        result.forEach(res -> {res.setFatherListToNull(); res.setListsNull();});
        return result;
    }

    public Iterable<CuentaContable> getCuentaContableByUserClient(UUID id) {
	log.info("CuentaContableServices getCuentaContableByUserClient");
	Iterable<CuentaContable> cuentas = cuentaContableRepository.findByUserClientId(id);
	for (CuentaContable cuenta : cuentas) {
            cuenta.setListsNull();
            cuenta.setFatherListToNull();
	}
	return cuentas;
    }
    
    // Selecciona Ctas Contables por Tipo
    public Iterable<CuentaContable> getCuentaContableByType(UUID id, String typ) {
        Iterable<CuentaContable> cuentasType = cuentaContableRepository.findByType(id, typ);
        cuentasType.forEach(ctasType -> {
            ctasType.setListsNull();
            ctasType.setFatherListToNull();
        });
        return cuentasType;
    }
    
    // Selecciona Ctas Contables por Type y por AccountType
    public Iterable<CuentaContable> getCuentaContableByTypeAndAccountType(String typ, String atyp) {
        Iterable<CuentaContable> ctasTypeAccType = cuentaContableRepository.findByTypeAndAccountType(typ, atyp);
        ctasTypeAccType.forEach(ctasTypAcc -> {
            ctasTypAcc.setListsNull();
            ctasTypAcc.setFatherListToNull();
        });
        return ctasTypeAccType;
    }
    
}
