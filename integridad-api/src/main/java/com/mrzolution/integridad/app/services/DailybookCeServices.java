package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DetailDailybookCe;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCeRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookCeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class DailybookCeServices {
    @Autowired
    DailybookCeRepository dailybookCeRepository;
    @Autowired
    DetailDailybookCeRepository detailDailybookCeRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona COMPROBANTE DE EGRESO por Id
    public DailybookCe getDailybookCeById(UUID id) {
        log.info("DailybookCeServices getDailybookCeById: {}", id);
        DailybookCe retrieved = dailybookCeRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCeServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCeServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }
    
    //Selecciona todos los COMPROBANTES DE EGRESO por UserClientId
    public Iterable<DailybookCe> getDailybookCeByUserClientId(UUID userClientId) {
        log.info("DailybookCeServices getDailybookCgByUserClientId: {}", userClientId);
        Iterable<DailybookCe> dailys = dailybookCeRepository.findDailybookCeByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTES DE EGRESO por ProviderId
    public Iterable<DailybookCe> getDailybookCeByProviderId(UUID Id) {
        log.info("DailybookCeServices getDailybookCeByProviderId: {}", Id);
        Iterable<DailybookCe> dailys = dailybookCeRepository.findDailybookCeByProviderId(Id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Creación de los Diarios CxP
    public DailybookCe createDailybookCg(DailybookCe dailybookCe) throws BadRequestException {
        List<DetailDailybookCe> detailDailybookCe = dailybookCe.getDetailDailybookCe();
        
        if (detailDailybookCe == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCe.setActive(true);
        dailybookCe.setDetailDailybookCe(null);
        dailybookCe.setFatherListToNull();
        dailybookCe.setListsNull();
        DailybookCe saved = dailybookCeRepository.save(dailybookCe);
        
        Cashier cashier = cashierRepository.findOne(dailybookCe.getUserIntegridad().getCashier().getId());
        cashier.setDailyCppNumberSeq(cashier.getDailyCeNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookCe.forEach(detail -> {
            detail.setDailybookCe(saved);
            detailDailybookCeRepository.save(detail);
            detail.setDailybookCe(null);
        });
        
        saved.setDetailDailybookCe(detailDailybookCe);
        log.info("DailybookCeServices createDailybookCe DONE id: {}", saved.getId());
        return saved;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE EGRESO
    private void populateChildren(DailybookCe dailybookCe) {
	List<DetailDailybookCe> detailDailybookCeList = new ArrayList<>();
	Iterable<DetailDailybookCe> dailybookCesDetail = detailDailybookCeRepository.findByDailybookCe(dailybookCe);
        dailybookCesDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCe(null);
            detailDailybookCeList.add(detail);
	});
	dailybookCe.setDetailDailybookCe(detailDailybookCeList);
	dailybookCe.setFatherListToNull();
    }
}