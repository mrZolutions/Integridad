package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
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
public class CreditsDebtsServices {
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    
    public Iterable<CreditsDebts> getCreditsDebtsOfDebtsToPayByDebtsToPayId(UUID id) {
        log.info("CreditsDebtsServices getCreditsDebtsOfDebtsToPayByDebtsToPayId: {}", id);
        Iterable<CreditsDebts> creditsDebts = creditsDebtsRepository.findCreditsDebtsOfDebtsToPayByDebtsToPayId(id);
        creditsDebts.forEach (creditDebt -> {
            creditDebt.setListsNull();
            creditDebt.setFatherListToNull();
        });
        return creditsDebts;
    }
    
    private void populateChildren(CreditsDebts creditsDebts) {
        List<PaymentDebts> paymentDebtsList = new ArrayList<>();
        Iterable<PaymentDebts> paymentDebts = paymentDebtsRepository.findByCreditsDebts(creditsDebts);
        paymentDebts.forEach (paymentDebt -> {
            paymentDebt.setListsNull();
            paymentDebt.setFatherListToNull();
            paymentDebt.setCreditsDebts(null);          
            paymentDebtsList.add(paymentDebt);
        });
        creditsDebts.setPaymentDebts(paymentDebtsList);
        creditsDebts.setFatherListToNull();
    }
    
}
