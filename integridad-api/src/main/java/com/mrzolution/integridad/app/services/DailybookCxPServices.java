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
import org.springframework.scheduling.annotation.Async;

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
        DailybookCxP retrieved = dailybookCxPRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCxPServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCxPServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        log.info("DailybookCppServices getDailybookCppById: {}", id);
        return retrieved;
    }
    
    //Selecciona todos los Diarios CxP por UserClientId
    public Iterable<DailybookCxP> getDailybookCxPByUserClientId(UUID userClientId) {
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookCppServices getDailybookCppByUserClientId: {}", userClientId);
        return dailys;
    }
    
    //Selecciona todos los Diarios CxP por ProviderId
    public Iterable<DailybookCxP> getDailybookCxPByProviderId(UUID id) {
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByProviderId(id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookCxPServices getDailybookCppByProviderId: {}", id);
        return dailys;
    }
    
    //Busca Diarios CxP por Proveedor y Nro. Factura
    public Iterable<DailybookCxP> getDailybookCxPByUserClientIdAndProvIdAndBillNumber(UUID userClientId, UUID provId, String billNumber) {
        Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        dailys.forEach(daily -> {
            daily.setFatherListToNull();
            daily.setListsNull();
        });
        log.info("DailybookCxPServices getDailybookCppByProviderId: {}, {}, {}", userClientId, provId, billNumber);
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
            detail.setActive(true);
            detail.setDailybookCxP(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCxP(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCxPServices createDailybookCxP: {}, {}", saved.getId(), saved.getDailycxpStringSeq());
        return saved;
    }
    
    //Creación de los Diarios CxP
    @Async("asyncExecutor")
    public DailybookCxP createDailybookAsinCxP(DailybookCxP dailybookCxP) throws BadRequestException {
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
            detail.setActive(true);
            detail.setDailybookCxP(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCxP(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCxPServices createDailybookAsinCxP: {}, {}", saved.getId(), saved.getDailycxpStringSeq());
        return saved;
    }
    
    //Desactivación o Anulación de los Diarios CxP
    @Async("asyncExecutor")
    public DailybookCxP deactivateDailybookCxP(DailybookCxP dailybookCxP) throws BadRequestException {
        if (dailybookCxP.getId() == null) {
            throw new BadRequestException("Invalid DIARIO DE CUENTAS POR PAGAR");
        }
        DailybookCxP dailybookCxPToDeactivate = dailybookCxPRepository.findOne(dailybookCxP.getId());
        dailybookCxPToDeactivate.setListsNull();
        dailybookCxPToDeactivate.setActive(false);
        dailybookCxPToDeactivate.setGeneralDetail("DIARIO DE CUENTAS POR PAGAR ANULADO");
        DailybookCxP deactCxPSaved = dailybookCxPRepository.save(dailybookCxPToDeactivate);
        updateDetailDailyBookContab(deactCxPSaved);
        log.info("DailybookCxPServices deactivateDailybookCxP: {}", dailybookCxPToDeactivate.getId());
        return dailybookCxPToDeactivate;
    }
    
    public void updateDetailDailyBookContab (DailybookCxP dailybookCxP) {
        Iterable<DetailDailybookContab> detailCxPBook = detailDailybookContabRepository.findByDailybookCxPId(dailybookCxP.getId());
        detailCxPBook.forEach(detaCxP ->{
            detaCxP.setListsNull();
            detaCxP.setFatherListToNull();
            detaCxP.setActive(false);
            detailDailybookContabRepository.save(detaCxP);
        });
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