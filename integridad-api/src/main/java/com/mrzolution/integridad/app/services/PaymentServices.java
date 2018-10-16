package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PagoRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrzolutions-daniel
 */
@Slf4j
@Component
public class PaymentServices {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    CreditsRepository creditsRepository;
    
    public Payment create(Payment payment){
        log.info("PaymentServices create: {}", payment.getId());
        Payment saved = paymentRepository.save(payment);
        log.info("PaymentServices created id: {}", saved.getId());
	return saved;
    }
}
