package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCgRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class DailybookCgServices {
    @Autowired
    DailybookCgRepository dailybookCgRepository;
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona Diario CG por Id
    public DailybookCg getDailybookCgById(UUID id) {
        DailybookCg retrieved = dailybookCgRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCgServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCgServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        log.info("DailybookCgServices getDailybookCgById: {}", id);
        return retrieved;
    }
    
    //Selecciona todos los Diarios CG por UserClientId
    public Iterable<DailybookCg> getDailybookCgByUserClientId(UUID userClientId) {
        Iterable<DailybookCg> dailys = dailybookCgRepository.findDailybookCgByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookCgServices getDailybookCgByUserClientId: {}", userClientId);
        return dailys;
    }
    
    //Creación de los Diarios CG
    public DailybookCg createDailybookCg(DailybookCg dailybookCg) throws BadRequestException {
        List<DetailDailybookContab> detailDailybookContab = dailybookCg.getDetailDailybookContab();
        
        if (detailDailybookContab == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCg.setActive(true);
        dailybookCg.setDetailDailybookContab(null);
        dailybookCg.setFatherListToNull();
        dailybookCg.setListsNull();
        DailybookCg saved = dailybookCgRepository.save(dailybookCg);
        
        Cashier cashier = cashierRepository.findOne(dailybookCg.getUserIntegridad().getCashier().getId());
        cashier.setDailyCgNumberSeq(cashier.getDailyCgNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookContab.forEach(detail -> {
            detail.setActive(true);
            detail.setDailybookCg(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCg(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCgServices createDailybookCg: {}, {}", saved.getId(), saved.getDailyCgStringSeq());
        return saved;
    }
    
    //Desactivación o Anulación de los Diarios CG
    @Async("asyncExecutor")
    public DailybookCg deactivateDailybookCg(DailybookCg dailybookCg) throws BadRequestException {
        if (dailybookCg.getId() == null) {
            throw new BadRequestException("Invalid DIARIO DE CONTABILIDAD GENERAL");
        }
        DailybookCg dailybookCgToDeactivate = dailybookCgRepository.findOne(dailybookCg.getId());
        dailybookCgToDeactivate.setListsNull();
        dailybookCgToDeactivate.setActive(false);
        dailybookCgToDeactivate.setGeneralDetail("DIARIO DE CONTABILIDAD GENERAL ANULADO");
        DailybookCg deactCgSaved = dailybookCgRepository.save(dailybookCgToDeactivate);
        updateDetailDailyBookContab(deactCgSaved);
        log.info("DailybookCgServices deactivateDailybookCg: {}", dailybookCgToDeactivate.getId());
        return dailybookCgToDeactivate;
    }
    
    public void updateDetailDailyBookContab (DailybookCg dailybookCg) {
        Iterable<DetailDailybookContab> detailCgBook = detailDailybookContabRepository.findByDailybookCgId(dailybookCg.getId());
        detailCgBook.forEach(detaCg ->{
            detaCg.setListsNull();
            detaCg.setFatherListToNull();
            detaCg.setActive(false);
            detailDailybookContabRepository.save(detaCg);
        });
    }
    
    //Carga los Detalles hacia un Diario Gc
    private void populateChildren(DailybookCg dailybookCg) {
	List<DetailDailybookContab> detailDailybookContabList = new ArrayList<>();
	Iterable<DetailDailybookContab> dailybookContabsDetail = detailDailybookContabRepository.findByDailybookCg(dailybookCg);
        dailybookContabsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCg(null);
            detailDailybookContabList.add(detail);
	});
	dailybookCg.setDetailDailybookContab(detailDailybookContabList);
	dailybookCg.setFatherListToNull();
    }
}