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
    httpCallerService httpCallerService;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    UserClientRepository userClientRepository;
    @Autowired
    BillRepository billRepository;
        
    private String document;
    private String statusCambio;
    private String doc;
    private String saldo;
    private double valor = 0;
    private int numC = 1;
    private double sum = 0;
    private double sumado = 0;
    
    public UUID billId;

    public String getDatil(com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, UUID userClientId) throws Exception {
        UserClient userClient = userClientRepository.findOne(userClientId);
        if (userClient == null) {
            throw new BadRequestException("Empresa Invalida");
        }
        log.info("CreditNoteServices getDatil Empresa valida: {}", userClient.getName());
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(requirement);
        log.info("CreditNoteServices getDatil MAPPER creado");
        
        String response = httpCallerService.post(Constants.DATIL_CREDIT_NOTE_LINK, data, userClient);
        //String response = "OK";
        log.info("CreditNoteServices getDatil httpcall DONE");
        return response;
    }
    
    //Busca CreditNote por ID        
    public CreditNote getCreditNoteById(UUID id) {
        CreditNote retrieved = creditNoteRepository.findOne(id);
        if (retrieved != null) {
            log.info("CreditNoteServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("CreditNoteServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        log.info("CreditNoteServices getCreditNoteById: {}", id);
        return retrieved;
    }
    
    //Busca todas las CreditNote del Cliente
    public Iterable<CreditNote> getCreditNotesByClientId(UUID id) {
        Iterable<CreditNote> credits = creditNoteRepository.findCreditNotesByClientId(id);
        credits.forEach(credit -> {
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        log.info("CreditNoteServices getCreditNotesByClientId: {}", id);
        return credits;
    }

    //Creación de la Nota de Crédito
    public CreditNote createCreditNote(CreditNote creditNote) throws BadRequestException {
        Iterable<CreditNote> credNot = creditNoteRepository.findByDocumentStringSeqAndBillId(creditNote.getDocumentStringSeq(), creditNote.getBillSeq());
        if (Iterables.size(credNot) > 0) {
            throw new BadRequestException("Nota de Cretido de esta Factura Ya Existe");
        } else {
            List<Detail> details = creditNote.getDetails();
            if (details == null) {
                throw new BadRequestException("Debe tener un detalle por lo menos");
            }
            creditNote.setDateCreated(new Date().getTime());
            creditNote.setActive(true);
            creditNote.setDetails(null);
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
            saved.setDetails(details);
            saved.setFatherListToNull();
            
            updateBill(saved);
            Credits validate = creditsRepository.findByBillId(document);
            if (validate != null) {
                updateCreditsAndPayment(saved, document);
            } else {
                log.info("CreditNoteServices DO NOT updateCreditsAndPayment");
            }
            updateProductBySubsidiary(creditNote, details);
            log.info("CreditNoteServices createCreditNote DONE: {}, {}", saved.getId(), saved.getStringSeq());
            return saved;
        }
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(CreditNote creditNote, List<Detail> details) {
        details.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(creditNote.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() + detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("CreditNoteServices updateProductBySubsidiary DONE");
    }
        
    //Actualiza tablas Credits y Payment
    public void updateCreditsAndPayment(CreditNote saved, String document) {
        Credits docNumber = creditsRepository.findByBillId(document);
        doc = docNumber.getBillId();
        if (doc.equals(document) && docNumber.getPayNumber() == numC) {
            valor = docNumber.getValor();
            docNumber.setValor(valor - saved.getTotal());    
            statusCambio = "NOTA DE CREDITO APLICADA";
            docNumber.setStatusCredits(statusCambio);
            Credits spCredits =  creditsRepository.save(docNumber);
            
            Payment specialPayment = new Payment();
            specialPayment.setCredits(spCredits);
            specialPayment.setDatePayment(saved.getDateCreated());
            specialPayment.setNoDocument(saved.getStringSeq());
            specialPayment.setNoAccount("--");
            specialPayment.setDocumentNumber(saved.getDocumentStringSeq());
            specialPayment.setTypePayment("NTC");
            specialPayment.setDetail("ABONO POR NOTA DE CREDITO");
            specialPayment.setModePayment("NTC");
            specialPayment.setValorAbono(0.0);
            specialPayment.setValorReten(0.0);
            specialPayment.setValorNotac(saved.getTotal());
            specialPayment.setActive(true);
            paymentRepository.save(specialPayment);
        }
        log.info("CreditNoteServices updateCreditsAndPayment DONE");
        valor = 0;
    }
    
    //Actualiza tabla Bill
    public void updateBill(CreditNote saved) {
        billId = UUID.fromString(saved.getBillSeq());
        Iterable<Bill> bills = billRepository.findBillById(billId);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
            bill.setCreditNoteApplied(true);
            bill.setCreditNoteId(saved.getId().toString());
            bill.setCreditNoteNumber(saved.getStringSeq());
            bill.setObservation("NOTA DE CRÉDITO APLICADA");
            sum = bill.getTotal() - saved.getTotal();
            BigDecimal vsaldo = new BigDecimal(sum);
            if (sum <= 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else {
                vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            saldo = String.valueOf(vsaldo);
            bill.setSaldo(saldo);
            billRepository.save(bill);
        });
        log.info("CreditNoteServices updateBill DONE");
        sum = 0;
    }
    
    private void populateChildren(CreditNote creditNote) {
        List<Detail> detailList = getDetailsByCreditNote(creditNote);
        creditNote.setDetails(detailList);
        creditNote.setFatherListToNull();
    }

    private List<Detail> getDetailsByCreditNote(CreditNote creditNote) {
        List<Detail> detailList = new ArrayList<>();
        Iterable<Detail> details = detailRepository.findByCreditNote(creditNote);
        details.forEach(detail -> {
            detail.setListsNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setCreditNote(null);
            detailList.add(detail);
        });
        return detailList;
    }
}