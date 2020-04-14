package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.ComprobantePago;
import com.mrzolution.integridad.app.domain.DetailComprobantePago;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.ComprobantePagoRepository;
import com.mrzolution.integridad.app.repositories.DetailComprobantePagoRepository;
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
public class ComprobantePagoServices {
    @Autowired
    ComprobantePagoRepository comprobantePagoRepository;
    @Autowired
    DetailComprobantePagoRepository detailComprobantePagoRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona COMPROBANTE DE PAGO por Id
    public ComprobantePago getComprobantePagoById(UUID id) {
        ComprobantePago comprobante = comprobantePagoRepository.findOne(id);
        populateChildren(comprobante);
        log.info("ComprobantePagoServices getComprobantePagoById: {}", id);
        return comprobante;
    }
    
    //Selecciona todos los COMPROBANTES DE PAGO por UserClientId
    public Iterable<ComprobantePago> getComprobantePagoByUserClientId(UUID userClientId) {
        Iterable<ComprobantePago> comprobantes = comprobantePagoRepository.findComprobantePagoByUserClientId(userClientId);
        comprobantes.forEach(comprobante -> {
            comprobante.setListsNull();
            comprobante.setFatherListToNull();
        });
        log.info("ComprobantePagoServices getComprobantePagoByUserClientId: {}", userClientId);
        return comprobantes;
    }

    //Selecciona todos los COMPROBANTES DE PAGO por PaymentId
    public ComprobantePago getComprobantePagoByPaymentId(String paymentId) {
        ComprobantePago comprobante = comprobantePagoRepository.findByPaymentId(paymentId);
        if(comprobante != null){
            populateChildren(comprobante);
        }
        log.info("ComprobantePagoServices getComprobantePagoByUserClientId: {}", paymentId);
        return comprobante;
    }
    
    //Selecciona todos los COMPROBANTES DE PAGO por Proveedor
    public Iterable<ComprobantePago> getComprobantePagoByProviderId(UUID id) {
        Iterable<ComprobantePago> comprobantes = comprobantePagoRepository.findComprobantePagoByProviderId(id);
        comprobantes.forEach(comprobante -> {
            comprobante.setListsNull();
            comprobante.setFatherListToNull();
        });
        log.info("ComprobantePagoServices getComprobantePagoByProviderId: {}", id);
        return comprobantes;
    }
    
    //Creación de los COMPROBANTES DE PAGO
    @Async("asyncExecutor")
    public ComprobantePago createComprobantePago(ComprobantePago comprobantePago) throws BadRequestException {
        List<DetailComprobantePago> detailComprobantePago = comprobantePago.getDetailComprobantePago();
        
        if (detailComprobantePago == null) {
            throw new BadRequestException("Debe tener una Detalle por lo menos");
        }
        
        comprobantePago.setActive(true);
        comprobantePago.setDetailComprobantePago(null);
        comprobantePago.setFatherListToNull();
        comprobantePago.setListsNull();
        ComprobantePago saved = comprobantePagoRepository.save(comprobantePago);
        
        Cashier cashierCmpPago = cashierRepository.findOne(comprobantePago.getUserIntegridad().getCashier().getId());
        cashierCmpPago.setCompPagoNumberSeq(cashierCmpPago.getCompPagoNumberSeq() + 1);
        cashierRepository.save(cashierCmpPago);
              
        detailComprobantePago.forEach(detail -> {
            detail.setComprobantePago(saved);
            detailComprobantePagoRepository.save(detail);
            detail.setComprobantePago(null);
        });
        
        saved.setDetailComprobantePago(detailComprobantePago);
        log.info("ComprobantePagoServices createComprobantePago: {}, {}", saved.getId(), saved.getComprobanteStringSeq());
        return saved;
    }

    public ComprobantePago createComprobantePagoNoAsync(ComprobantePago comprobantePago) throws BadRequestException {
        List<DetailComprobantePago> detailComprobantePago = comprobantePago.getDetailComprobantePago();

        if (detailComprobantePago == null) {
            throw new BadRequestException("Debe tener una Detalle por lo menos");
        }

        comprobantePago.setActive(true);
        comprobantePago.setDetailComprobantePago(null);
        comprobantePago.setFatherListToNull();
        comprobantePago.setListsNull();
        ComprobantePago saved = comprobantePagoRepository.save(comprobantePago);

        Cashier cashierCmpPago = cashierRepository.findOne(comprobantePago.getUserIntegridad().getCashier().getId());
        cashierCmpPago.setCompPagoNumberSeq(cashierCmpPago.getCompPagoNumberSeq() + 1);
        cashierRepository.save(cashierCmpPago);

        detailComprobantePago.forEach(detail -> {
            detail.setComprobantePago(saved);
            detailComprobantePagoRepository.save(detail);
            detail.setComprobantePago(null);
        });

        saved.setDetailComprobantePago(detailComprobantePago);
        log.info("ComprobantePagoServices createComprobantePago: {}, {}", saved.getId(), saved.getComprobanteStringSeq());
        return saved;
    }
    
    //Desactivación de los Comprobantes de Pago
    @Async("asyncExecutor")
    public ComprobantePago deactivateComprobantePago(ComprobantePago comprobantePago) throws BadRequestException {
        if (comprobantePago.getId() == null) {
            throw new BadRequestException("Invalid ComprobantePago");
        }
        ComprobantePago compPagoToDeactivate = comprobantePagoRepository.findOne(comprobantePago.getId());
        compPagoToDeactivate.setListsNull();
        compPagoToDeactivate.setActive(false);
        compPagoToDeactivate.setComprobanteEstado("ANULADO");
        comprobantePagoRepository.save(compPagoToDeactivate);
        log.info("ComprobantePagoServices deactivateComprobantePago: {}", compPagoToDeactivate.getId());
        return compPagoToDeactivate;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE PAGO
    private void populateChildren(ComprobantePago comprobantePago) {
	List<DetailComprobantePago> detailComprobantePagoList = new ArrayList<>();
	Iterable<DetailComprobantePago> comprobantePagoDetail = detailComprobantePagoRepository.findByComprobantePago(comprobantePago);
        comprobantePagoDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setComprobantePago(null);
            detailComprobantePagoList.add(detail);
	});
	comprobantePago.setDetailComprobantePago(detailComprobantePagoList);
	comprobantePago.setFatherListToNull();
    }
}