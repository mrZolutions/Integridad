package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCxP;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mrzolution.integridad.app.repositories.DailybookCxPRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class DailybookCxPServices {
    @Autowired
    DailybookCxPRepository dailybookCxPRepository;
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona Diario CxP por Id
    public DailybookCxP getDailybookCxPById(UUID id) {
        log.info("DailybookCppServices getDailybookCppById: {}", id);
        DailybookCxP retrieved = dailybookCxPRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCxPServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCxPServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }
    
    //Selecciona todos los Diarios CxP por UserClientId
    public Iterable<DailybookCxP> getDailybookCxPByUserClientId(UUID userClientId) {
        log.info("DailybookCppServices getDailybookCppByUserClientId: {}", userClientId);
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los Diarios CxP por ProviderId
    public Iterable<DailybookCxP> getDailybookCxPByProviderId(UUID Id) {
        log.info("DailybookCxPServices getDailybookCppByProviderId: {}", Id);
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByProviderId(Id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Busca Diarios CxP por Proveedor y Nro. Factura
    public Iterable<DailybookCxP> getDailybookCxPByUserClientIdAndProvIdAndBillNumber(UUID userClientId, UUID provId, String billNumber) {
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        dailys.forEach(daily -> {
            daily.setFatherListToNull();
            daily.setListsNull();
        });
        return dailys;
    }
    
    //Creación de los Diarios CxP
    public DailybookCxP createDailybookCxP(DailybookCxP dailybookCxP) throws BadRequestException {
        List<DetailDailybookContab> detailDailybookContab = dailybookCxP.getDetailDailybookContab();
        
        if (detailDailybookContab == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCxP.setActive(true);
        dailybookCxP.setDetailDailybookContab(null);
        dailybookCxP.setFatherListToNull();
        dailybookCxP.setListsNull();
        DailybookCxP saved = dailybookCxPRepository.save(dailybookCxP);
        
        Cashier cashier = cashierRepository.findOne(dailybookCxP.getUserIntegridad().getCashier().getId());
        cashier.setDailyCppNumberSeq(cashier.getDailyCppNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookContab.forEach(detail -> {
            detail.setDailybookCxP(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCxP(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCxPServices createDailybookCxP DONE id: {}", saved.getId());
        return saved;
    }
    
    //Desactivación o Anulación de los Diarios CxP
    public DailybookCxP deactivateDailybookCxP(DailybookCxP dailybookCxP) throws BadRequestException {
        if (dailybookCxP.getId() == null) {
            throw new BadRequestException("Invalid DailybookCxP");
        }
        DailybookCxP dailybookCxPToDeactivate = dailybookCxPRepository.findOne(dailybookCxP.getId());
        dailybookCxPToDeactivate.setListsNull();
        dailybookCxPToDeactivate.setActive(false);
        dailybookCxPRepository.save(dailybookCxPToDeactivate);
        log.info("DailybookCxPServices deactivateDailybookCxP DONE id: {}", dailybookCxPToDeactivate.getId());
        return dailybookCxPToDeactivate;
    }
    
    //Carga los Detalles hacia un Diario CxP
    private void populateChildren(DailybookCxP dailybookCxP) {
	List<DetailDailybookContab> detailDailybookContabList = new ArrayList<>();
	Iterable<DetailDailybookContab> dailybookCxPDetail = detailDailybookContabRepository.findByDailybookCxP(dailybookCxP);
        dailybookCxPDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCxP(null);
            detailDailybookContabList.add(detail);
	});
	dailybookCxP.setDetailDailybookContab(detailDailybookContabList);
	dailybookCxP.setFatherListToNull();
    }
}