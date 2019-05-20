package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCeRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;
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
    DetailDailybookContabRepository detailDailybookContabRepository;
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
        log.info("DailybookCeServices getDailybookCeByUserClientId: {}", userClientId);
        Iterable<DailybookCe> dailys = dailybookCeRepository.findDailybookCeByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTES DE EGRESO por UserClientId Sin Id de Provveedor
    public Iterable<DailybookCe> getDailybookCeByUserClientIdWithNoProvider(UUID userClientId) {
        log.info("DailybookCeServices getDailybookCeByUserClientIdWithNoProvider: {}", userClientId);
        Iterable<DailybookCe> dailys = dailybookCeRepository.findDailybookCeByUserClientIdWithNoProvider(userClientId);
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
    
    //Busca COMPROBANTES DE EGRESO por UserClient, Proveedor y Nro. Factura
    public Iterable<DailybookCe> getDailybookCeByUserClientIdAndProvIdAndBillNumber(UUID userClientId, UUID provId, String billNumber) {
        Iterable<DailybookCe> dailys = dailybookCeRepository.findDailybookCeByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        dailys.forEach(daily -> {
            daily.setFatherListToNull();
            daily.setListsNull();
        });
        return dailys;
    }
    
    //Creación de los COMPROBANTES DE EGRESO
    public DailybookCe createDailybookCe(DailybookCe dailybookCe) throws BadRequestException {
        List<DetailDailybookContab> detailDailybookContab = dailybookCe.getDetailDailybookContab();
        
        if (detailDailybookContab == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCe.setActive(true);
        dailybookCe.setDetailDailybookContab(null);
        dailybookCe.setFatherListToNull();
        dailybookCe.setListsNull();
        DailybookCe saved = dailybookCeRepository.save(dailybookCe);
        
        Cashier cashier = cashierRepository.findOne(dailybookCe.getUserIntegridad().getCashier().getId());
        cashier.setDailyCeNumberSeq(cashier.getDailyCeNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookContab.forEach(detail -> {
            detail.setDailybookCe(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCe(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCeServices createDailybookCe DONE id: {}", saved.getId());
        return saved;
    }
    
    //Desactivación o Anulación de los COMPROBANTES DE EGRESO
    public DailybookCe deactivateDailybookCe(DailybookCe dailybookCe) throws BadRequestException {
        if (dailybookCe.getId() == null) {
            throw new BadRequestException("Invalid DailybookCe");
        }
        DailybookCe dailybookCeToDeactivate = dailybookCeRepository.findOne(dailybookCe.getId());
        dailybookCeToDeactivate.setListsNull();
        dailybookCeToDeactivate.setActive(false);
        dailybookCeToDeactivate.setGeneralDetail("COMPROBANTE DE EGRESO ANULADO");
        dailybookCeRepository.save(dailybookCeToDeactivate);
        log.info("DailybookCeServices deactivateDailybookCe DONE id: {}", dailybookCeToDeactivate.getId());
        return dailybookCeToDeactivate;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE EGRESO
    private void populateChildren(DailybookCe dailybookCe) {
	List<DetailDailybookContab> detailDailybookContabList = new ArrayList<>();
	Iterable<DetailDailybookContab> dailybookContabsDetail = detailDailybookContabRepository.findByDailybookCe(dailybookCe);
        dailybookContabsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCe(null);
            detailDailybookContabList.add(detail);
	});
	dailybookCe.setDetailDailybookContab(detailDailybookContabList);
	dailybookCe.setFatherListToNull();
    }
}