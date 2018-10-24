package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CCResumenReport;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PagoRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class PaymentServices {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    CreditsRepository creditsRepository;
    
    UUID idCredit;
    double nume = 0.0;
    double abono = 0.0;
    String statusCambio;
    double resto = 0.0;
       
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
        nume = cambio.getValor();
        resto = nume - abono;
        cambio.setValor(resto);
        if (resto <= 0.0){
            cambio.setStatusCredits(statusCambio);
        }
        creditsRepository.save(cambio);
        log.info("PaymentServices Credits updated");
    };
    
    public List<CCResumenReport> getPaymentsByUserClientId(UUID id){
        log.info("PaymentServices getPaymentsByUserClientI: {}", id);
        Iterable<Payment> payments = paymentRepository.findPaymentsByUserClientId(id);
        List<CCResumenReport> ccResumenReportList = new ArrayList<>();
        
        payments.forEach(payment -> {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechPago = dateFormat.format(new Date(payment.getDatePayment()));
            Double totalTotal = new Double(0);
            totalTotal += payment.getValor();
            CCResumenReport ccReport = new CCResumenReport(payment.getCredits().getPago().getBill().getClient().getIdentification(), payment.getCredits().getPago().getBill().getClient().getName(),
                                                           payment.getModePayment(), payment.getDetail(), fechPago, payment.getDocumentNumber(), totalTotal);
            ccResumenReportList.add(ccReport);
        });
        return ccResumenReportList;
    };
}
