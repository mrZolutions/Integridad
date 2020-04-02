package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CreditsReport;
import com.mrzolution.integridad.app.domain.report.CreditsResumeReport;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private double saldo;
    private double sumTotal = 0.0;
    private double sumTotalAbono = 0.0;
    private double sumTotalReten = 0.0;
    private double sumTotalNotac = 0.0;
    private double sumTotalValor = 0.0;
    private double total = 0.0;
    private double totalReten = 0.0;
    private double totalNotac = 0.0;
    private double totalValor = 0.0;

    private double totalAbonoSum = 0.0;
    private double totalNCum = 0.0;
    private double totalRetSum = 0.0;
    private double totalSellSum = 0.0;
    private double totalSaldoSum = 0.0;
    private double totalAbono = 0.0;
    private double totalNC = 0;
    private double totalRet = 0;
    private int plazo = 0;
    private long lastDay = 0;

    public Iterable<Credits> getCreditsByBillId(UUID id) {
        Iterable<Credits> credits = creditsRepository.findCreditsByBillId(id);
        credits.forEach(credit -> {
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        log.info("CreditsServices getCreditsByBillId: {}", id);
        return credits;
    }
    
    public List<CreditsReport> getCreditsPendingOfBillByUserClientId(UUID id, long dateTwo) {
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalNotac = 0;
        sumTotalValor = 0;
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
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
                                   
            saldo = credit.getPago().getBill().getTotal() - (sumAbono + sumReten + sumNotac);
            
            BigDecimal vsaldo = new BigDecimal(saldo);
            if (saldo == 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else if (saldo < 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else {
                vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            
            saldo = vsaldo.doubleValue();
            
            if (saldo > 0) {
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
                    sumTotalValor = saldo;
                    sumTotalAbono = sumAbono;
                    sumTotalReten = sumReten;
                    sumTotalNotac = sumNotac;   
                }
            }
            
            Date today = new Date();
            int diasVencim = 0;
            try {
                Date fVence = dateFormat.parse(fechaVence);
                diasVencim = (int) ((today.getTime()-fVence.getTime())/86400000);
            } catch (ParseException ex) {
                Logger.getLogger(CreditsServices.class.getName()).log(Level.SEVERE, null, ex);
            }
                 
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
            
            if (saldo > 0) {
                CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getIdentification(), credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), 
                                                         fechaVenta, fechaVence, credit.getDiasPlazo(), diasVencim, credit.getPago().getBill().getTotal(), sumAbono, sumReten, sumNotac, saldo, pPlazo, sPlazo,
                                                         tPlazo, cPlazo, qPlazo);
                creditsReportList.add(saleReport);
                total = Double.sum(total, credit.getPago().getBill().getTotal());
                totalAbono = Double.sum(totalAbono, sumAbono);
                totalReten = Double.sum(totalReten, sumReten);
                totalNotac = Double.sum(totalNotac, sumNotac);
                totalValor = total - (totalAbono + totalReten + totalNotac);
            }
            
        });
        
        if (saldo >= 0) {
            CreditsReport saleReport = new CreditsReport("SUB-TOTAL ", null, null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalNotac, sumTotalValor, 0, 0, 0, 0, 0);
            creditsReportList.add(saleReport);
        
            CreditsReport salesReport = new CreditsReport("TOTAL GENERAL ", null, null, null, null, 0, 0, total, totalAbono, totalReten, totalNotac, totalValor, 0, 0, 0, 0, 0);
            creditsReportList.add(salesReport);
            sumTotal = 0;
            sumTotalAbono = 0;
            sumTotalReten = 0;
            sumTotalNotac = 0;
            sumTotalValor = 0;
        }
        
        return creditsReportList;
    }

    public List<CreditsResumeReport> getCreditsPendingOfBillByUserClientIdResume(UUID id, long dateTwo) {
        log.info("CreditsServices getCreditsPendingOfBillByUserClientIdResume: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsOfBillByUserClientId(id, dateTwo);
        Credits credit = null;
        List<CreditsResumeReport> creditsReportList = new ArrayList<>();
        List<Credits> creditsList = Lists.newArrayList(credits);
        Bill bill = null;
        UUID clientId = null;
        CreditsResumeReport creditsToAdd = null;
        long today = new Date().getTime();

        if (creditsList.size() > 0) {
            credit = creditsList.get(0);
            creditsToAdd = setInitialToReport(credit);
            bill = credit.getPago().getBill();
            clientId = bill.getClient().getId();

            totalSellSum = bill.getTotal();
            totalSaldoSum = Double.valueOf(bill.getSaldo());
        }

        for (int i = 0; i < creditsList.size(); i++){
            credit = creditsList.get(i);
            populateChildren(credit);

            if(bill.getId().equals(UUID.fromString(credit.getBillId()))){
                if (plazo < credit.getDiasPlazo()){
                    plazo = credit.getDiasPlazo();
                    lastDay = credit.getFecha();
                }
            } else {
                setReportCalcedVariables(creditsToAdd, today);
                creditsReportList.add(creditsToAdd);

                creditsToAdd = setInitialToReport(credit);
                bill = credit.getPago().getBill();
                //********************************************************************************************************************
                if(!clientId.equals(bill.getClient().getId())){
                    CreditsResumeReport totalClient = new CreditsResumeReport();
                    totalClient.setIdentification("TOTAL");
                    totalClient.setValorAbono(totalAbonoSum);
                    totalClient.setValorReten(totalRetSum);
                    totalClient.setValorNotac(totalNCum);
                    totalClient.setSaldo(totalSaldoSum);
                    totalClient.setCosto(totalSellSum);
                    creditsReportList.add(totalClient);
                    clientId = bill.getClient().getId();
                    totalAbonoSum = 0.0;
                    totalNCum = 0.0;
                    totalRetSum = 0.0;

                    totalSellSum = bill.getTotal();
                    totalSaldoSum = Double.valueOf(bill.getSaldo());

                } else {
                    totalSellSum = totalSellSum + bill.getTotal();
                    totalSaldoSum = totalSaldoSum + Double.valueOf(bill.getSaldo());
                }
                //********************************************************************************************************************
            }

            double partial = 0;
            double partialNT = 0;
            double partialRet = 0;
            for(Payment pay : credit.getPayments()){
                if(pay.isActive()){
                    partial = partial + pay.getValorAbono();
                    partialNT = partialNT + pay.getValorNotac();
                    partialRet = partialRet + pay.getValorReten();
                }
            }
            totalAbono = totalAbono + partial;
            totalNC = totalNC + partialNT;
            totalRet = totalRet + partialRet;
        }

        setReportCalcedVariables(creditsToAdd, today);
        creditsReportList.add(creditsToAdd);
        CreditsResumeReport totalClient = new CreditsResumeReport();
        totalClient.setIdentification("TOTAL");
        totalClient.setValorAbono(totalAbonoSum);
        totalClient.setValorReten(totalRetSum);
        totalClient.setValorNotac(totalNCum);
        totalClient.setSaldo(totalSaldoSum);
        totalClient.setCosto(totalSellSum);
        creditsReportList.add(totalClient);

        log.info("CreditsServices getCreditsPendingOfBillByUserClientIdResume DONE size: {}", creditsReportList.size());
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

    private void setReportCalcedVariables (CreditsResumeReport creditsToAdd, long today ){
        long diasVenc = today - lastDay;
        if(diasVenc < 0){
            diasVenc = 0;
        }

        creditsToAdd.setDiasCredit(plazo);
        creditsToAdd.setValorAbono(totalAbono);
        creditsToAdd.setValorNotac(totalNC);
        creditsToAdd.setValorReten(totalRet);
        creditsToAdd.setDiasVencim((int)(TimeUnit.DAYS.convert(diasVenc, TimeUnit.MILLISECONDS)));

        totalAbonoSum = totalAbonoSum + totalAbono;
        totalNCum = totalNCum + totalNC;
        totalRetSum = totalRetSum + totalRet;
    }

    private CreditsResumeReport setInitialToReport (Credits credit){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Bill bill = credit.getPago().getBill();
        totalAbono = 0;
        totalNC = 0;
        totalRet = 0;
        plazo = credit.getDiasPlazo();
        lastDay = credit.getFecha();

        return new CreditsResumeReport(bill.getClient().getIdentification(), bill.getClient().getName(),
                bill.getStringSeq(), dateFormat.format(new Date(bill.getDateCreated())), bill.getTotal(), Double.valueOf(bill.getSaldo()));
    }
    
}