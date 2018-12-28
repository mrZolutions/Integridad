package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.domain.Kardex;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CellarRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarChildRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarRepository;
import com.mrzolution.integridad.app.repositories.KardexRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;
import java.util.ArrayList;
import java.util.List;
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
    
    public Iterable<Cellar> getByUserLazy(UserIntegridad user) {
        log.info("CellarServices getByUserLazy: {}", user.getId());
        Iterable<Cellar> cellars = cellarRepository.findCellarByUserIntegridad(user);
        cellars.forEach (cellar-> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        });
        return cellars;
    }
    
    public Iterable<Cellar> getActivesCellarByWhNumberSeqAndSubsidiaryId(String whNumberSeq, UUID subId) {
        log.info("CellarServices getActivesCellarByWhNumberSeqAndSubsidiaryId: {}, {}", whNumberSeq, subId);
        Iterable<Cellar> cellars = cellarRepository.findActivesCellarByWhNumberSeqAndSubsidiaryId(whNumberSeq, subId);
        cellars.forEach (cellar -> {
            cellar.setFatherListToNull();
            cellar.setListsNull();
        });
        return cellars;
    }
    
    public Iterable<Cellar> getActivesCellarByWhNumberSeqAndUserClientId(String whNumberSeq, UUID userClientId) {
        log.info("CellarServices getActivesCellarByWhNumberSeqAndUserClientId: {}, {}", whNumberSeq, userClientId);
        Iterable<Cellar> cellars = cellarRepository.findActivesCellarByWhNumberSeqAndUserClientId(whNumberSeq, userClientId);
        cellars.forEach (cellar -> {
            cellar.setFatherListToNull();
            cellar.setListsNull();
        });
        return cellars;
    }
    
    public Cellar getCellarById(UUID id) {
        log.info("CellarServices getById: {}", id);
        Cellar retrieved = cellarRepository.findOne(id);
        if (retrieved != null) {
            log.info("CellarServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("CellarServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        return retrieved;
    }
    
    public Cellar deactivateCellar(Cellar cellar) throws BadRequestException {
        if (cellar.getId() == null) {
            throw new BadRequestException("Invalid Cellar");
        }
        Cellar cellarToDeactivate = cellarRepository.findOne(cellar.getId());
        cellarToDeactivate.setListsNull();
        cellarToDeactivate.setActive(false);
        cellarRepository.save(cellarToDeactivate);
        return cellarToDeactivate;
    }
    
    @Async("asyncExecutor")
    public Cellar create(Cellar cellar) throws BadRequestException {
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
        updateCashier(cellar);
        saveDetailsCellar(saved, detailsCellar);
        saveKardex(saved, detailsKardex);
        if ("INGRESADO".equals(saved.getStatusIngreso())) {
            updateProductBySubsidiary(cellar, detailsCellar);
        }
        log.info("CellarServices Cellar created id: {}", saved.getId());
        return saved;
    }
    
    public void updateCashier(Cellar cellar) {
        Cashier cashier = cashierRepository.findOne(cellar.getUserIntegridad().getCashier().getId());
        cashier.setWhNumberSeq(cashier.getWhNumberSeq() + 1);
        cashierRepository.save(cashier);
        log.info("CellarServices updateCashier DONE");
    }
    
    public void saveDetailsCellar(Cellar saved, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach (detail -> {
            detail.setCellar(saved);
            detailCellarRepository.save(detail);
            detail.setCellar(null);
        });
        saved.setDetailsCellar(detailsCellar);
        log.info("CellarServices saveDetailsCellar DONE");
    }
    
    public void saveKardex(Cellar saved, List<Kardex> detailsKardex) {
        detailsKardex.forEach (detail -> {
            detail.setCellar(saved);
            kardexRepository.save(detail);
            detail.setCellar(null);
        });
        saved.setDetailsKardex(detailsKardex);
        log.info("CellarServices saveKardex DONE");
    }
    
    public void updateProductBySubsidiary(Cellar cellar, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach (detail-> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(cellar.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() + detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("CellarServices updateProductBySubsidiary DONE");
    }
    
    private void populateChildren(Cellar cellar) {
        List<DetailCellar> detailCellarList = getDetailsByCellar(cellar);
        cellar.setDetailsCellar(detailCellarList);
        cellar.setFatherListToNull();
        log.info("CellarServices populateChildren cellarId: {}", cellar.getId());
    }
    
    private List<DetailCellar> getDetailsByCellar(Cellar cellar) {
        List<DetailCellar> detailCellarList = new ArrayList<>();
        Iterable<DetailCellar> detailsCellar = detailCellarRepository.findByCellar(cellar);
        detailsCellar.forEach (detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setCellar(null);
            detailCellarList.add(detail);
        });
        return detailCellarList;
    }
}
