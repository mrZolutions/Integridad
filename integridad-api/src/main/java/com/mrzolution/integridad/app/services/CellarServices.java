package com.mrzolution.integridad.app.services;

import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.report.CellarEntryReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@Component
public class CellarServices {
    @Autowired
    CellarRepository cellarRepository;
    @Autowired
    DetailCellarRepository detailCellarRepository;
    @Autowired
    DetailCellarChildRepository detailCellarChildRepository;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    UserClientRepository userClientRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    ProductRepository productRepository;
    
    public Iterable<Cellar> getByUserLazy(UserIntegridad user) {
        Iterable<Cellar> cellars = cellarRepository.findCellarByUserIntegridad(user);
        cellars.forEach(cellar-> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        });
        return cellars;
    }
    
    public Cellar getCellarById(UUID id) {
        Cellar retrieved = cellarRepository.findOne(id);		
        populateChildren(retrieved);
        log.info("CellarServices getCellarById: {}", id);
        return retrieved;
    }
       
    //Validate Cellar and updateProductBySubsidiary
    @Async("asyncExecutor")
    public Cellar validateCellar(Cellar cellar) throws BadRequestException {
        if (cellar.getId() == null) {
            throw new BadRequestException("Cellar NOT FOUND");
        }
        Cellar cellarToValidate = cellarRepository.findOne(cellar.getId());
        populateChildren(cellarToValidate);
        cellarToValidate.setStatusIngreso("INGRESADO");
        Cellar saved = cellarRepository.save(cellarToValidate);
        saveDetailsCellar(saved, cellarToValidate.getDetailsCellar());
        updateProductBySubsidiary(cellar, cellarToValidate.getDetailsCellar());
        log.info("CellarServices validateCellar DONE");
        return cellarToValidate;
    }
    
    private void populateChildren(Cellar cellar) {
        List<DetailCellar> detailCellarList = getDetailsByCellar(cellar);
        List<Kardex> detailsKardexList = getDetailsKardexByCellar(cellar);
        cellar.setDetailsCellar(detailCellarList);
        cellar.setDetailsKardex(detailsKardexList);
        cellar.setFatherListToNull();
    }
    
    private List<DetailCellar> getDetailsByCellar(Cellar cellar) {
        List<DetailCellar> detailCellarList = new ArrayList<>();
        Iterable<DetailCellar> detailsCellar = detailCellarRepository.findByCellar(cellar);
        detailsCellar.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setCellar(null);
            detailCellarList.add(detail);
        });
        return detailCellarList;
    }
    
    private List<Kardex> getDetailsKardexByCellar(Cellar cellar) {
        List<Kardex> detailsKardexList = new ArrayList<>();
        Iterable<Kardex> detailsKardex = kardexRepository.findByCellar(cellar);
        detailsKardex.forEach (detail -> {
            detail.getCellar().setListsNull();
            detail.getCellar().setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setCellar(null);
            detailsKardexList.add(detail);
        });
        return detailsKardexList;
    }
    
    //Create Cellar
    @Async("asyncExecutor")
    public Cellar createCellar(Cellar cellar) throws BadRequestException {
        List<DetailCellar> detailsCellar = cellar.getDetailsCellar();
        List<Kardex> detailsKardex = cellar.getDetailsKardex();
        if (detailsCellar == null) {
            throw new BadRequestException("Debe tener un producto por lo menos");
        }
        cellar.setDetailsCellar(null);
        cellar.setActive(true);
        cellar.setFatherListToNull();
        cellar.setListsNull();
        Cellar saved = cellarRepository.save(cellar);
        
        Cashier cashier = cashierRepository.findOne(cellar.getUserIntegridad().getCashier().getId());
        cashier.setWhNumberSeq(cashier.getWhNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        saveDetailsCellar(saved, detailsCellar);
        saveKardex(saved, detailsKardex);
        if ("INGRESADO".equals(saved.getStatusIngreso())) {
            updateProductBySubsidiary(saved, detailsCellar);
        }
        log.info("CellarServices createCellar: {}, {}", saved.getId(), saved.getWhNumberSeq());
        return saved;
    }
    
    public void saveDetailsCellar(Cellar saved, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach(detail -> {
            detail.setCellar(saved);
            detailCellarRepository.save(detail);
            detail.setCellar(null);
        });
        saved.setDetailsCellar(detailsCellar);
    }
    
    public void saveKardex(Cellar saved, List<Kardex> detailsKardex) {
        detailsKardex.forEach(detail -> {
            detail.setActive(true);
            detail.setCellar(saved);
            kardexRepository.save(detail);
            detail.setCellar(null);
        });
        saved.setDetailsKardex(detailsKardex);
    }

    public void updateProductBySubsidiary(Cellar cellar, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(cellar.getSubsidiary().getId(), detail.getProduct().getId());
                if(ps != null){
                    ps.setQuantity(ps.getQuantity() + detail.getQuantity());
                    productBySubsidiairyRepository.save(ps);
                }
            }
        });
        log.info("CellarServices updateProductBySubsidiary DONE");
    }
    
    //Selecciona los Ingresos a Bodega por Proveedor
    public Iterable<Cellar> getCellarsByProviderId(UUID id) {
        Iterable<Cellar> cellars = cellarRepository.findCellarsByProviderId(id);
        cellars.forEach(cellar -> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        });
        log.info("CellarServices getCellarByProviderId: {}", id);
        return cellars;
    }
    
    //Selecciona los Ingresos a Bodega sin Nota de Cŕedito Aplicada por Proveedor
    public Iterable<Cellar> getCellarsByProviderIdAndNoCN(UUID id) {
        Iterable<Cellar> cellars = cellarRepository.findCellarsByProviderIdAndNoCN(id);
        cellars.forEach(cellar -> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        });
        log.info("CellarServices getCellarsByProviderIdAndNoCN: {}", id);
        return cellars;
    }
    
    //Selecciona los Ingresos a Bodega Pendientes por Aprobación
    public Iterable<Cellar> getCellarPendingOfWarehouse(UUID id) {
        Iterable<Cellar> cellars = cellarRepository.findCellarPendingOfWarehouse(id);
        cellars.forEach(cellar -> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        });
        log.info("CellarServices getCellarsPendingByProviderId: {}", id);
        return cellars;
    }
    
    public Iterable<Cellar> getActivesCellarByWhNumberSeqAndSubsidiaryId(String whNumberSeq, UUID subId) {
        Iterable<Cellar> cellars = cellarRepository.findCellarByWhNumberSeqAndSubsidiaryId(whNumberSeq, subId);
        cellars.forEach(cellar -> {
            cellar.setFatherListToNull();
            cellar.setListsNull();
        });
        log.info("CellarServices getActivesCellarByWhNumberSeqAndSubsidiaryId: {}, {}", whNumberSeq, subId);
        return cellars;
    }
    
    public Iterable<Cellar> getActivesCellarByWhNumberSeqAndUserClientId(String whNumberSeq, UUID userClientId) {
        Iterable<Cellar> cellars = cellarRepository.findCellarByWhNumberSeqAndUserClientId(whNumberSeq, userClientId);
        cellars.forEach(cellar -> {
            cellar.setFatherListToNull();
            cellar.setListsNull();
        });
        log.info("CellarServices getActivesCellarByWhNumberSeqAndUserClientId: {}, {}", whNumberSeq, userClientId);
        return cellars;
    }
    
    //Reporte de Ingreso a Bodega
    public List<CellarEntryReport> getByUserClientIdAndDatesActives(UUID userClientId, long dateOne, long dateTwo) {
        log.info("CellarServices getByUserClientIdAndDatesActives: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Cellar> cellars = cellarRepository.findByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
        Set<UUID> productIds = new HashSet<>();
        cellars.forEach(cellar-> {
            populateChildren(cellar);
            for (DetailCellar detail: cellar.getDetailsCellar()) {
                productIds.add(detail.getProduct().getId());
            }
        });	
        return loadListItems(Lists.newArrayList(cellars), productIds);
    }
    
    private List<CellarEntryReport> loadListItems(List<Cellar> cellars, Set<UUID> productIds) {
        List<CellarEntryReport> reportList = new ArrayList<>();
        for (UUID uuidCurrent: productIds) {
            for (Cellar cellar: cellars) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String fechaIngreso = dateFormat.format(new Date(cellar.getDateEnterCellar()));
                String fechaBill = dateFormat.format(new Date(cellar.getDateBill()));
                Double iva = new Double(0);
                Double total = new Double(0);
                for (DetailCellar detail: cellar.getDetailsCellar()) {
                    if (uuidCurrent.equals(detail.getProduct().getId())) {
                        if (detail.getProduct().isIva()) {
                            iva = 0.12;
                            total = 1.12;
                        } else {
                            iva = 0.0;
                            total = 1.0;
                        }
                        CellarEntryReport item = new CellarEntryReport(fechaIngreso, cellar.getWhNumberSeq(), cellar.getProvider().getRazonSocial(), fechaBill,
                                                                        cellar.getBillNumber(), detail.getProduct().getName(), detail.getQuantity(), detail.getCostEach(),
                                                                        (detail.getTotal() * iva), (detail.getTotal() * total));
                        reportList.add(item);
                    }
                }
            }
        }
        return reportList;
    }
}