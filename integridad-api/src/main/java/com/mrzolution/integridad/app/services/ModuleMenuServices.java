package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.ModuleMenu;
import com.mrzolution.integridad.app.repositories.ModuleMenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ModuleMenuServices {
    @Autowired
    ModuleMenuRepository moduleMenuRepository;

    public Iterable<ModuleMenu> getAll(){
        return moduleMenuRepository.findAll();
    }

}