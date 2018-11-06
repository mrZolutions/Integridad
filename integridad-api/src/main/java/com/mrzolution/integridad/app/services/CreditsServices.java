package com.mrzolution.integridad.app.services;
/**
 *
 * @author mrzolutions-daniel
 */
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


@Slf4j
@Component
public class CreditsServices {
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    PaymentRepository paymentRepository;
    
    private UUID clientId;
    private double sumTotal = 0;
    private double sumTotalAbono = 0;
    private double sumTotalReten = 0;
    private double sumTotalNotac = 0;
    private double sumTotalValor = 0;
    
    public Iterable<Credits> getCreditsOfBillByBillId(UUID id){
        log.info("CreditsServices getCreditsOfBillByBillId: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsOfBillByBillId(id);
        credits.forEach(credit ->{
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        return credits;
    };
    
    public List<CreditsReport> getCreditsPendingOfBillByUserClientId(UUID id){
        log.info("CreditsServices getCreditsOfBillByUserClientId: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsPendingOfBillByUserClientId(id);
        List<CreditsReport> creditsReportList = new ArrayList<>();
        
        if(Iterables.size(credits) >= 0){
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
            
            for (Payment payments : credit.getPayments()){
                sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                sumReten = Double.sum(sumReten, payments.getValorReten());
                sumNotac = Double.sum(sumNotac, payments.getValorNotac());
            }
            
            if(clientId != null && clientId.equals(credit.getPago().getBill().getClient().getId())){
                sumTotal = Double.sum(sumTotal, credit.getPago().getBill().getTotal());
                sumTotalValor = Double.sum(sumTotalValor, credit.getValor());
                sumTotalAbono = Double.sum(sumTotalAbono, sumAbono);
                sumTotalReten = Double.sum(sumTotalReten, sumReten);
                sumTotalNotac = Double.sum(sumTotalNotac, sumNotac);
            } else {
                clientId = credit.getPago().getBill().getClient().getId();
                CreditsReport saleReport = new CreditsReport("SUB-TOTAL ", null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalNotac, sumTotalValor, 0, 0, 0, 0, 0);
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
            
            if(diasVencim > 0 && diasVencim <= 30){
                pPlazo = credit.getValor();
            } else if(diasVencim > 30 && diasVencim <= 60){
                sPlazo = credit.getValor();               
            } else if(diasVencim > 60 && diasVencim <= 90){
                tPlazo = credit.getValor();
            } else if(diasVencim > 90 && diasVencim <= 120){
                cPlazo = credit.getValor();
            } else if(diasVencim > 120){
                qPlazo = credit.getValor();
            } else {
                pPlazo = 0.0;
                sPlazo = 0.0;
                tPlazo = 0.0;
                cPlazo = 0.0;
                qPlazo = 0.0;
            }
            
            CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), 
                                                         fechaVenta, fechaVence, credit.getDiasPlazo(), diasVencim, credit.getPago().getBill().getTotal(), 
                                                         sumAbono, sumReten, sumNotac, credit.getValor(), pPlazo, sPlazo, tPlazo, cPlazo, qPlazo);
            creditsReportList.add(saleReport);    
        });
        
        CreditsReport saleReport = new CreditsReport("SUB-TOTAL ", null, null, null, 0, 0, sumTotal, sumTotalAbono, sumTotalReten, sumTotalNotac, sumTotalValor,0, 0, 0, 0, 0);
        creditsReportList.add(saleReport);
        
        sumTotal = 0;
        sumTotalAbono = 0;
        sumTotalReten = 0;
        sumTotalNotac = 0;
        sumTotalValor = 0;
        
        return creditsReportList;
    };
    
    private void populateChildren(Credits credits){
        log.info("CreditsServices populateChildren creditsId: {}", credits.getId());
        List<Payment> paymentsList = new ArrayList<>();
        Iterable<Payment> creditos = paymentRepository.findByCredits(credits);
        
        creditos.forEach(payment -> {
            payment.setListsNull();
            payment.setFatherListToNull();
            payment.setCredits(null);          
            paymentsList.add(payment);
        });
        credits.setPayments(paymentsList);
        credits.setFatherListToNull();
        log.info("CreditsServices populateChildren FINISHED creditsId: {}", credits.getId());
    };
    
}
