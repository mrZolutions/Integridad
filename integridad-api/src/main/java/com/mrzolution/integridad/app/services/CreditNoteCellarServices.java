package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.CreditNoteCellar;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.domain.Kardex;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.report.CreditNoteCellarReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CellarRepository;
import com.mrzolution.integridad.app.repositories.CreditNoteCellarRepository;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.DetailCellarRepository;
import com.mrzolution.integridad.app.repositories.KardexRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    @Autowired
    DebtsToPayRepository debtsToPayRepositoty;
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    ProductRepository productRepository;
    
    public UUID cellarId;
    public String debtsId;
    public String doc;
    public double valor;
    public String estadoCambio;
    
    public long qntCel;
    public double cstCel;
    public double avgCst;

    
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
    @Async("asyncExecutor")
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
        
            Cashier cashier = cashierRepository.findOne(creditNoteCellar.getUserIntegridad().getCashier().getId());
            cashier.setCreditNoteCellarNumberSeq(cashier.getCreditNoteCellarNumberSeq() + 1);
            cashierRepository.save(cashier);
            
            detailsCellar.forEach(detailC -> {
                detailC.setCreditNoteCellar(saved);
                DetailCellar detCelSaved = detailCellarRepository.save(detailC);
                detailC.setCreditNoteCellar(null);
                Kardex spKarCel = new Kardex();
                    spKarCel.setActive(true);
                    spKarCel.setBill(null);
                    spKarCel.setCellar(null);
                    spKarCel.setConsumption(null);
                    spKarCel.setCredNoteIdVenta("--");
                    spKarCel.setCredNoteIdCompra(saved.getId().toString());
                    spKarCel.setDateRegister(saved.getDateCreated());
                    spKarCel.setDetails("N.C. Nro. " + saved.getNotaCredito() + ", de FACTURA-COMPRA Nro. " + saved.getDocumentStringSeq());
                    spKarCel.setObservation("NCC");
                    spKarCel.setDetalle("--");
                    spKarCel.setProdCostEach(detCelSaved.getCostEach());
                    spKarCel.setProdQuantity(detCelSaved.getQuantity());
                    spKarCel.setProdTotal(detCelSaved.getTotal());
                    spKarCel.setProduct(detCelSaved.getProduct());
                    spKarCel.setProdName(detCelSaved.getProduct().getName());
                    spKarCel.setCodeWarehouse("--");
                    spKarCel.setSubsidiaryId(saved.getSubsidiary().getId().toString());
                    spKarCel.setUserClientId(saved.getSubsidiary().getUserClient().getId().toString());
                    spKarCel.setUserId(saved.getUserIntegridad().getId().toString());
                kardexRepository.save(spKarCel);
                
                Product prod = productRepository.findPrdByUsrClntAndId(saved.getSubsidiary().getUserClient().getId(), detCelSaved.getProduct().getId());
                    qntCel = prod.getQuantityCellar() - detCelSaved.getQuantity();    
                    cstCel = prod.getCostCellar() - detCelSaved.getTotal();
                    avgCst = cstCel / qntCel;
                    BigDecimal vavgCst = new BigDecimal(avgCst);
                    if (avgCst > 0) {
                        vavgCst = vavgCst.setScale(4, BigDecimal.ROUND_HALF_UP);
                    }
                    avgCst = vavgCst.doubleValue();
                    prod.setQuantityCellar(qntCel);
                    prod.setCostCellar(cstCel);
                    prod.setAverageCostSuggested(avgCst);
                productRepository.save(prod);
            });
            
            saved.setDetailsCellar(detailsCellar);
            saved.setFatherListToNull();
            
            updateCellar(saved);
            updateProductBySubsidiary(creditNoteCellar, detailsCellar);
            updateDebtsToPayWithCreditsDebtsAndPaymentDebts(saved);
            log.info("CreditNoteCellarServices createCreditNoteCellar DONE: {}, {}", saved.getId(), saved.getStringSeq());
            return saved;
        }
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(CreditNoteCellar creditNoteCellar, List<DetailCellar> detailsCellar) {
        detailsCellar.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary psNCl = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(creditNoteCellar.getSubsidiary().getId(), detail.getProduct().getId());
                if (psNCl == null) {
                    throw new BadRequestException("ERROR: Producto NO encontrado");
                } else {
                    psNCl.setQuantity(psNCl.getQuantity() - detail.getQuantity());
                    productBySubsidiairyRepository.save(psNCl);
                }
            }
        });
        log.info("CreditNoteCellarServices updateProductBySubsidiary DONE");
    }
    
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
    
    //Actualiza tablas CreditsDebts y PaymentDebts
    public void updateDebtsToPayWithCreditsDebtsAndPaymentDebts(CreditNoteCellar saved) {
        Iterable<DebtsToPay> debts = debtsToPayRepositoty.findDebtsToPayByProdiverIdAndBillNumber(saved.getProvider().getId(), saved.getDocumentStringSeq());
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
            debtsId = debt.getId().toString();
            CreditsDebts creditsDebts = creditsDebtsRepository.findByDebtsToPayId(debtsId);
            doc = creditsDebts.getDebtsToPayId();
            if (doc.equals(debtsId)) {
                creditsDebts.setValor(creditsDebts.getValor() - saved.getTotal());
                estadoCambio = "NOTA DE CREDITO APLICADA";
                creditsDebts.setEstadoCredits(estadoCambio);
                CreditsDebts spCreditsDebts = creditsDebtsRepository.save(creditsDebts);
                valor = spCreditsDebts.getValor();
                PaymentDebts spPaymentDebts = new PaymentDebts();
                    spPaymentDebts.setCreditsDebts(spCreditsDebts);
                    spPaymentDebts.setDatePayment(saved.getDateCreated());
                    spPaymentDebts.setNoDocument(saved.getStringSeq());
                    spPaymentDebts.setNoAccount("--");
                    spPaymentDebts.setDocumentNumber(saved.getDocumentStringSeq());
                    spPaymentDebts.setTypePayment("NTC");
                    spPaymentDebts.setDetail("ABONO POR NOTA DE CREDITO");
                    spPaymentDebts.setModePayment("NTC");
                    spPaymentDebts.setValorAbono(0.0);
                    spPaymentDebts.setValorReten(0.0);
                    spPaymentDebts.setValorNotac(saved.getTotal());
                    spPaymentDebts.setActive(true);
                paymentDebtsRepository.save(spPaymentDebts);
            }
            debt.setCredNoteApplied(true);
            debt.setCredNoteId(saved.getId().toString());
            debt.setCredNoteNumber(saved.getStringSeq());
            debt.setObservacion("NOTA DE CRÉDITO APLICADA" + "--" + debt.getObservacion());
            debt.setSaldo(valor);
            debtsToPayRepositoty.save(debt);
            log.info("CreditNoteCellarServices updateDebtsToPayWithCreditsDebtsAndPaymentDebts DONE");
            valor = 0;
        });
    }
    
    public List<CreditNoteCellarReport> getCreditNotesCellarByUserClientIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("CreditNoteServices getCreditNotesByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<CreditNoteCellar> creditNotesCellar = creditNoteCellarRepository.findCreditNotesCellarByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<CreditNoteCellarReport> creditNoteCellarReportList = new ArrayList<>();
        creditNotesCellar.forEach(creditNote -> {
            creditNote.setListsNull();
            String status = creditNote.isActive() ? "ACTIVA" : "ANULADA";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
            String dateCreated = dateFormat.format(new Date(creditNote.getDateCreated()));
            
            CreditNoteCellarReport creditNoteCellarReport = new CreditNoteCellarReport(creditNote.getStringSeq(), dateCreated, creditNote.getDocumentStringSeq(), status, creditNote.getBaseTaxes(),
                                                                     creditNote.getBaseNoTaxes(), creditNote.getIva(), creditNote.getTotal(), creditNote.getMotivo());
            
            creditNoteCellarReportList.add(creditNoteCellarReport);
        });
        return creditNoteCellarReportList;
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