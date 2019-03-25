package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DetailDailybookCg;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCgRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookCgRepository;
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
public class DailybookCgServices {
    @Autowired
    DailybookCgRepository dailybookCgRepository;
    @Autowired
    DetailDailybookCgRepository detailDailybookCgRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona Diario CG por Id
    public DailybookCg getDailybookCgById(UUID id) {
        log.info("DailybookCgServices getDailybookCgById: {}", id);
        DailybookCg retrieved = dailybookCgRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCgServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCgServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }
    
    //Selecciona todos los Diarios CG por UserClientId
    public Iterable<DailybookCg> getDailybookCgByUserClientId(UUID userClientId) {
        log.info("DailybookCgServices getDailybookCgByUserClientId: {}", userClientId);
        Iterable<DailybookCg> dailys = dailybookCgRepository.findDailybookCgByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Creación de los Diarios CG
    public DailybookCg createDailybookCg(DailybookCg dailybookCg) throws BadRequestException {
        List<DetailDailybookCg> detailDailybookCg = dailybookCg.getDetailDailybookCg();
        
        if (detailDailybookCg == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCg.setActive(true);
        dailybookCg.setDetailDailybookCg(null);
        dailybookCg.setFatherListToNull();
        dailybookCg.setListsNull();
        DailybookCg saved = dailybookCgRepository.save(dailybookCg);
        
        Cashier cashier = cashierRepository.findOne(dailybookCg.getUserIntegridad().getCashier().getId());
        cashier.setDailyCgNumberSeq(cashier.getDailyCgNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookCg.forEach(detail -> {
            detail.setDailybookCg(saved);
            detailDailybookCgRepository.save(detail);
            detail.setDailybookCg(null);
        });
        
        saved.setDetailDailybookCg(detailDailybookCg);
        log.info("DailybookCgServices createDailybookCg DONE id: {}", saved.getId());
        return saved;
    }
    
    //Desactivación o Anulación de los Diarios CG
    public DailybookCg deactivateDailybookCg(DailybookCg dailybookCg) throws BadRequestException {
        if (dailybookCg.getId() == null) {
            throw new BadRequestException("Invalid DailybookCg");
        }
        DailybookCg dailybookCgToDeactivate = dailybookCgRepository.findOne(dailybookCg.getId());
        dailybookCgToDeactivate.setListsNull();
        dailybookCgToDeactivate.setActive(false);
        dailybookCgRepository.save(dailybookCgToDeactivate);
        log.info("DailybookCgServices deactivateDailybookCg DONE id: {}", dailybookCgToDeactivate.getId());
        return dailybookCgToDeactivate;
    }
    
    //Carga los Detalles hacia un Diario Gc
    private void populateChildren(DailybookCg dailybookCg) {
	List<DetailDailybookCg> detailDailybookCgList = new ArrayList<>();
	Iterable<DetailDailybookCg> dailybookCgsDetail = detailDailybookCgRepository.findByDailybookCg(dailybookCg);
        dailybookCgsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCg(null);
            detailDailybookCgList.add(detail);
	});
	dailybookCg.setDetailDailybookCg(detailDailybookCgList);
	dailybookCg.setFatherListToNull();
    }
}