package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CCResumenReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
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
    CreditsRepository creditsRepository;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    BillRepository billRepository;
    
    private double nume = 0;
    private double abono = 0;
    private double resto = 0;
    private String document = "--";
    private String documentDeactivated = "--";
    private String saldo = "";
    private String numCheque = "--";
    
    private UUID clientId;
    private double sumTotalAbono = 0;
    private double sumTotalReten = 0;
    private double sumTotalNotac = 0;
    private double totalAbono = 0;
    private double totalReten = 0;
    private double totalNotac = 0;
    
    
    public Iterable<Payment> getPaymentsByUserClientIdWithBankAndNroDocument(UUID userClientId, String banco, String nroduc) {
	Iterable<Payment> payments = paymentRepository.findPaymentsByUserClientIdWithBankAndNroDocument(userClientId, banco, nroduc);
	payments.forEach(payment -> {
            payment.setFatherListToNull();
            payment.setListsNull();
        });
        log.info("PaymentServices getPaymentsByUserClientIdWithBankAndNroDocument DONE: {}, {}, {}", userClientId, banco, nroduc);
	return payments;
    }
    
    public Iterable<Payment> getPaymentsByClientId(UUID id) {
        Iterable<Payment> payments = paymentRepository.findPaymentsByClientId(id);
        payments.forEach(payment -> {
            payment.setFatherListToNull();
            payment.setListsNull();
        });
        log.info("PaymentServices getPaymentsByClientId DONE: {}", id);
	return payments;
    }
    
    public Payment createPayment(Payment payment) {
        payment.setActive(true);
        payment.setFatherListToNull();
        payment.setListsNull();
        Payment saved = paymentRepository.save(payment);
        if (saved.getCredits().getId() != null) {
            if ("PAC".equals(saved.getTypePayment())){
                abono = saved.getValorAbono();
            } else {
                abono = saved.getValorNotac();
            }
            updateCreditsAndBill(saved);
        }
        log.info("PaymentServices createPayment DONE: {}, {}", saved.getId(), saved.getDocumentNumber());
	return saved;
    }
    
    public void updateCreditsAndBill(Payment saved){
        Iterable<Credits> credits = creditsRepository.findCreditsById(saved.getCredits().getId());
        credits.forEach(credit -> {
            credit.setValor(credit.getValor() - saved.getValorAbono());
            Credits Valor = creditsRepository.save(credit);
            Iterable<Bill> bills = billRepository.findBillById(Valor.getPago().getBill().getId());
            bills.forEach(bill -> {
                resto = Valor.getValor();
                BigDecimal vresto = new BigDecimal(resto);
                if (Valor.getValor() == 0) {
                    vresto = vresto.setScale(0, BigDecimal.ROUND_HALF_UP);
                } else {
                    vresto = vresto.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                saldo = String.valueOf(vresto);
                bill.setSaldo(saldo);
                billRepository.save(bill);
            });
        });
        log.info("PaymentServices updateCreditsAndBill DONE");
        resto = 0;
        saldo = "";
    }
    
    public List<CCResumenReport> getPaymentsByUserClientIdAndDates(UUID id, long dateOne, long dateTwo) {
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalNotac = 0;
        totalAbono = 0;
        totalReten = 0;
        totalNotac = 0;
        
        log.info("PaymentServices getPaymentsByUserClientId: {}", id);
        Iterable<Payment> payments = paymentRepository.findAllPaymentsByUserClientIdAndDates(id, dateOne, dateTwo);
        List<CCResumenReport> ccResumenReportList = new ArrayList<>();
        
        if (Iterables.size(payments) > 0) {
            Payment firstPayment = Iterables.getFirst(payments, new Payment());
            clientId = firstPayment.getCredits().getPago().getBill().getClient().getId();
        }
        
        payments.forEach(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaPago = dateFormat.format(new Date(payment.getDatePayment()));
            
            if (clientId != null && clientId.equals(payment.getCredits().getPago().getBill().getClient().getId())) {
                sumTotalAbono = Double.sum(sumTotalAbono, payment.getValorAbono());
                sumTotalReten = Double.sum(sumTotalReten, payment.getValorReten());
                sumTotalNotac = Double.sum(sumTotalNotac, payment.getValorNotac());
            } else {
                clientId = payment.getCredits().getPago().getBill().getClient().getId();
                CCResumenReport resumenReport = new CCResumenReport("SUB-TOTAL ", null, null, null, null, null, null, null, sumTotalAbono, sumTotalReten, sumTotalNotac);
                ccResumenReportList.add(resumenReport);
                sumTotalAbono = payment.getValorAbono();
                sumTotalReten = payment.getValorReten();
                sumTotalNotac = payment.getValorNotac();
            }
            
            if ("CHQ".equals(payment.getModePayment())) {
                numCheque = payment.getNoDocument();
            } else {
                numCheque = "-";
            }
            
            CCResumenReport resumenReport = new CCResumenReport(payment.getCredits().getPago().getBill().getClient().getIdentification(), payment.getCredits().getPago().getBill().getClient().getName(),
                                                                payment.getCredits().getPago().getBill().getStringSeq(), payment.getCredits().getPago().getBill().getTotal(), payment.getTypePayment(), 
                                                                payment.getModePayment(), numCheque, fechaPago, payment.getValorAbono(), payment.getValorReten(), payment.getValorNotac());        
            ccResumenReportList.add(resumenReport);
            
            totalAbono = Double.sum(totalAbono, payment.getValorAbono());
            totalReten = Double.sum(totalReten, payment.getValorReten());
            totalNotac = Double.sum(totalNotac, payment.getValorNotac());
        });
        CCResumenReport resumenReport = new CCResumenReport("SUB-TOTAL ", null, null, null, null, null, null, null, sumTotalAbono, sumTotalReten, sumTotalNotac);
        ccResumenReportList.add(resumenReport);
        
        CCResumenReport totResumenReport = new CCResumenReport("TOTAL GENERAL ", null, null, null, null, null, null, null, totalAbono, totalReten, totalNotac);
        ccResumenReportList.add(totResumenReport);
        
        return ccResumenReportList;
    }
    
    public Payment deactivatePayment(Payment payment) throws BadRequestException {
        if (payment.getId() == null) {
            throw new BadRequestException("Invalid Payment");
        }
        Payment paymentToDeactivate = paymentRepository.findOne(payment.getId());
        paymentToDeactivate.setListsNull();
        paymentToDeactivate.setActive(false);
        paymentToDeactivate.setDetail("ABONO ANULADO");
        Payment deactivated = paymentRepository.save(paymentToDeactivate);
        documentDeactivated = deactivated.getCredits().getPago().getBill().getId().toString();
        abono = deactivated.getValorAbono();
        updateCreditsAndBillOfPaymentDeactivated(deactivated, documentDeactivated);
        log.info("PaymentServices deactivatePayment DONE id: {}", paymentToDeactivate.getId());
        return paymentToDeactivate;
    }
    
    public void updateCreditsAndBillOfPaymentDeactivated(Payment deactivated, String document){
        Credits cambio = creditsRepository.findOne(deactivated.getCredits().getId());
        nume = cambio.getValor();
        resto = nume + abono;
        BigDecimal vrestado = new BigDecimal(resto);
        vrestado = vrestado.setScale(2, BigDecimal.ROUND_HALF_UP);
        cambio.setValor(vrestado.doubleValue());
        Credits creditSaved = creditsRepository.save(cambio);
        if (creditSaved != null) {
            Bill billed = billRepository.findOne(deactivated.getCredits().getPago().getBill().getId());
            String nbillId = billed.getId().toString();
            if (nbillId.equals(document)) {
                BigDecimal vsumado = new BigDecimal(creditSaved.getValor());
                vsumado = vsumado.setScale(2, BigDecimal.ROUND_HALF_UP);
                saldo = String.valueOf(vsumado);
                billed.setSaldo(saldo);
                billRepository.save(billed);
            }
        }
        log.info("PaymentServices updateCreditsAndBillOfPaymentDeactivated DONE");
        nume = 0;
        resto = 0;
        saldo = "";
    }
}