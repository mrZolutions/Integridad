package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCi;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCiRepository;
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
public class DailybookCiServices {
    @Autowired
    DailybookCiRepository dailybookCiRepository;
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona COMPROBANTE DE INGRESO por Id
    public DailybookCi getDailybookCiById(UUID id) {
        log.info("DailybookCiServices getDailybookCiById: {}", id);
        DailybookCi retrieved = dailybookCiRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCiServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCiServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }
    
    //Selecciona todos los COMPROBANTES DE INGRESO por UserClientId
    public Iterable<DailybookCi> getDailybookCiByUserClientId(UUID userClientId) {
        log.info("DailybookCiServices getDailybookCiByUserClientId: {}", userClientId);
        Iterable<DailybookCi> dailys = dailybookCiRepository.findDailybookCiByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTES DE INGRESO por UserClientId Sin Id de Provveedor
    public Iterable<DailybookCi> getDailybookCiByUserClientIdWithNoProvider(UUID userClientId) {
        log.info("DailybookCiServices getDailybookCiByUserClientIdWithNoProvider: {}", userClientId);
        Iterable<DailybookCi> dailys = dailybookCiRepository.findDailybookCiByUserClientIdWithNoProvider(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTES DE INGRESO por ProviderId
    public Iterable<DailybookCi> getDailybookCiByProviderId(UUID Id) {
        log.info("DailybookCiServices getDailybookCiByProviderId: {}", Id);
        Iterable<DailybookCi> dailys = dailybookCiRepository.findDailybookCiByProviderId(Id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Busca COMPROBANTES DE INGRESO por UserClient, Proveedor y Nro. Factura
    public Iterable<DailybookCi> getDailybookCiByUserClientIdAndProvIdAndBillNumber(UUID userClientId, UUID provId, String billNumber) {
        Iterable<DailybookCi> dailys = dailybookCiRepository.findDailybookCiByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        dailys.forEach(daily -> {
            daily.setFatherListToNull();
            daily.setListsNull();
        });
        return dailys;
    }
    
    //Creación de los COMPROBANTES DE INGRESO
    public DailybookCi createDailybookCi(DailybookCi dailybookCi) throws BadRequestException {
        List<DetailDailybookContab> detailDailybookContab = dailybookCi.getDetailDailybookContab();
        
        if (detailDailybookContab == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCi.setActive(true);
        dailybookCi.setDetailDailybookContab(null);
        dailybookCi.setFatherListToNull();
        dailybookCi.setListsNull();
        DailybookCi saved = dailybookCiRepository.save(dailybookCi);
        
        Cashier cashier = cashierRepository.findOne(dailybookCi.getUserIntegridad().getCashier().getId());
        cashier.setDailyCiNumberSeq(cashier.getDailyCiNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookContab.forEach(detail -> {
            detail.setDailybookCi(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookCe(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookCiServices createDailybookCi DONE id: {}", saved.getId());
        //System.out.print(saved);
        return saved;
    }
    
    //Desactivación o Anulación de los COMPROBANTES DE INGRESO
    public DailybookCi deactivateDailybookCi(DailybookCi dailybookCi) throws BadRequestException {
        if (dailybookCi.getId() == null) {
            throw new BadRequestException("Invalid DailybookCi");
        }
        DailybookCi dailybookCiToDeactivate = dailybookCiRepository.findOne(dailybookCi.getId());
        dailybookCiToDeactivate.setListsNull();
        dailybookCiToDeactivate.setActive(false);
        dailybookCiToDeactivate.setGeneralDetail("COMPROBANTE DE INGRESO ANULADO");
        dailybookCiRepository.save(dailybookCiToDeactivate);
        log.info("DailybookCiServices deactivateDailybookCi DONE id: {}", dailybookCiToDeactivate.getId());
        return dailybookCiToDeactivate;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE INGRESO
    private void populateChildren(DailybookCi dailybookCi) {
	List<DetailDailybookContab> detailDailybookContabList = new ArrayList<>();
	Iterable<DetailDailybookContab> dailybookContabsDetail = detailDailybookContabRepository.findByDailybookCi(dailybookCi);
        dailybookContabsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCe(null);
            detailDailybookContabList.add(detail);
	});
	dailybookCi.setDetailDailybookContab(detailDailybookContabList);
	dailybookCi.setFatherListToNull();
    }
}