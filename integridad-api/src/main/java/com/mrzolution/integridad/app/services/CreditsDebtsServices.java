package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.report.CreditsDebtsReport;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private double saldos;
    private double sumTotal = 0;
    private double sumTotalAbono = 0;
    private double sumTotalReten = 0;
    private double sumTotalValor = 0;
    private double total = 0.0;
    private double totalAbono = 0.0;
    private double totalReten = 0.0;
    private double totalValor = 0.0;
    private String retenNumber = "";
    private String numCheque = "";
    
    public Iterable<CreditsDebts> getCreditsDebtsByDebtsToPayId(UUID id) {
        Iterable<CreditsDebts> creditsDebts = creditsDebtsRepository.findCreditsDebtsByDebtsToPayId(id);
        creditsDebts.forEach(creditDebt -> {
            creditDebt.setListsNull();
            creditDebt.setFatherListToNull();
        });
        log.info("CreditsDebtsServices getCreditsDebtsByDebtsToPayId: {}", id);
        return creditsDebts;
    }
    
    public List<CreditsDebtsReport> getCreditsDebtsPendingOfDebtsToPayByUserClientId(UUID id, long dateTwo) {
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalValor = 0;
        total = 0;
        totalAbono = 0;
        totalReten = 0;
        totalValor = 0;
        
        log.info("CreditsDebtsServices getCreditsDebtsPendingOfDebtsToPayByUserClientId: {}", id);
        Iterable<CreditsDebts> creditsDebts = creditsDebtsRepository.findCreditsDebtsPendingOfDebtsToPayByUserClientId(id, dateTwo);
        List<CreditsDebtsReport> creditsDebtsReportList = new ArrayList<>();
        
        if (Iterables.size(creditsDebts) > 0) {
            CreditsDebts firstCredit = Iterables.getFirst(creditsDebts, new CreditsDebts());
            providerId = firstCredit.getPagoDebts().getDebtsToPay().getProvider().getId();
        }
        
        creditsDebts.forEach(creditD -> {
            populateChildren(creditD);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
            String fechaVenta = dateFormat.format(new Date(creditD.getPagoDebts().getDebtsToPay().getFecha()));
            String fechaVence = dateFormat.format(new Date(creditD.getFecha()));
            
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            
            for (PaymentDebts payments : creditD.getPaymentDebts()) {
                if (payments.getDatePayment() <= dateTwo && payments.isActive()) {
                    sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                    sumReten = Double.sum(sumReten, payments.getValorReten());
                }
            }
            
            saldos = creditD.getPagoDebts().getDebtsToPay().getTotal() - (sumAbono + sumReten);

            BigDecimal vsaldo = new BigDecimal(saldos);
            
            if (saldos == 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else if (saldos < 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else {
                vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            
            saldos = vsaldo.doubleValue();
            
            if (saldos > 0) {
                if (providerId != null && providerId.equals(creditD.getPagoDebts().getDebtsToPay().getProvider().getId())) {
                    sumTotal = Double.sum(sumTotal, creditD.getPagoDebts().getDebtsToPay().getTotal());
                    sumTotalAbono = Double.sum(sumTotalAbono, sumAbono);
                    sumTotalReten = Double.sum(sumTotalReten, sumReten);
                    sumTotalValor = sumTotal - (sumTotalAbono + sumTotalReten);
                } else {
                    providerId = creditD.getPagoDebts().getDebtsToPay().getProvider().getId();
                    CreditsDebtsReport creditDebtReport = new CreditsDebtsReport("SUB-TOTAL ", null, null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalValor);
                    creditsDebtsReportList.add(creditDebtReport);
                    sumTotal = creditD.getPagoDebts().getDebtsToPay().getTotal();
                    sumTotalValor = saldos;
                    sumTotalAbono = sumAbono;
                    sumTotalReten = sumReten;
                }
            }
            
            Date today = new Date();
            int diasVencim = 0;
            try {
                Date fVence = dateFormat.parse(fechaVence);
                diasVencim = (int) ((today.getTime()-fVence.getTime())/86400000);
            } catch (ParseException ex) {
                Logger.getLogger(CreditsDebtsServices.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (saldos > 0) {
                CreditsDebtsReport creditDebtReport = new CreditsDebtsReport(creditD.getPagoDebts().getDebtsToPay().getProvider().getRuc(), creditD.getPagoDebts().getDebtsToPay().getProvider().getName(), creditD.getPagoDebts().getDebtsToPay().getBillNumber(), 
                                                                             fechaVenta, fechaVence, creditD.getDiasPlazo(), diasVencim, creditD.getPagoDebts().getDebtsToPay().getTotal(),
                                                                             sumAbono, sumReten, saldos);
                creditsDebtsReportList.add(creditDebtReport);
                total = Double.sum(total, creditD.getPagoDebts().getDebtsToPay().getTotal());
                totalAbono = Double.sum(totalAbono, sumAbono);
                totalReten = Double.sum(totalReten, sumReten);
                totalValor = total - (totalAbono + totalReten);
            }
        });

        if (saldos >= 0) {
            CreditsDebtsReport creditDebtReport = new CreditsDebtsReport("SUB-TOTAL ", null, null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalValor);
            creditsDebtsReportList.add(creditDebtReport);

            CreditsDebtsReport creditDebtsReport = new CreditsDebtsReport("TOTAL GENERAL ", null, null, null, null, 0, 0, total, totalAbono, totalReten, totalValor);
            creditsDebtsReportList.add(creditDebtsReport);
            sumTotal = 0;
            sumTotalAbono = 0;
            sumTotalReten = 0;
            sumTotalValor = 0;
        }
        
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