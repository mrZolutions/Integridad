package com.mrzolution.integridad.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.CreditNote;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CreditNoteServices {
    @Autowired
    CreditNoteRepository creditNoteRepository;
    @Autowired
    DetailRepository detailRepository;
    @Autowired
    DetailChildRepository detailChildRepository;
    @Autowired
    httpCallerService httpCallerService;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    UserClientRepository userClientRepository;
    @Autowired
    BillRepository billRepository;
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    KardexChildRepository kardexChildRepository;
        
    private String document = "";
    private String statusCambio = "";
    private String doc = "";
    private String saldo = "";
    private double valor = 0.0;
    private int numC = 1;
    private double sum = 0.0;
    private double sumado = 0.0;

    public String getDatil(com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, UUID userClientId) throws Exception {
        UserClient userClient = userClientRepository.findOne(userClientId);
        if (userClient == null) {
            throw new BadRequestException("Empresa Invalida");
        }
        log.info("CreditNoteServices getDatil Empresa valida: {}", userClient.getName());
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(requirement);
        log.info("CreditNoteServices getDatil maper creado");
        
        //String response = httpCallerService.post(Constants.DATIL_CREDIT_NOTE_LINK, data, userClient);
        String response = "OK";
        log.info("CreditNoteServices getDatil httpcall success");
        return response;
    }
	
    public CreditNote create(CreditNote creditNote) throws BadRequestException {
        log.info("CreditNoteServices create");            
        Iterable<CreditNote> credNot = creditNoteRepository.findByDocumentStringSeqAndBillId(creditNote.getDocumentStringSeq(), creditNote.getBillSeq());
        if (Iterables.size(credNot) > 0) {
            throw new BadRequestException("Nota de Cretido de esta Factura Ya Existe");
        } else {
            List<Detail> details = creditNote.getDetails();
            if (details == null) {
                throw new BadRequestException("Debe tener un detalle por lo menos");
            }
            List<Kardex> detailsKardex = creditNote.getDetailsKardex();
            creditNote.setDateCreated(new Date().getTime());
            creditNote.setActive(true);
            creditNote.setDetails(null);
            creditNote.setDetailsKardex(null);
            creditNote.setFatherListToNull();
            creditNote.setListsNull();
            CreditNote saved = creditNoteRepository.save(creditNote);
            document = creditNote.getBillSeq();
        
            Cashier cashier = cashierRepository.findOne(creditNote.getUserIntegridad().getCashier().getId());
            cashier.setCreditNoteNumberSeq(cashier.getCreditNoteNumberSeq() + 1);
            cashierRepository.save(cashier);

            details.forEach(detail -> {
                detail.setCreditNote(saved);
                detailRepository.save(detail);
                detail.setCreditNote(null);
            });
            
            detailsKardex.forEach(detail -> {
                detail.setCreditNote(saved);
                detail.setCreditNote(null);
            });

            log.info("CreditNoteServices created id: {}", saved.getId());
            saved.setDetails(details);
            saved.setDetailsKardex(detailsKardex);
            saved.setFatherListToNull();
            
            Credits validate = creditsRepository.findByBillId(document);
            if (validate != null) {
                updateCreditsAndPayment(saved, document);
            } else {
                log.info("CreditNoteServices DO NOT updateCreditsAndPayment");
            }
            return saved;
        }
    }
        
    public void updateCreditsAndPayment(CreditNote saved, String document) throws BadRequestException {
        Credits docNumber = creditsRepository.findByBillId(document);
        doc = docNumber.getBillId();
        if (doc.equals(document) && docNumber.getPayNumber() == numC) {
            valor = docNumber.getValor();
            docNumber.setValor(valor - saved.getTotal());
            if (docNumber.getValor() <= 0.01) {
                statusCambio = "NOTA DE CREDITO APLICADA";
                docNumber.setStatusCredits(statusCambio);
            }
            Credits spCretits =  creditsRepository.save(docNumber);
            
            Payment specialPayment = new Payment();
            specialPayment.setCredits(spCretits);
            specialPayment.setDatePayment(saved.getDateCreated());
            specialPayment.setNoDocument(saved.getStringSeq());
            specialPayment.setNoAccount(null);
            specialPayment.setDocumentNumber(saved.getDocumentStringSeq());
            specialPayment.setTypePayment("NTC");
            specialPayment.setDetail("ABONO POR NOTA DE CREDITO");
            specialPayment.setModePayment("NTC");
            specialPayment.setValorAbono(0.0);
            specialPayment.setValorReten(0.0);
            specialPayment.setValorNotac(saved.getTotal());
            paymentRepository.save(specialPayment);
        }
        log.info("CreditNoteServices updateCreditsAndPayment DONE");
        valor = 0.0;
    }
    
}