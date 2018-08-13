package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.repositories.CuentaContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CuentaContableServices {

    @Autowired
    CuentaContableRepository cuentaContableRepository;

    public Iterable<CuentaContable> getAll() {
        Iterable<CuentaContable> result = cuentaContableRepository.findAll();
        result.forEach(res -> {res.setFatherListToNull(); res.setListsNull();});
        return result;
    }
}

