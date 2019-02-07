package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.DetailConsumption;
import com.mrzolution.integridad.app.domain.Kardex;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.ConsumptionRepository;
import com.mrzolution.integridad.app.repositories.DetailConsumptionChildRepository;
import com.mrzolution.integridad.app.repositories.DetailConsumptionRepository;
import com.mrzolution.integridad.app.repositories.KardexRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
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
public class ConsumptionServices {
    @Autowired
    ConsumptionRepository consumptionRepository;
    @Autowired
    DetailConsumptionRepository detailConsumptionRepository;
    @Autowired
    DetailConsumptionChildRepository detailConsumptionChildRepository;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    KardexRepository kardexRepository;
    
    public Iterable<Consumption> getByUserLazy(UserIntegridad user) {
        log.info("ConsumptionServices getByUserLazy: {}", user.getId());
        Iterable<Consumption> consumptions = consumptionRepository.findConsumptionByUserIntegridad(user);
        consumptions.forEach(consump -> {
            consump.setListsNull();
            consump.setFatherListToNull();
        });
        return consumptions;
    }
    
    public Iterable<Consumption> getConsumptionByClientId(UUID id) {
        log.info("ConsumptionServices getConsumptionByClientId" );
        Iterable<Consumption> consumptions = consumptionRepository.findConsumptionByClientId(id);
        consumptions.forEach(consump -> {
            consump.setListsNull();
            consump.setFatherListToNull();
        });
        return consumptions;
    }
    
    public Consumption getConsumptionById(UUID id) {
        log.info("ConsumptionServices getConsumptionById: {}", id);
        Consumption retrieved = consumptionRepository.findOne(id);
        if (retrieved != null) {
            log.info("ConsumptionServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("ConsumptionServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        return retrieved;
    }
    
    private void populateChildren(Consumption consumption) {
        List<DetailConsumption> detailConsumptionList = getDetailsByConsumption(consumption);
        List<Kardex> detailsKardexList = getDetailsKardexByConsumption(consumption);
        consumption.setDetailsConsumption(detailConsumptionList);
        consumption.setDetailsKardex(detailsKardexList);
        consumption.setFatherListToNull();
    }
    
    private List<DetailConsumption> getDetailsByConsumption(Consumption consumption) {
        List<DetailConsumption> detailConsumptionList = new ArrayList<>();
        Iterable<DetailConsumption> detailsConsumption = detailConsumptionRepository.findByConsumption(consumption);
        detailsConsumption.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setConsumption(null);
            detailConsumptionList.add(detail);
        });
        return detailConsumptionList;
    }
    
    private List<Kardex> getDetailsKardexByConsumption(Consumption consumption) {
        List<Kardex> detailsKardexList = new ArrayList<>();
        Iterable<Kardex> detailsKardex = kardexRepository.findByConsumption(consumption);
        detailsKardex.forEach (detail -> {
            detail.getConsumption().setListsNull();
            detail.getConsumption().setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setConsumption(null);
            detailsKardexList.add(detail);
        });
        return detailsKardexList;
    }
    
    @Async("asyncExecutor")
    public Consumption createConsumption(Consumption consumption) throws BadRequestException {
        List<DetailConsumption> detailsConsumption = consumption.getDetailsConsumption();
        List<Kardex> detailsKardex = consumption.getDetailsKardex();
        if (detailsConsumption == null) {
            throw new BadRequestException("Debe tener un producto por lo menos");
        }
        consumption.setDetailsConsumption(null);
        consumption.setActive(true);
        consumption.setFatherListToNull();
        consumption.setListsNull();
        Consumption saved = consumptionRepository.save(consumption);
        Cashier cashier = cashierRepository.findOne(consumption.getUserIntegridad().getCashier().getId());
        cashier.setCsmNumberSeq(cashier.getCsmNumberSeq() + 1);
        cashierRepository.save(cashier);
        saveDetailsConsumption(saved, detailsConsumption);
        saveKardex(saved, detailsKardex);
        updateProductBySubsidiary(consumption, detailsConsumption);
        log.info("ConsumptionServices createConsumption DONE id: {}", saved.getId());
        return saved;
    }
    
    public void saveDetailsConsumption(Consumption saved, List<DetailConsumption> detailsConsumption) {
        detailsConsumption.forEach(detail -> {
            detail.setConsumption(saved);
            detailConsumptionRepository.save(detail);
            detail.setConsumption(null);
        });
        saved.setDetailsConsumption(detailsConsumption);
        log.info("ConsumptionServices saveDetailsConsumption DONE");
    }
    
    public void saveKardex(Consumption saved, List<Kardex> detailsKardex) {
        detailsKardex.forEach(detail -> {
            detail.setConsumption(saved);
            kardexRepository.save(detail);
            detail.setConsumption(null);
        });
        saved.setDetailsKardex(detailsKardex);
        log.info("ConsumptionServices saveKardex DONE");
    }
    
    public void updateProductBySubsidiary(Consumption consumption, List<DetailConsumption> detailsConsumption) {
        detailsConsumption.forEach(detail-> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(consumption.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() - detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("ConsumptionServices updateProductBySubsidiary DONE");
    }
}
