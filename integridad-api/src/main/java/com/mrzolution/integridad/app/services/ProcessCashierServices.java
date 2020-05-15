package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DetailProcessCashier;
import com.mrzolution.integridad.app.domain.ProcessCashier;
import com.mrzolution.integridad.app.repositories.DetailProcessCashierRepository;
import com.mrzolution.integridad.app.repositories.ProcessCashierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class ProcessCashierServices {

    @Autowired
    private ProcessCashierRepository processCashierRepository;
    @Autowired
    private DetailProcessCashierRepository detailProcessCashierRepository;

    public ProcessCashier save(ProcessCashier processCashier){
        List<DetailProcessCashier> details = processCashier.getDetails();
        processCashier.setListsToNull();
        processCashier.setFatherListToNull();
        ProcessCashier saved = processCashierRepository.save(processCashier);
        details.forEach(detail ->{
            detail.setProcessCashier(saved);
            detailProcessCashierRepository.save(detail);
        });
        return saved;
    }

}