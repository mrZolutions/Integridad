package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import com.mrzolution.integridad.app.domain.DetailComprobanteCobro;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.ComprobanteCobroRepository;
import com.mrzolution.integridad.app.repositories.DetailComprobanteCobroRepository;
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
public class ComprobanteCobroServices {
    @Autowired
    ComprobanteCobroRepository comprobanteCobroRepository;
    @Autowired
    DetailComprobanteCobroRepository detailComprobanteCobroRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona COMPROBANTE DE COBRO por Id
    public ComprobanteCobro getComprobanteCobroById(UUID id) {
        ComprobanteCobro comprobante = comprobanteCobroRepository.findOne(id);
        if (comprobante != null) {
            log.info("ComprobanteCobroServices retrieved id: {}", comprobante.getId());
        } else {
            log.info("ComprobanteCobroServices retrieved id NULL: {}", id);
	}
        populateChildren(comprobante);
        log.info("ComprobanteCobroServices getComprobanteCobroById: {}", id);
        return comprobante;
    }
    
    //Selecciona todos los COMPROBANTES DE COBRO por UserClientId
    public Iterable<ComprobanteCobro> getComprobanteCobroByUserClientId(UUID userClientId) {
        Iterable<ComprobanteCobro> comprobantes = comprobanteCobroRepository.findComprobanteCobroByUserClientId(userClientId);
        comprobantes.forEach(comprobante -> {
            comprobante.setListsNull();
            comprobante.setFatherListToNull();
        });
        log.info("ComprobanteCobroServices getComprobanteCobroByUserClientId: {}", userClientId);
        return comprobantes;
    }
    
    //Selecciona todos los COMPROBANTES DE COBRO por Cliente
    public Iterable<ComprobanteCobro> getComprobanteCobroByClientId(UUID id) {
        Iterable<ComprobanteCobro> comprobantes = comprobanteCobroRepository.findComprobanteCobroByClientId(id);
        comprobantes.forEach(comprobante -> {
            comprobante.setListsNull();
            comprobante.setFatherListToNull();
        });
        log.info("ComprobanteCobroServices getComprobanteCobroByClientId: {}", id);
        return comprobantes;
    }
    
    //Creación de los COMPROBANTES DE INGRESO
    public ComprobanteCobro createComprobanteCobro(ComprobanteCobro comprobanteCobro) throws BadRequestException {
        List<DetailComprobanteCobro> detailComprobanteCobro = comprobanteCobro.getDetailComprobanteCobro();
        
        if (detailComprobanteCobro == null) {
            throw new BadRequestException("Debe tener una Factura por lo menos");
        }
        
        comprobanteCobro.setActive(true);
        comprobanteCobro.setDetailComprobanteCobro(null);
        comprobanteCobro.setFatherListToNull();
        comprobanteCobro.setListsNull();
        ComprobanteCobro saved = comprobanteCobroRepository.save(comprobanteCobro);
              
        detailComprobanteCobro.forEach(detail -> {
            detail.setComprobanteCobro(saved);
            detailComprobanteCobroRepository.save(detail);
            detail.setComprobanteCobro(null);
        });
        
        Cashier cashierCmpCbro = cashierRepository.findOne(comprobanteCobro.getUserIntegridad().getCashier().getId());
        cashierCmpCbro.setCompCobroNumberSeq(cashierCmpCbro.getCompCobroNumberSeq() + 1);
        cashierRepository.save(cashierCmpCbro);
        
        saved.setDetailComprobanteCobro(detailComprobanteCobro);
        log.info("ComprobanteCobroServices createComprobanteCobro: {}, {}", saved.getId(), saved.getComprobanteStringSeq());
        return saved;
    }
    
    //Carga los Detalles hacia un COMPROBANTE DE COBRO
    private void populateChildren(ComprobanteCobro comprobanteCobro) {
	List<DetailComprobanteCobro> detailComprobanteCobroList = new ArrayList<>();
	Iterable<DetailComprobanteCobro> comprobanteCobroDetail = detailComprobanteCobroRepository.findByComprobanteCobro(comprobanteCobro);
        comprobanteCobroDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setComprobanteCobro(null);
            detailComprobanteCobroList.add(detail);
	});
	comprobanteCobro.setDetailComprobanteCobro(detailComprobanteCobroList);
	comprobanteCobro.setFatherListToNull();
    }
}