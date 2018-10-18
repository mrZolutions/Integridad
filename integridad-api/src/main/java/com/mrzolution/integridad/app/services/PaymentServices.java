package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PagoRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
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
public class PaymentServices {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    CreditsRepository creditsRepository;
    
    UUID idCredit;
    Double abono = 0.0;
    String statusCambio;
       
    public Payment create(Payment payment){
        log.info("PaymentServices preparing for create");
        Payment saved = paymentRepository.save(payment);
        log.info("PaymentServices Payment created id: {}", saved.getId());
        if (saved.getCredits().getId() != null){
            idCredit = saved.getCredits().getId();
            abono = saved.getValor();
            statusCambio = "PAGADO";
            updateCredits(idCredit);
        }
	return saved;
    };
    
    @Async
    public void updateCredits(UUID credits){
        log.info("PaymentServices updating Credits");
        Credits cambio = creditsRepository.findOne(idCredit);
        cambio.setValor((cambio.getValor() - abono));
        if (cambio.getValor() <= 0){
            cambio.setStatusCredits(statusCambio);
        }
        creditsRepository.save(cambio);
    };
}
