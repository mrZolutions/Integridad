package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.report.CreditsDebtsReport;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
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
public class CreditsDebtsServices {
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    
    private UUID providerId;
    private double saldo = 0;
    private double sumTotal = 0;
    private double sumTotalAbono = 0;
    private double sumTotalReten = 0;
    private double sumTotalValor = 0;
    
    public Iterable<CreditsDebts> getCreditsDebtsOfDebtsToPayByDebtsToPayId(UUID id) {
        log.info("CreditsDebtsServices getCreditsDebtsOfDebtsToPayByDebtsToPayId: {}", id);
        Iterable<CreditsDebts> creditsDebts = creditsDebtsRepository.findCreditsDebtsOfDebtsToPayByDebtsToPayId(id);
        creditsDebts.forEach(creditDebt -> {
            creditDebt.setListsNull();
            creditDebt.setFatherListToNull();
        });
        return creditsDebts;
    }
    
    public List<CreditsDebtsReport> getCreditsDebtsPendingOfDebtsToPayByUserClientId(UUID id, long dateTwo) {
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalValor = 0;
        saldo = 0;
        log.info("CreditsDebtsServices getCreditsDebtsPendingOfDebtsToPayByUserClientId: {}", id);
        Iterable<CreditsDebts> creditsDebts = creditsDebtsRepository.findCreditsDebtsPendingOfDebtsToPayByUserClientId(id, dateTwo);
        List<CreditsDebtsReport> creditsDebtsReportList = new ArrayList<>();
        
        if (Iterables.size(creditsDebts) > 0) {
            CreditsDebts firstCredit = Iterables.getFirst(creditsDebts, new CreditsDebts());
            providerId = firstCredit.getPagoDebts().getDebtsToPay().getProvider().getId();
        }
        
        creditsDebts.forEach(creditD -> {
            populateChildren(creditD);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            String fechaVenta = dateFormat.format(new Date(creditD.getPagoDebts().getDebtsToPay().getFecha()));
            String fechaVence = dateFormat.format(new Date(creditD.getFecha()));
            
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            
            for (PaymentDebts payments : creditD.getPaymentDebts()) {
                if (payments.getDatePayment() <= dateTwo) {
                    sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                    sumReten = Double.sum(sumReten, payments.getValorReten());
                }
            }
            
            if (providerId != null && providerId.equals(creditD.getPagoDebts().getDebtsToPay().getProvider().getId())) {
                sumTotal = Double.sum(sumTotal, creditD.getPagoDebts().getDebtsToPay().getTotal());
                sumTotalAbono = Double.sum(sumTotalAbono, sumAbono);
                sumTotalReten = Double.sum(sumTotalReten, sumReten);
                sumTotalValor = sumTotal - (sumTotalAbono + sumTotalReten);
            } else {
                providerId = creditD.getPagoDebts().getDebtsToPay().getProvider().getId();
                CreditsDebtsReport creditDebtReport = new CreditsDebtsReport("SUB-TOTAL ", null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalValor);
                creditsDebtsReportList.add(creditDebtReport);
                sumTotal = creditD.getPagoDebts().getDebtsToPay().getTotal();
                sumTotalValor = creditD.getValor();
                sumTotalAbono = sumAbono;
                sumTotalReten = sumReten;
            }
            
            Date today = new Date();
            int diasVencim = 0;
            try {
                Date fVence = dateFormat.parse(fechaVence);
                diasVencim = (int) ((today.getTime()-fVence.getTime())/86400000);
            } catch (ParseException ex) {
                Logger.getLogger(CreditsDebtsServices.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            saldo = creditD.getPagoDebts().getDebtsToPay().getTotal() - (sumAbono + sumReten);
            
            CreditsDebtsReport creditDebtReport = new CreditsDebtsReport(creditD.getPagoDebts().getDebtsToPay().getProvider().getName(), creditD.getPagoDebts().getDebtsToPay().getBillNumber(), 
                                                                         fechaVenta, fechaVence, creditD.getDiasPlazo(), diasVencim, creditD.getPagoDebts().getDebtsToPay().getTotal(),
                                                                         sumAbono, sumReten, saldo);
            creditsDebtsReportList.add(creditDebtReport);
        });
        CreditsDebtsReport creditDebtReport = new CreditsDebtsReport("SUB-TOTAL ", null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalValor);
        creditsDebtsReportList.add(creditDebtReport);
        
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalValor = 0;
        saldo = 0;
        
        return creditsDebtsReportList;
    }
    
    private void populateChildren(CreditsDebts creditsDebts) {
        List<PaymentDebts> paymentDebtsList = new ArrayList<>();
        Iterable<PaymentDebts> paymentDebts = paymentDebtsRepository.findByCreditsDebts(creditsDebts);
        paymentDebts.forEach(paymentDebt -> {
            paymentDebt.setListsNull();
            paymentDebt.setFatherListToNull();
            paymentDebt.setCreditsDebts(null);          
            paymentDebtsList.add(paymentDebt);
        });
        creditsDebts.setPaymentDebts(paymentDebtsList);
        creditsDebts.setFatherListToNull();
    }
    
}
