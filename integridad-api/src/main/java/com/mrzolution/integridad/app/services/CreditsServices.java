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
                    
            Double sumAbono = Double.valueOf(0);
            Double sumReten = Double.valueOf(0);
            Double sumNotac = Double.valueOf(0);
            Double sumSubTotal = Double.valueOf(0);
            
            for (Payment payments : credit.getPayments()){
                sumAbono = Double.sum(sumAbono, payments.getValorAbono());
                sumReten = Double.sum(sumReten, payments.getValorReten());
                sumNotac = Double.sum(sumNotac, payments.getValorNotac());
            }
            
            sumSubTotal = (sumAbono + sumReten) - sumNotac;
            
            //System.out.println(sumAbono);
            //System.out.println(sumReten);
            //System.out.println(sumNotac);
            //System.out.println(sumSubTotal);
                        
            CreditsReport saleReport = new CreditsReport(credit.getPago().getBill().getClient().getName(), credit.getPago().getBill().getClient().getIdentification(),
                                           credit.getPago().getBill().getStringSeq(), credit.getPago().getBill().getTotal(), sumAbono, sumReten, sumNotac, sumSubTotal, credit.getValor(),
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
