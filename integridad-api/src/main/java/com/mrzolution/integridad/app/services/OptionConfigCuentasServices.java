package com.mrzolution.integridad.app.services;

import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.domain.ConfigCuentas;
import com.mrzolution.integridad.app.domain.OptionConfigCuentas;
import com.mrzolution.integridad.app.repositories.ConfigCuentasRepository;
import com.mrzolution.integridad.app.repositories.OptionConfigCuentasRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OptionConfigCuentasServices {
    @Autowired
    OptionConfigCuentasRepository optionConfigCuentasRepository;

    public Iterable<OptionConfigCuentas> getAll(){
        return optionConfigCuentasRepository.findAll();
    }

    public OptionConfigCuentas getByCode(String code){
        return optionConfigCuentasRepository.findByCode(code);
    }

}
