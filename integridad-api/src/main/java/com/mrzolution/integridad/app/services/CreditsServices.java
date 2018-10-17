package com.mrzolution.integridad.app.services;
/**
 *
 * @author mrzolutions-daniel
 */
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CreditsServices {
    @Autowired
    CreditsRepository creditsRepository;
    
    public Iterable<Credits> getCreditsOfBillByBillId(UUID id){
        log.info("CreditsServices getCreditsOfBillByBillId: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsOfBillByBillId(id);
        credits.forEach(credit ->{
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        return credits;
    };
    
    //@Async
    //public void updatePayment(Credits credits){
    //    if (credits.getId() == null){
    //        throw new BadRequestException("Invalid Credit");
    //    }
    //    log.info ("CreditsServices updating: {}", credits.getId());
    //    Credits creditToUpdate = creditsRepository.findOne(credits.getId());
    //    creditToUpdate.setListsNull();
        
    //};
}
