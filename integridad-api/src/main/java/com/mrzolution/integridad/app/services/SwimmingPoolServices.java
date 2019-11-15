package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.SwimmingPool;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.SwimmingPoolRepository;
import java.util.Date;
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
public class SwimmingPoolServices {
    @Autowired
    SwimmingPoolRepository swimmingPoolRepository;
    
    //Bucar SwimmingPool por ID
    public SwimmingPool getSwimmPoolById(UUID id) {
        SwimmingPool retrieved = swimmingPoolRepository.findOne(id);
        log.info("SwimmingPoolServices getSwimmPoolById: {}", id);
        return retrieved;
    }
    
    public Iterable<SwimmingPool> getSwimmPoolByClientId(UUID id) {
        Iterable<SwimmingPool> swimmingPool = swimmingPoolRepository.findSwimmPoolByClientId(id);
        log.info("SwimmingPoolServices getSwimmPoolByClientId: {}", id);
        return swimmingPool;
    }
    
    public SwimmingPool getSwimmPoolBySubIdAndBarCodeActive(UUID subId, String barCode) {
        SwimmingPool swimm = swimmingPoolRepository.findSwimmPoolBySubIdAndBarCodeActive(subId, barCode);
        log.info("SwimmingPoolServices getSwimmPoolByBarCodeActive: {}", barCode);
        return swimm;
    }
    
    public SwimmingPool getSwimmPoolBySubIdAndBarCodeAll(UUID subId, String barCode) {
        SwimmingPool swimm = swimmingPoolRepository.findSwimmPoolBySubIdAndBarCodeAll(subId, barCode);
        log.info("SwimmingPoolServices getSwimmPoolByBarCode: {}", barCode);
        return swimm;
    }
    
    public SwimmingPool createSwimmPool(SwimmingPool swimmPool) {
        swimmPool.setActive(true);
        SwimmingPool saved = swimmingPoolRepository.save(swimmPool);
        log.info("SwimmingPoolServices createSwimmPool: {}, {}", saved.getId(), saved.getStringSeq());
        return saved;
    }
    
    @Async("asyncExecutor")
    public SwimmingPool deactivateSwimmPool(SwimmingPool swimmPool) throws BadRequestException {
        if (swimmPool == null) {
            throw new BadRequestException("Invalid Id");
        }
        SwimmingPool swimmToDeactivate = swimmingPoolRepository.findOne(swimmPool.getId());
        swimmToDeactivate.setActive(false);
        swimmingPoolRepository.save(swimmToDeactivate);
        log.info("SwimmingPoolServices deactivateSwimmPool: {}, {}", swimmToDeactivate.getId(), swimmToDeactivate.getStringSeq());
        return swimmToDeactivate;
    }

}