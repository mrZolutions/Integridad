package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.DetailConsumption;
import com.mrzolution.integridad.app.domain.UserIntegridad;
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
        consumptions.forEach (consump -> {
            consump.setListsNull();
            consump.setFatherListToNull();
        });
        return consumptions;
    }
    
    public Consumption getConsumptionById(UUID id) {
        log.info("ConsumptionServices getById: {}", id);
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
        consumption.setDetailsConsumption(detailConsumptionList);
        consumption.setFatherListToNull();
        log.info("CellarServices populateChildren cellarId: {}", consumption.getId());
    }
    
    private List<DetailConsumption> getDetailsByConsumption(Consumption consumption) {
        List<DetailConsumption> detailConsumptionList = new ArrayList<>();
        Iterable<DetailConsumption> detailsCellar = detailConsumptionRepository.findByConsumption(consumption);
        detailsCellar.forEach (detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setConsumption(null);
            detailConsumptionList.add(detail);
        });
        return detailConsumptionList;
    }
}
