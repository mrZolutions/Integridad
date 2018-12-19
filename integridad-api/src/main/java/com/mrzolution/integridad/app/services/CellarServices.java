package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CellarRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarChildRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    public Cellar getById(UUID id) {
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
    
    public Cellar create(Cellar cellar) throws BadRequestException {
        List<DetailCellar> detailCellar = cellar.getDetailCellar();
        if (detailCellar == null) {
            throw new BadRequestException("Debe tener un producto por lo menos");
        }
        cellar.setDetailCellar(null);
        cellar.setFatherListToNull();
        cellar.setListsNull();
        Cellar saved = cellarRepository.save(cellar);
        
        Cashier cashier = cashierRepository.findOne(cellar.getUserIntegridad().getCashier().getId());
        cashier.setWhNumberSeq(cashier.getWhNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailCellar.forEach (detail -> {
            detail.setCellar(saved);
            detailCellarRepository.save(detail);
            detail.setCellar(null);
        });
        saved.setDetailCellar(detailCellar);
        log.info("CellarServices Cellar created id: {}", saved.getId());
        return saved;
    }
    
    private void populateChildren(Cellar cellar) {
        List<DetailCellar> detailCellarList = getDetailsByCellar(cellar);
        cellar.setDetailCellar(detailCellarList);
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
