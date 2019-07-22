package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.CreditNoteCellar;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CellarRepository;
import com.mrzolution.integridad.app.repositories.CreditNoteCellarRepository;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class CreditNoteCellarServices {
    @Autowired
    CreditNoteCellarRepository creditNoteCellarRepository;
    @Autowired
    DetailCellarRepository detailCellarRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    CellarRepository cellarRepository;
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    private String document;
    private String statusCambio;
    private String doc;
    private double valor = 0;
    
    public UUID cellarId;
    
    //Busca CreditNote por ID        
    public CreditNoteCellar getCreditNoteCellarById(UUID id) {
        CreditNoteCellar retrieved = creditNoteCellarRepository.findOne(id);
        if (retrieved != null) {
            log.info("CreditNoteCellarServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("CreditNoteCellarServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        log.info("CreditNoteCellarServices getCreditNoteCellarById: {}", id);
        return retrieved;
    }
    
    //Busca todas las CreditNote del Proveedor
    public Iterable<CreditNoteCellar> getCreditNotesCellarByProviderId(UUID id) {
        Iterable<CreditNoteCellar> creditsCellar = creditNoteCellarRepository.findCreditNotesCellarByProviderId(id);
        creditsCellar.forEach(credit -> {
            credit.setListsNull();
            credit.setFatherListToNull();
        });
        log.info("CreditNoteCellarServices getCreditNotesCellarByProviderId: {}", id);
        return creditsCellar;
    }
    
    //Creación de la Nota de Crédito
    public CreditNoteCellar createCreditNoteCellar(CreditNoteCellar creditNoteCellar) throws BadRequestException {
        Iterable<CreditNoteCellar> credNotCellar = creditNoteCellarRepository.findByDocumentStringSeqAndCellarId(creditNoteCellar.getDocumentStringSeq(), creditNoteCellar.getCellarSeq());
        if (Iterables.size(credNotCellar) > 0) {
            throw new BadRequestException("Nota de Cretido de esta Factura Ya Existe");
        } else {
            List<DetailCellar> detailsCellar = creditNoteCellar.getDetailsCellar();
            if (detailsCellar == null) {
                throw new BadRequestException("Debe tener un detalle por lo menos");
            }
            creditNoteCellar.setDateCreated(new Date().getTime());
            creditNoteCellar.setActive(true);
            creditNoteCellar.setDetailsCellar(null);
            creditNoteCellar.setFatherListToNull();
            creditNoteCellar.setListsNull();
            CreditNoteCellar saved = creditNoteCellarRepository.save(creditNoteCellar);
            document = creditNoteCellar.getCellarSeq();
        
            Cashier cashier = cashierRepository.findOne(creditNoteCellar.getUserIntegridad().getCashier().getId());
            cashier.setCreditNoteCellarNumberSeq(cashier.getCreditNoteCellarNumberSeq() + 1);
            cashierRepository.save(cashier);

            detailsCellar.forEach(detail -> {
                detail.setCreditNoteCellar(saved);
                detailCellarRepository.save(detail);
                detail.setCreditNoteCellar(null);
            });
            saved.setDetailsCellar(detailsCellar);
            saved.setFatherListToNull();
            
            updateCellar(saved);
            //Credits validate = creditsRepository.findByBillId(document);
            //if (validate != null) {
            //    updateCreditsAndPayment(saved, document);
            //} else {
            //    log.info("CreditNoteServices DO NOT updateCreditsAndPayment");
            //}
            updateProductBySubsidiary(creditNoteCellar, detailsCellar);
            log.info("CreditNoteServices createCreditNote DONE: {}, {}", saved.getId(), saved.getStringSeq());
            return saved;
        }
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(CreditNoteCellar creditNoteCellar, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(creditNoteCellar.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() - detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("CreditNoteServices updateProductBySubsidiary DONE");
    }
        
    //Actualiza tablas CreditsDebts y PaymentDebts
    //public void updateCreditsDebtsAndPaymentDebts(CreditNoteCellar saved, String document) {
    //    CreditsDebts docNumber = creditsDebtsRepository.findByBillId(document);
    //    doc = docNumber.getBillId();
    //    if (doc.equals(document) && docNumber.getPayNumber() == numC) {
    //        valor = docNumber.getValor();
    //        docNumber.setValor(valor - saved.getTotal());
    //        if (docNumber.getValor() <= 0.01) {
    //            statusCambio = "NOTA DE CREDITO APLICADA";
    //            docNumber.setStatusCredits(statusCambio);
    //        }
    //        CreditsDebts spCredits =  creditsDebtsRepository.save(docNumber);
            
    //        PaymentDebts specialPayment = new PaymentDebts();
    //        specialPayment.setCreditsDebts(spCredits);
    //        specialPayment.setDatePayment(saved.getDateCreated());
    //        specialPayment.setNoDocument(saved.getStringSeq());
    //        specialPayment.setNoAccount(null);
    //        specialPayment.setDocumentNumber(saved.getDocumentStringSeq());
    //        specialPayment.setTypePayment("NTC");
    //        specialPayment.setDetail("ABONO POR NOTA DE CREDITO");
    //        specialPayment.setModePayment("NTC");
    //        specialPayment.setValorAbono(0.0);
    //        specialPayment.setValorReten(0.0);
    //        specialPayment.setValorNotac(saved.getTotal());
    //        specialPayment.setActive(true);
    //        paymentRepository.save(specialPayment);
    //    }
    //    log.info("CreditNoteCellarServices updateCreditsAndPayment DONE");
    //    valor = 0;
    //}
    
    //Actualiza tabla Cellar
    public void updateCellar(CreditNoteCellar saved) {
        cellarId = UUID.fromString(saved.getCellarSeq());
        Iterable<Cellar> cellars = cellarRepository.findCellarById(cellarId);
        cellars.forEach(cellar -> {
            cellar.setListsNull();
            cellar.setFatherListToNull();
            cellar.setCredNotCellarApplied(true);
            cellar.setCredNotCellarId(saved.getId().toString());
            cellar.setCredNotCellarNumber(saved.getStringSeq());
            cellarRepository.save(cellar);
        });
        log.info("CreditNoteCellarServices updateCellar DONE");
    }
    
    private void populateChildren(CreditNoteCellar creditNoteCellar) {
        List<DetailCellar> detailCellarList = getDetailsByCreditNote(creditNoteCellar);
        creditNoteCellar.setDetailsCellar(detailCellarList);
        creditNoteCellar.setFatherListToNull();
    }

    private List<DetailCellar> getDetailsByCreditNote(CreditNoteCellar creditNoteCellar) {
        List<DetailCellar> detailCellarList = new ArrayList<>();
        Iterable<DetailCellar> detailsCellar = detailCellarRepository.findByCreditNoteCellar(creditNoteCellar);
        detailsCellar.forEach(detail -> {
            detail.setListsNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setCreditNoteCellar(null);
            detailCellarList.add(detail);
        });
        return detailCellarList;
    }
}