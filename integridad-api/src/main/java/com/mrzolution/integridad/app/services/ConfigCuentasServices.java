package com.mrzolution.integridad.app.services;

import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.domain.ConfigCuentas;
import com.mrzolution.integridad.app.repositories.ConfigCuentasRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfigCuentasServices {

    @Autowired
    ConfigCuentasRepository configCuentasRepository;

    public ConfigCuentas saveConfigCuentas(ConfigCuentas configCuentas){
        ConfigCuentas configSaved = configCuentasRepository.save(configCuentas);
        configSaved.getUserClient().setListsNull();
        configSaved.getUserClient().setFatherListToNull();
        return configSaved;
    }

    public List<ConfigCuentas> saveConfigCuentasList(List<ConfigCuentas> configCuentasList, UUID userClientId){

        Iterable<ConfigCuentas> configCuentasToRemove = configCuentasRepository.findByUserClientId(userClientId);
        configCuentasRepository.delete(configCuentasToRemove);

        Iterable<ConfigCuentas> configListSaved = configCuentasRepository.save(configCuentasList);
        List<ConfigCuentas> cuentasConfigListSaved = Lists.newArrayList(configListSaved);

        for(ConfigCuentas config : cuentasConfigListSaved){
            config.getUserClient().setListsNull();
            config.getUserClient().setFatherListToNull();
        }
        return cuentasConfigListSaved;
    }

    public List<ConfigCuentas> getCuentasByUserCliendId(UUID id){
        Iterable<ConfigCuentas> configCuentas = configCuentasRepository.findByUserClientId(id);
        List<ConfigCuentas> cuentasConfig = Lists.newArrayList(configCuentas);
        for(ConfigCuentas config : cuentasConfig){
            config.getUserClient().setListsNull();
            config.getUserClient().setFatherListToNull();
        }

        return cuentasConfig;
    }

    public ConfigCuentas getCuentasByUserCliendIdAndOptionCode(UUID userClientId, String code) {
        ConfigCuentas configCuentas = configCuentasRepository.findByUserClientIdAndOptionCode(userClientId, code);

        if(configCuentas != null){
            configCuentas.getUserClient().setListsNull();
            configCuentas.getUserClient().setFatherListToNull();
        }

        return configCuentas;
    }
}
