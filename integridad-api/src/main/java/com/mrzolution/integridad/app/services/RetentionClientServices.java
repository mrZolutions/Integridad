package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import com.mrzolution.integridad.app.repositories.RetentionClientRepository;
import java.math.BigDecimal;
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
public class RetentionClientServices {
    @Autowired
    RetentionClientRepository retentionClientRepository;
    @Autowired
    DetailRetentionClientRepository detailRetentionClientRepository;
    @Autowired
    DetailRetentionClientChildRepository detailRetentionClientChildRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    BillRepository billRepository;
    
    private double sum = 0.0;
    private double sumado = 0.0;
    private String document = "";
    private double valor = 0.0;
    private String doc = "";
    private int numC = 1;
    private String saldo = "";
 
    public RetentionClient getById(UUID id) {
	log.info("RetentionClientServices getById: {}", id);
	RetentionClient retrieved = retentionClientRepository.findOne(id);
	if(retrieved != null){
            log.info("RetentionClientServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("RetentionClientServices retrieved id NULL: {}", id);
	}
	populateChildren(retrieved);
        return retrieved;
    };
    
    @Async("asyncExecutor")
    public RetentionClient create(RetentionClient retentionClient) throws BadRequestException{   
        log.info("RetentionClientServices preparing for create new Retention");
        Iterable<RetentionClient> retenCli = retentionClientRepository.findByDocumentNumberAndBillId(retentionClient.getDocumentNumber(), retentionClient.getBill().getId());
        if (Iterables.size(retenCli) > 0){
            throw new BadRequestException("Retención Ya Existe");
        }
        
	List<DetailRetentionClient> details = retentionClient.getDetailRetentionClient();
        document = retentionClient.getBill().getId().toString();
        retentionClient.setDocumentDate(new Date().getTime());
        retentionClient.setDetailRetentionClient(null);
        retentionClient.setFatherListToNull();
        retentionClient.setListsNull();
        RetentionClient saved = retentionClientRepository.save(retentionClient);
        details.forEach(detail -> {
            detail.setRetentionClient(saved);
            sum += detail.getTotal();
            detailRetentionClientRepository.save(detail);
            detail.setRetentionClient(null);
        });
                                  
        log.info("RetentionClientServices Retention created id: {}", saved.getId());
        saved.setDetailRetentionClient(details);
        updateCreditsAndPayment(retentionClient, document);
        updateBill(retentionClient, document);
        sum = 0.0;
        valor = 0.0;
        return saved;
    };
    
    @Async("asyncExecutor")
    public void updateCreditsAndPayment(RetentionClient retentionClient, String document){
        Credits docNumber = creditsRepository.findByBillId(document);
        doc = docNumber.getBillId();
        if (doc.equals(document) && docNumber.getPayNumber() == numC){
            valor = docNumber.getValor();
            docNumber.setValor(valor - sum);
            Credits spCretits =  creditsRepository.save(docNumber);
            
            Payment specialPayment = new Payment();
            specialPayment.setCredits(spCretits);
            specialPayment.setDatePayment(retentionClient.getDateToday());
            specialPayment.setNoDocument(retentionClient.getRetentionNumber());
            specialPayment.setNoAccount(null);
            specialPayment.setDocumentNumber(retentionClient.getDocumentNumber());
            specialPayment.setTypePayment("RET");
            specialPayment.setDetail("ABONO POR RETENCION");
            specialPayment.setModePayment("RET");
            specialPayment.setValorAbono(0.0);
            specialPayment.setValorReten(sum);
            specialPayment.setValorNotac(0.0);
            paymentRepository.save(specialPayment);
        }
        log.info("RetentionClientServices Credits and Payment UPDATED");
    };
    
    @Async("asyncExecutor")
    public void updateBill(RetentionClient retentionClient, String document){
        Bill bill = billRepository.findOne(retentionClient.getBill().getId());
        String nbillId = bill.getId().toString();
        if (nbillId.equals(document)){
            saldo = bill.getSaldo();
            valor = Double.parseDouble(saldo);
            sumado = valor - sum;
            BigDecimal vsumado = new BigDecimal(sumado);
            vsumado = vsumado.setScale(2, BigDecimal.ROUND_HALF_UP);
            saldo = String.valueOf(vsumado);
            bill.setSaldo(saldo);
            billRepository.save(bill);
        }
    };
    
    private void populateChildren(RetentionClient retentionClient) {
	log.info("RetentionClientServices populateChildren retentionClientId: {}", retentionClient.getId());
	List<DetailRetentionClient> detailRetentionList = new ArrayList<>();
	Iterable<DetailRetentionClient> retentions = detailRetentionClientRepository.findByRetentionClient(retentionClient);
	retentions.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setRetentionClient(null);
            detailRetentionList.add(detail);
	});

	retentionClient.setDetailRetentionClient(detailRetentionList);
	retentionClient.setFatherListToNull();
	log.info("RetentionClientServices populateChildren FINISHED retentionId: {}", retentionClient.getId());
    };
}
