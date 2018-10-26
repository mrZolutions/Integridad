package com.mrzolution.integridad.app.services;
/**
 *
 * @author mrzolutions-daniel
 */
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CreditsPayedReport;
import com.mrzolution.integridad.app.domain.report.CreditsReport;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.common.collect.Iterables;
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
        //UUID clientId = null;
        //Double sumSubTotal = Double.valueOf(0);
        //Double sumSubTotalOtrasuma = Double.valueOf(0);
        
        //if(Iterables.size(credits) > 0){
      
         //   Credits firstCredit = Iterables.getFirst(credits, new Credits());
         //   clientId = firstCredit.getPago().getBill().getClient().getId();
            
        //}
        
        // cambiale este lambda por un for normal for(int i....)
        
        credits.forEach(credit -> {
            populateChildren(credit);
                    
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            Double sumNotac = Double.valueOf(0);
                
            for (Payment payments : credit.getPayments()){
                sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                sumReten = Double.sum(sumReten, payments.getValorReten());
                sumNotac = Double.sum(sumNotac, payments.getValorNotac());
            }
            
            //if(clientId != null && 
            //        clientId.equals(credit.getPago().getBill().getClient().getId())){
            //    sumSubTotal = (sumAbono + sumReten) - sumNotac;
            //    sumSubTotalOtrasuma = suma + suma
            //} else {
                //
                //AQUI con solo el nombre y el sumsubtotal en el campo respectivo, el resto de campos con string vacio o null
                //CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), credit.getPago().getBill().getDateCreated(), ,credit.getPago().getBill().getTotal(), sumAbono, sumReten, sumNotac, sumSubTotal, credit.getValor(),
                //                           credit.getStatusCredits());
            
                //creditsReportList.add(saleReport);
            //    sumSubTotal = Double.valueOf(0);
            //    clientId = credit.getPago().getBill().getClient().getId();
           // }
                        
            CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getStringSeq(), credit.getPago().getBill().getDateCreated(), ,credit.getPago().getBill().getTotal(), sumAbono, sumReten, sumNotac, sumSubTotal, credit.getValor(),
                                           credit.getStatusCredits());
            
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
