package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.SwimmingPool;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.SwimmingPoolRepository;
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
public class SwimmingPoolServices {
    @Autowired
    SwimmingPoolRepository swimmingPoolRepository;
    
    //Bucar SwimmingPool por ID
    public SwimmingPool getSwimmPoolById(UUID id) {
        SwimmingPool retrieved = swimmingPoolRepository.findOne(id);
        populateChildren(retrieved);
        log.info("SwimmingPoolServices getSwimmPoolById: {}", id);
        return retrieved;
    }
    
    public Iterable<SwimmingPool> getSwimmPoolActivesBySubIdAndDates(UUID subId, long dateOne, long dateTwo) {
        Iterable<SwimmingPool> swimmingPools = swimmingPoolRepository.findSwimmPoolActivesBySubIdAndDates(subId, dateOne, dateTwo);
        swimmingPools.forEach(swimm -> {
            swimm.setFatherListToNull();
        });
        return swimmingPools;
    }
    
    public SwimmingPool getSwimmPoolBySubIdAndBarCodeActive(UUID subId, String barCode) throws BadRequestException {
        SwimmingPool swimmingPool = swimmingPoolRepository.findSwimmPoolBySubIdAndBarCodeActive(subId, barCode);
        if (swimmingPool == null) {
            throw new BadRequestException("TICKET ANULADO");
        } else {
            populateChildren(swimmingPool);
        }
        log.info("SwimmingPoolServices getSwimmPoolByBarCodeActive: {}", barCode);
        return swimmingPool;
    }
    
    public SwimmingPool getSwimmPoolBySubIdAndBarCodeAll(UUID subId, String barCode) {
        SwimmingPool swimmingPool = swimmingPoolRepository.findSwimmPoolBySubIdAndBarCodeAll(subId, barCode);
        populateChildren(swimmingPool);
        log.info("SwimmingPoolServices getSwimmPoolByBarCode: {}", barCode);
        return swimmingPool;
    }
    
    public SwimmingPool createSwimmPool(SwimmingPool swimmPool) {
        swimmPool.setActive(true);
        SwimmingPool saved = swimmingPoolRepository.save(swimmPool);
        log.info("SwimmingPoolServices createSwimmPool: {}, {}", saved.getId(), saved.getStringSeq());
        return saved;
    }
    
    public SwimmingPool validateSwimmPool(UUID subId, String barCode) {
        SwimmingPool swimmToValidate = swimmingPoolRepository.findSwimmPoolBySubIdAndBarCodeActive(subId, barCode);
        swimmToValidate.setFatherListToNull();
        swimmToValidate.setStatus("TICKET USADO -- VALIDADO");
        swimmingPoolRepository.save(swimmToValidate);
        log.info("SwimmingPoolServices deactivateSwimmPool: {}", swimmToValidate.getStringSeq());
        return swimmToValidate;
    }
    
    public SwimmingPool deactivateSwimmPool(SwimmingPool swimmPool) throws BadRequestException {
        if (swimmPool.getId() == null) {
            throw new BadRequestException("Invalid Ticket");
        }
        SwimmingPool swimmToDeactivate = swimmingPoolRepository.findOne(swimmPool.getId());
        swimmToDeactivate.setFatherListToNull();
        swimmToDeactivate.setActive(false);
        swimmToDeactivate.setStatus("TICKET ANULADO");
        swimmingPoolRepository.save(swimmToDeactivate);
        return swimmToDeactivate;
    }
    
    private void populateChildren(SwimmingPool swimmPool) {
        swimmPool.setFatherListToNull();
    }
}