package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.domain.report.RetentionClientReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.CreditsRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientRepository;
import com.mrzolution.integridad.app.repositories.PaymentRepository;
import com.mrzolution.integridad.app.repositories.RetentionClientRepository;
import java.text.SimpleDateFormat;
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
    
    private double sum = 0;
    private String document = "";
    private double valor = 0;
    private String doc = "";
    private int numC = 1;
    private String saldo = "";
 
    public RetentionClient getRetentionClientById(UUID id) {
	RetentionClient retrieved = retentionClientRepository.findOne(id);
	if (retrieved != null) {
            log.info("RetentionClientServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("RetentionClientServices retrieved id NULL: {}", id);
	}
	populateChildren(retrieved);
        log.info("RetentionClientServices getRetentionClientById DONE: {}", id);
	return retrieved;
    }
    
    public RetentionClient createRetentionClient(RetentionClient retentionClient) throws BadRequestException {   
        Iterable<RetentionClient> retenCli = retentionClientRepository.findByDocumentNumberAndBillId(retentionClient.getDocumentNumber(), retentionClient.getBill().getId());
        
        if (Iterables.size(retenCli) > 0) {
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
                                  
        saved.setDetailRetentionClient(details);
        updateBillCreditsAndPayment(saved, document);
        log.info("RetentionClientServices createRetentionClient DONE: {}, {}", saved.getId(), saved.getRetentionNumber());
        return saved;
    }

    @Async("asyncExecutor")
    public void updateBillCreditsAndPayment(RetentionClient saved, String document) {
        Credits docNumber = creditsRepository.findByBillId(document);
        doc = docNumber.getBillId();
        
        if (doc.equals(document) && docNumber.getPayNumber() == numC) {
            valor = docNumber.getValor();
            docNumber.setValor(valor - sum);
            Credits spCretits =  creditsRepository.save(docNumber);
            
            Payment specialPayment = new Payment();
            specialPayment.setCredits(spCretits);
            specialPayment.setDatePayment(saved.getDateToday());
            specialPayment.setNoDocument(saved.getRetentionNumber());
            specialPayment.setNoAccount(null);
            specialPayment.setDocumentNumber(saved.getDocumentNumber());
            specialPayment.setTypePayment("RET");
            specialPayment.setDetail("ABONO POR RETENCION");
            specialPayment.setModePayment("RET");
            specialPayment.setValorAbono(0.0);
            specialPayment.setValorReten(sum);
            specialPayment.setValorNotac(0.0);
            paymentRepository.save(specialPayment);
            
            if (spCretits != null) {
                Bill bill = billRepository.findOne(saved.getBill().getId());
                String nbillId = bill.getId().toString();
                if (nbillId.equals(document)) {
                    saldo = String.valueOf(spCretits.getValor());
                    bill.setSaldo(saldo);
                    billRepository.save(bill);
                }
            }
        }
        log.info("RetentionClientServices Bill, Credits and Payment UPDATED");
        sum = 0;
        valor = 0;
    }
    
    public List<RetentionClientReport> getRetentionClientByUserClientIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("RetentionServices getRetentionClientByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<RetentionClient> retentionsClient = retentionClientRepository.findRetentionClientByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<RetentionClientReport> retentionClientReportList = new ArrayList<>();
        retentionsClient.forEach(retention -> {
            populateChildren(retention);
            Double sum = Double.valueOf(0);
            Double baseF = Double.valueOf(0);
            Double porcenF = Double.valueOf(0);
            Double subTotalF = Double.valueOf(0);
            Double baseIva = Double.valueOf(0);
            Double porcenIva = Double.valueOf(0);
            Double subTotalIva = Double.valueOf(0);
            String codRetenFuente = null;
            String codRetenIva = null;
            for (DetailRetentionClient detail : retention.getDetailRetentionClient()) {
                sum = Double.sum(sum, detail.getTotal());
                if ("RETENCION EN LA FUENTE".equals(detail.getTaxType())) {
                    baseF = Double.sum(baseF, detail.getBaseImponible());
                    porcenF = Double.sum(porcenF, detail.getPercentage());
                    subTotalF = baseF * (porcenF / 100.00);
                    if (detail.getCode() != null) {
                        codRetenFuente = detail.getCode();
                    } else {
                        codRetenFuente = null;
                    }
                }
                if ("RETENCION EN EL IVA".equals(detail.getTaxType())) {
                    baseIva = Double.sum(baseIva, detail.getBaseImponible());
                    porcenIva = Double.sum(porcenIva, detail.getPercentage());
                    subTotalIva = baseIva * (porcenIva / 100.00);
                    if (detail.getCode() != null) {
                        codRetenIva = detail.getCode();
                    } else {
                        codRetenIva = null;
                    }
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(retention.getDateToday()));
            String docDate = dateFormat.format(new Date(retention.getDocumentDate()));
            RetentionClientReport retentionClReport= new RetentionClientReport(date, docDate, retention.getBill().getClient().getCodApp(), retention.getBill().getClient().getName(), retention.getBill().getClient().getIdentification(), retention.getRetentionNumber(),
                                                                               retention.getDocumentNumber(),retention.getEjercicioFiscal(), codRetenFuente, baseF, porcenF, subTotalF, codRetenIva, baseIva, porcenIva, subTotalIva, sum);

            retentionClientReportList.add(retentionClReport);
        });
        return retentionClientReportList;
    }
    
    private void populateChildren(RetentionClient retentionClient) {
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
    }
}