package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CCResumenReport;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PagoRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import java.math.BigDecimal;
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
    @Autowired
    BillRepository billRepository;
    
    private UUID idCredit;
    private double nume = 0.0;
    private double abono = 0.0;
    private String statusCambio = "";
    private double resto = 0.0;
    private String document = "";
    private String doc = "";
    private String saldo = "";
    private double sumado = 0.0;
    
    @Async("asyncExecutor")
    public Payment create(Payment payment) {
        Payment saved = paymentRepository.save(payment);
        document = saved.getCredits().getPago().getBill().getId().toString();
        log.info("PaymentServices Payment created id: {}", saved.getId());
        if (saved.getCredits().getId() != null){
            idCredit = saved.getCredits().getId();
            if ("PAC".equals(saved.getTypePayment())){
                abono = saved.getValorAbono();
            } else {
                abono = saved.getValorNotac();
            }
            updateCredits(idCredit);
            updateBill(payment, document);
        }
	return saved;
    }
    
    @Async("asyncExecutor")
    public void updateCredits(UUID credits){
        Credits cambio = creditsRepository.findOne(idCredit);
        nume = cambio.getValor();
        resto = nume - abono;
        cambio.setValor(resto);
        if (cambio.getValor() <= 0.01){
            statusCambio = "PAGADO";
            cambio.setStatusCredits(statusCambio);
        }
        creditsRepository.save(cambio);
        resto = 0.00;
        log.info("PaymentServices updateCredits FINISHED");
    }
    
    @Async("asyncExecutor")
    public void updateBill(Payment payment, String document) {
        Bill billed = billRepository.findOne(payment.getCredits().getPago().getBill().getId());
        String nbillId = billed.getId().toString();
        if (nbillId.equals(document)){
            saldo = billed.getSaldo();
            nume = Double.parseDouble(saldo);
            sumado = nume - abono;
            BigDecimal vsumado = new BigDecimal(sumado);
            vsumado = vsumado.setScale(2, BigDecimal.ROUND_HALF_UP);
            saldo = String.valueOf(vsumado);
            billed.setSaldo(saldo);
            billRepository.save(billed);
        }
        log.info("PaymentServices updateBill FINISHED");
    }
    
    public List<CCResumenReport> getPaymentsByUserClientId(UUID id) {
        log.info("PaymentServices getPaymentsByUserClientId: {}", id);
        Iterable<Payment> payments = paymentRepository.findAllPaymentsByUserClientId(id);
        List<CCResumenReport> ccResumenReportList = new ArrayList<>();
        
        payments.forEach(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            String fechaPago = dateFormat.format(new Date(payment.getDatePayment()));
            
            CCResumenReport resumenReport = new CCResumenReport(payment.getCredits().getPago().getBill().getClient().getIdentification(), payment.getCredits().getPago().getBill().getClient().getName(),
                                                                payment.getCredits().getPago().getBill().getStringSeq(), payment.getCredits().getPago().getBill().getTotal(), payment.getTypePayment(), 
                                                                payment.getModePayment(), fechaPago, payment.getValorAbono(), payment.getValorReten(), payment.getValorNotac());
            
            ccResumenReportList.add(resumenReport);
        });
        
        return ccResumenReportList;
    }
}
