package com.mrzolution.integridad.app.services;
/**
 *
 * @author mrzolutions-daniel
 */
import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CreditsPayedReport;
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
        
        credits.forEach(credit -> {
            populateChildren(credit);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaVenta = dateFormat.format(new Date(credit.getPago().getBill().getDateCreated()));
            String fechaVence = dateFormat.format(new Date(credit.getFecha()));
            
            Date today = new Date();
            int diasVencim = 0;
            
            try {
                Date fVence = dateFormat.parse(fechaVence);
                diasVencim = (int) ((today.getTime()-fVence.getTime())/86400000);
            } catch (ParseException ex) {
                Logger.getLogger(CreditsServices.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
            Double sumSubTotal = Double.valueOf(0);
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            Double sumNotac = Double.valueOf(0);
            
            for (Payment payments : credit.getPayments()){
                sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                sumReten = Double.sum(sumReten, payments.getValorReten());
                sumNotac = Double.sum(sumNotac, payments.getValorNotac());
            }
            
            Double priPlazo = Double.valueOf(0);
            Double segPlazo = Double.valueOf(0);
            Double terPlazo = Double.valueOf(0);
            Double cuaPlazo = Double.valueOf(0);
            Double quiPlazo = Double.valueOf(0);
            
            if (diasVencim > 0 && diasVencim <= 30){
                priPlazo = credit.getValor();
            } else if (diasVencim > 30 && diasVencim <= 60) {
                segPlazo = credit.getValor();
            } else if (diasVencim > 60 && diasVencim <= 90) {
                terPlazo = credit.getValor();
            } else if (diasVencim > 90 && diasVencim <= 120) {
                cuaPlazo = credit.getValor();
            } else if (diasVencim > 120){
                quiPlazo = credit.getValor();
            } else {
                priPlazo = 0.0;
                segPlazo = 0.0;
                terPlazo = 0.0;
                cuaPlazo = 0.0;
                quiPlazo = 0.0;
            }
            
            CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), 
                                                         fechaVenta, fechaVence, credit.getDiasPlazo(), diasVencim, credit.getPago().getBill().getTotal(), 
                                                         sumAbono, sumReten, sumNotac, credit.getValor(), priPlazo, segPlazo, terPlazo, cuaPlazo, quiPlazo);
            
            creditsReportList.add(saleReport);
        });
        return creditsReportList;
    };
    
    public List<CreditsPayedReport> getCreditsPayedOfBillByUserClientId(UUID id){
        log.info("CreditsServices getCreditsOfBillByUserClientId: {}", id);
        Iterable<Credits> credits = creditsRepository.findCreditsPayedOfBillByUserClientId(id);
        List<CreditsPayedReport> creditsPayedReportList = new ArrayList<>();
        
        credits.forEach(creditp -> {
            CreditsPayedReport salesReport = new CreditsPayedReport(creditp.getPago().getBill().getClient().getName(), creditp.getPago().getBill().getClient().getIdentification(),
                                           creditp.getPago().getBill().getStringSeq(), creditp.getPago().getBill().getTotal(), creditp.getValor(), creditp.getStatusCredits());
            creditsPayedReportList.add(salesReport);
        });
        return creditsPayedReportList;
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
