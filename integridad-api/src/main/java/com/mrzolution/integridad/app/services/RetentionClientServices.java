package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import com.mrzolution.integridad.app.repositories.RetentionClientRepository;
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

    double sum = 0.0;
    String document;
    double valor = 0.0;
    String doc;
    long numC = 1;
    
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
      
    public RetentionClient create(RetentionClient retentionClient) throws BadRequestException{
        log.info("RetentionClientServices preparing for create new Retention");
	List<DetailRetentionClient> details = retentionClient.getDetailRetentionClient();
        
        document = retentionClient.getDocumentNumber();
        RetentionClient retrieved = retentionClientRepository.findByDocumentNumber(document);
        
        if (retrieved == null){
            retentionClient.setDocumentDate(new Date().getTime());
            retentionClient.setDetailRetentionClient(null);
            retentionClient.setFatherListToNull();
            retentionClient.setListsNull();
            RetentionClient saved = retentionClientRepository.save(retentionClient);

            details.forEach(detail->{
                detail.setRetentionClient(saved);
                sum += detail.getTotal();
                detailRetentionClientRepository.save(detail);
                detail.setRetentionClient(null);
            });
                                  
            log.info("RetentionClientServices Retention created id: {}", saved.getId());
            saved.setDetailRetentionClient(details);
            updatePayment(retentionClient);
            updateCredits(document);
            return saved;
        } else {
            throw new BadRequestException("Retención ya Existente");
        }
    };
    
    @Async
    public void updateCredits(String document){
        Credits docNumber = creditsRepository.findByDocumentNumber(document);
        doc = docNumber.getDocumentNumber();
        if (doc.equals(document) && docNumber.getPayNumber() == numC){
            valor = docNumber.getValor();
            docNumber.setValor(valor - sum);
            creditsRepository.save(docNumber);
        }
    };
    
    @Async
    public void updatePayment(RetentionClient retentionClient){
        Payment specialPayment = new Payment();
        specialPayment.setCredits(null);
        specialPayment.setCuentaContablePrincipal(null);
        specialPayment.setDatePayment(retentionClient.getDateToday());
        specialPayment.setNoDocument(retentionClient.getRetentionNumber());
        specialPayment.setNoAccount(null);
        specialPayment.setDocumentNumber(retentionClient.getDocumentNumber());
        specialPayment.setTypePayment("RET");
        specialPayment.setDetail("ABONO POR RETENCION");
        specialPayment.setModePayment("RET");
        specialPayment.setValor(sum);
        paymentRepository.save(specialPayment);
        log.info("RetentionClientServices Payment updated");
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
