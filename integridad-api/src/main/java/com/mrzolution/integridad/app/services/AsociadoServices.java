package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Asociado;
import com.mrzolution.integridad.app.repositories.AsociadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsociadoServices {
    @Autowired
	AsociadoRepository asociadoRepository;

    public Iterable<Asociado> getAllActivesLazy() {
		Iterable<Asociado> asociadosList = asociadoRepository.findByActive(true);
		asociadosList.forEach(aso -> {aso.setFatherListToNull();});
		return asociadosList;
    }

}