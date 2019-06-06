package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookFv;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookFvRepository;
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
public class DailybookFvServices {
    @Autowired
    DailybookFvRepository dailybookFvRepository;
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona COMPROBANTE DE FACTURACIÓN-VENTA por Id
    public DailybookFv getDailybookFvById(UUID id) {
        DailybookFv retrieved = dailybookFvRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookFvServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookFvServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        log.info("DailybookFvServices getDailybookFvById DONE: {}", id);
        return retrieved;
    }
    
    //Selecciona todos los COMPROBANTE DE FACTURACIÓN-VENTA por UserClientId
    public Iterable<DailybookFv> getDailybookFvByUserClientId(UUID userClientId) {
        Iterable<DailybookFv> dailys = dailybookFvRepository.findDailybookFvByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookFvServices getDailybookFvByUserClientId DONE: {}", userClientId);
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTE DE FACTURACIÓN-VENTA por UserClientId Sin Id de Cliente
    public Iterable<DailybookFv> getDailybookFvByUserClientIdWithNoClient(UUID userClientId) {
        Iterable<DailybookFv> dailys = dailybookFvRepository.findDailybookFvByUserClientIdWithNoClient(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookFvServices getDailybookFvByUserClientIdWithNoClient DONE: {}", userClientId);
        return dailys;
    }
    
    //Selecciona todos los COMPROBANTE DE FACTURACIÓN-VENTA por Cliente
    public Iterable<DailybookFv> getDailybookFvByClientId(UUID id) {
        Iterable<DailybookFv> dailys = dailybookFvRepository.findDailybookFvByClientId(id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        log.info("DailybookFvServices getDailybookFvByClientId DONE: {}", id);
        return dailys;
    }
    
    //Busca COMPROBANTE DE FACTURACIÓN-VENTA por UserClient, Cliente y Nro. Factura
    public Iterable<DailybookFv> getDailybookFvByUserClientIdAndClientIdAndBillNumber(UUID userClientId, UUID clientId, String billNumber) {
        Iterable<DailybookFv> dailys = dailybookFvRepository.findDailybookFvByUserClientIdAndClientIdAndBillNumber(userClientId, clientId, billNumber);
        dailys.forEach(daily -> {
            daily.setFatherListToNull();
            daily.setListsNull();
        });
        log.info("DailybookFvServices getDailybookFvByUserClientIdAndClientIdAndBillNumber DONE: {}, {}, {}", userClientId, clientId, billNumber);
        return dailys;
    }
    
    //Creación de los COMPROBANTE DE FACTURACIÓN-VENTA
    public DailybookFv createDailybookFv(DailybookFv dailybookFv) throws BadRequestException {
        List<DetailDailybookContab> detailDailybookContab = dailybookFv.getDetailDailybookContab();
        if (detailDailybookContab == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        dailybookFv.setActive(true);
        dailybookFv.setDetailDailybookContab(null);
        dailybookFv.setFatherListToNull();
        dailybookFv.setListsNull();
        DailybookFv saved = dailybookFvRepository.save(dailybookFv);
        
        Cashier cashier = cashierRepository.findOne(dailybookFv.getUserIntegridad().getCashier().getId());
        cashier.setDailyFvNumberSeq(cashier.getDailyFvNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookContab.forEach(detail -> {
            detail.setDailybookFv(saved);
            detailDailybookContabRepository.save(detail);
            detail.setDailybookFv(null);
        });
        
        saved.setDetailDailybookContab(detailDailybookContab);
        log.info("DailybookFvServices createDailybookFv DONE: {}, {}", saved.getId(), saved.getDailyFvStringSeq());
        return saved;
    }
    
    //Desactivación o Anulación de los COMPROBANTE DE FACTURACIÓN-VENTA
    public DailybookFv deactivateDailybookFv(DailybookFv dailybookFv) throws BadRequestException {
        if (dailybookFv.getId() == null) {
            throw new BadRequestException("Invalid DailybookFv");
        }
        DailybookFv dailybookFvToDeactivate = dailybookFvRepository.findOne(dailybookFv.getId());
        dailybookFvToDeactivate.setListsNull();
        dailybookFvToDeactivate.setActive(false);
        dailybookFvToDeactivate.setGeneralDetail("COMPROBANTE DE FACTURACIÓN-VENTA ANULADO");
        dailybookFvRepository.save(dailybookFvToDeactivate);
        log.info("DailybookFvServices deactivateDailybookFv DONE id: {}", dailybookFvToDeactivate.getId());
        return dailybookFvToDeactivate;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE FACTURACIÓN-VENTA
    private void populateChildren(DailybookFv dailybookFv) {
	List<DetailDailybookContab> detailDailybookContabList = new ArrayList<>();
	Iterable<DetailDailybookContab> dailybookContabsDetail = detailDailybookContabRepository.findByDailybookFv(dailybookFv);
        dailybookContabsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookFv(null);
            detailDailybookContabList.add(detail);
	});
	dailybookFv.setDetailDailybookContab(detailDailybookContabList);
	dailybookFv.setFatherListToNull();
    }
}