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
            abono = saved.getValorAbono();
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
        if (cambio.getValor() == 0.0000){
            statusCambio = "PAGADO";
            cambio.setStatusCredits(statusCambio);
        }
        creditsRepository.save(cambio);
        resto = 0.0;
        log.info("PaymentServices Credits updated");
    };
    
    public List<CCResumenReport> getPaymentsByUserClientId(UUID id){
        log.info("PaymentServices getPaymentsByUserClientId: {}", id);
        Iterable<Payment> payments = paymentRepository.findAllPaymentsByUserClientId(id);
        List<CCResumenReport> ccResumenReportList = new ArrayList<>();
        
        payments.forEach(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaPago = dateFormat.format(new Date(payment.getDatePayment()));
            Double paySubTotal = new Double(0);
            Double payTotal = new Double(0);
            Double billTotal = new Double(0);
            
            billTotal = payment.getCredits().getPago().getBill().getTotal();
            paySubTotal = payment.getValorAbono() - payment.getValorReten();
            payTotal = billTotal - paySubTotal;
            
            CCResumenReport resumenReport = new CCResumenReport(payment.getCredits().getPago().getBill().getClient().getIdentification(), payment.getCredits().getPago().getBill().getClient().getName(),
                                                                payment.getCredits().getPago().getBill().getStringSeq(), billTotal, payment.getTypePayment(), payment.getModePayment(), fechaPago,
                                                                payment.getValorAbono(), payment.getValorReten(), paySubTotal, payTotal);
            
            ccResumenReportList.add(resumenReport);
        });
        
        return ccResumenReportList;
    };
}
