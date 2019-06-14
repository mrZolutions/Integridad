package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CreditsReport;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@Component
public class CreditsServices {
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    PaymentRepository paymentRepository;
    
    private UUID clientId;
    private double saldo = 0.0;
    private double sumTotal = 0.0;
    private double sumTotalAbono = 0.0;
    private double sumTotalReten = 0.0;
    private double sumTotalNotac = 0.0;
    private double sumTotalValor = 0.0;
    private double total = 0.0;
    private double totalAbono = 0.0;
    private double totalReten = 0.0;
    private double totalNotac = 0.0;
    private double totalValor = 0.0;
    
    public Iterable<Credits> getCreditsByBillId(UUID id) {
        Iterable<Credits> credits = creditsRepository.findCreditsByBillId(id);
        credits.forEach(credit -> {
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        log.info("CreditsServices getCreditsByBillId DONE: {}", id);
        return credits;
    }
    
    public List<CreditsReport> getCreditsPendingOfBillByUserClientId(UUID id, long dateTwo) {
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalNotac = 0;
        sumTotalValor = 0;
        saldo = 0;
        total = 0;
        totalAbono = 0;
        totalReten = 0;
        totalNotac = 0;
        totalValor = 0;
        
        log.info("CreditsServices getCreditsPendingOfBillByUserClientId: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsPendingOfBillByUserClientId(id, dateTwo);
        List<CreditsReport> creditsReportList = new ArrayList<>();
        
        if (Iterables.size(credits) > 0) {
            Credits firstCredit = Iterables.getFirst(credits, new Credits());
            clientId = firstCredit.getPago().getBill().getClient().getId();
        }
        
        credits.forEach(credit -> {
            populateChildren(credit);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            String fechaVenta = dateFormat.format(new Date(credit.getPago().getBill().getDateCreated()));
            String fechaVence = dateFormat.format(new Date(credit.getFecha()));
            
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            Double sumNotac = Double.valueOf(0);
            Double pPlazo = Double.valueOf(0);
            Double sPlazo = Double.valueOf(0);
            Double tPlazo = Double.valueOf(0);
            Double cPlazo = Double.valueOf(0);
            Double qPlazo = Double.valueOf(0);
            
            for (Payment payments : credit.getPayments()) {
                if (payments.getDatePayment() <= dateTwo && payments.isActive()) {
                    sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                    sumReten = Double.sum(sumReten, payments.getValorReten());
                    sumNotac = Double.sum(sumNotac, payments.getValorNotac());
                }
            }
            
            if (clientId != null && clientId.equals(credit.getPago().getBill().getClient().getId())) {
                sumTotal = Double.sum(sumTotal, credit.getPago().getBill().getTotal());
                sumTotalAbono = Double.sum(sumTotalAbono, sumAbono);
                sumTotalReten = Double.sum(sumTotalReten, sumReten);
                sumTotalNotac = Double.sum(sumTotalNotac, sumNotac);
                sumTotalValor = sumTotal - (sumTotalAbono + sumTotalReten + sumTotalNotac);
            } else {
                clientId = credit.getPago().getBill().getClient().getId();
                CreditsReport saleReport = new CreditsReport("SUB-TOTAL ", null, null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalNotac, sumTotalValor, 0, 0, 0, 0, 0);
                creditsReportList.add(saleReport);
                sumTotal = credit.getPago().getBill().getTotal();
                sumTotalValor = credit.getValor();
                sumTotalAbono = sumAbono;
                sumTotalReten = sumReten;
                sumTotalNotac = sumNotac;
            }
            
            Date today = new Date();
            int diasVencim = 0;
            try {
                Date fVence = dateFormat.parse(fechaVence);
                diasVencim = (int) ((today.getTime()-fVence.getTime())/86400000);
            } catch (ParseException ex) {
                Logger.getLogger(CreditsServices.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            saldo = credit.getPago().getBill().getTotal() - (sumAbono + sumReten + sumNotac);
                 
            if (diasVencim > 0 && diasVencim <= 30) {
                pPlazo = saldo;
            } else if (diasVencim > 30 && diasVencim <= 60) {
                sPlazo = saldo;               
            } else if (diasVencim > 60 && diasVencim <= 90) {
                tPlazo = saldo;
            } else if (diasVencim > 90 && diasVencim <= 120) {
                cPlazo = saldo;
            } else if (diasVencim > 120) {
                qPlazo = saldo;
            } else {
                pPlazo = 0.0;
                sPlazo = 0.0;
                tPlazo = 0.0;
                cPlazo = 0.0;
                qPlazo = 0.0;
            } 
            
            CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getIdentification(), credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), 
                                                         fechaVenta, fechaVence, credit.getDiasPlazo(), diasVencim, credit.getPago().getBill().getTotal(), sumAbono, sumReten, sumNotac, saldo, pPlazo, sPlazo,
                                                         tPlazo, cPlazo, qPlazo);
            creditsReportList.add(saleReport);
            
            total = Double.sum(total, credit.getPago().getBill().getTotal());
            totalAbono = Double.sum(totalAbono, sumAbono);
            totalReten = Double.sum(totalReten, sumReten);
            totalNotac = Double.sum(totalNotac, sumNotac);
            totalValor = total - (totalAbono + totalReten + totalNotac);
        });    
        CreditsReport saleReport = new CreditsReport("SUB-TOTAL ", null, null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalNotac, sumTotalValor, 0, 0, 0, 0, 0);
        creditsReportList.add(saleReport);
        
        CreditsReport salesReport = new CreditsReport("TOTAL GENERAL ", null, null, null, null, 0, 0, total, totalAbono, totalReten, totalNotac, totalValor, 0, 0, 0, 0, 0);
        creditsReportList.add(salesReport);
        
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalNotac = 0;
        sumTotalValor = 0;
        saldo = 0;
        
        return creditsReportList;
    }
    
    private void populateChildren(Credits credits) {
        List<Payment> paymentsList = new ArrayList<>();
        Iterable<Payment> payments = paymentRepository.findByCredits(credits);
        payments.forEach(payment -> {
            payment.setListsNull();
            payment.setFatherListToNull();
            payment.setCredits(null);          
            paymentsList.add(payment);
        });
        credits.setPayments(paymentsList);
        credits.setFatherListToNull();
    }
    
}