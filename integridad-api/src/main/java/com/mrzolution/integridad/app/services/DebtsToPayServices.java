package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.report.DebtsReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CreditsDebtsChildRepository;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayChildRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.PagoDebtsChildRepository;
import com.mrzolution.integridad.app.repositories.PagoDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import com.mrzolution.integridad.app.repositories.RetentionRepository;
import com.mrzolution.integridad.app.repositories.DailybookCxPRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@Component
public class DebtsToPayServices {
    @Autowired
    DebtsToPayRepository debtsToPayRepository;
    @Autowired
    DetailDebtsToPayRepository detailDebtsToPayRepository;
    @Autowired
    DetailDebtsToPayChildRepository detailDebtsToPayChildRepository;
    @Autowired
    PagoDebtsRepository pagoDebtsRepository;
    @Autowired
    PagoDebtsChildRepository pagoDebtsChildRepository;
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    CreditsDebtsChildRepository creditsDebtsChildRepository;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    @Autowired
    RetentionRepository retentionRepository;
    @Autowired
    DailybookCeServices dailybookCeServices;
    @Autowired
    ComprobantePagoServices comprobantePagoServices;
    @Autowired
    CashierServices cashierServices;
    @Autowired
    DailybookCxPRepository dailybookCxPRepository;
    
    public String debtsId = "";
    public UUID retentionId;
    
    //Selecciona Debts por Id
    public DebtsToPay getDebtsToPayById(UUID id) {
        DebtsToPay retrieved = debtsToPayRepository.findOne(id);
        if (retrieved != null) {
            log.info("DebtsToPayServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DebtsToPayServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        log.info("DebtsToPayServices getDebtsToPayById: {}", id);
        return retrieved;
    }
    
    //Selección de Debts por Id de Proveedor
    public Iterable<DebtsToPay> getDebtsToPayByProviderId(UUID id) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByProviderId(id);
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByProviderId: {}", id);
        return debts;
    }
    
    public Iterable<DebtsToPay> getDebtsToPayByProdiverIdAndBillNumber(UUID id, String billNum) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByProdiverIdAndBillNumber(id, billNum);
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByProdiverIdAndBillNumber: {}, {}", id, billNum);
        return debts;
    }
    
    //Selección de Debts por Id de Empresa
    public Iterable<DebtsToPay> getDebtsToPayByUserClientId(UUID id) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByUserClientId(id);
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByUserClientId: {}", id);
        return debts;
    }
    
    //Selección de Debts con Saldo (Crédito) por Id de Proveedor
    public Iterable<DebtsToPay> getDebtsToPayByProviderIdWithSaldo(UUID id) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByProviderIdWithSaldo(id);
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByProviderIdWithSaldo: {}", id);
        return debts;
    }
    
    //Busca Debts por Numero de Secuencia y Subsidiaria
    public Iterable<DebtsToPay> getDebtsToPayByDebtsSeqAndSubId(String stringSeq, UUID subId) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByDebtsSeqAndSubsidiaryId(stringSeq, subId);
        debts.forEach(debt -> {
            debt.setFatherListToNull();
            debt.setListsNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByDebtsSeqAndSubId: {}, {}", stringSeq, subId);
        return debts;
    }
    
    //Busca Debts por Nro. Factura y Nro. Autorización
    public Iterable<DebtsToPay> getDebtsToPayByBillNumberAndAuthoNumber(UUID userClientId, String billNumber, String authoNumber) {
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByBillNumberAndAuthoNumber(userClientId, billNumber, authoNumber);
        debts.forEach(debt -> {
            debt.setFatherListToNull();
            debt.setListsNull();
        });
        log.info("DebtsToPayServices getDebtsToPayByBillNumberAndAuthoNumber DONE");
        return debts;
    }
    
    //Creación de los Debts
    public DebtsToPay createDebtsToPay(DebtsToPay debtsToPay) throws BadRequestException {
        List<DetailDebtsToPay> detailDebtsToPay = debtsToPay.getDetailDebtsToPay();
        List<PagoDebts> pagos = debtsToPay.getPagos();
        
        if (detailDebtsToPay == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        debtsToPay.setActive(true);
        debtsToPay.setDetailDebtsToPay(null);
        debtsToPay.setPagos(null);
        debtsToPay.setFatherListToNull();
        debtsToPay.setListsNull();
        DebtsToPay saved = debtsToPayRepository.save(debtsToPay);
        
        Cashier cashier = cashierRepository.findOne(debtsToPay.getUserIntegridad().getCashier().getId());
        cashier.setDebtsNumberSeq(cashier.getDebtsNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDebtsToPay.forEach(detail -> {
            detail.setDebtsToPay(saved);
            detailDebtsToPayRepository.save(detail);
            detail.setDebtsToPay(null);
        });
        
        savePagosAndCreditsOfDebts(saved, pagos);
        if (saved.getRetentionId() != null) {
            updateRetention(saved);
        }
        saved.setDetailDebtsToPay(detailDebtsToPay);

        log.info("DebtsToPayServices createDebtsToPay: {}, {}", saved.getId(), saved.getDebtsSeq());
        return saved;
    }
    
    //Guarda Pagos y Credits de Debts
    void savePagosAndCreditsOfDebts(DebtsToPay saved, List<PagoDebts> pagosDebts) {
        final Boolean[] savePayments = {null, null};
        savePayments[0] = (saved.getRetentionId() != null);
        savePayments[1] = (pagosDebts.size() == 1 && "efectivo".equals(pagosDebts.get(0).getMedio()));
        pagosDebts.forEach(pagoDebts -> {
            List<CreditsDebts> creditsDebtsList = pagoDebts.getCreditsDebts();
            pagoDebts.setCreditsDebts(null);
            pagoDebts.setDebtsToPay(saved);
            PagoDebts pagoDebtSaved = pagoDebtsRepository.save(pagoDebts);
            log.info("DebtsToPayServices savePagosAndCreditsOfDebts pagoDebtSaved: {}", pagoDebtSaved.getId());
            if (creditsDebtsList != null) {
                creditsDebtsList.forEach(creditDebt -> {
                    creditDebt.setPagoDebts(pagoDebtSaved);
                    creditDebt.setDebtsToPayId(saved.getId().toString());
                    if (savePayments[0]) {
                        creditDebt.setValor(creditDebt.getValor() - saved.getRetentionTotal());
                    } else if (savePayments[1]) {
                        creditDebt.setValor(0);
                    }
                    CreditsDebts savedCreditDebt = creditsDebtsRepository.save(creditDebt);
                    log.info("DebtsToPayServices savePagosAndCreditsOfDebts savedCreditDebt: {}", savedCreditDebt.getId());
                    if (savePayments[0]) {
                        PaymentDebts paymentDebt = new PaymentDebts();
                        paymentDebt.setCreditsDebts(savedCreditDebt);
                        paymentDebt.setDatePayment(saved.getFecha());
                        paymentDebt.setNoDocument(saved.getRetentionNumber());
                        paymentDebt.setNoAccount("--");
                        paymentDebt.setDocumentNumber(saved.getBillNumber());
                        paymentDebt.setModePayment("RET");
                        paymentDebt.setTypePayment("RET");
                        paymentDebt.setDetail("ABONO POR RETENCIÓN");
                        paymentDebt.setBanco("--");
                        paymentDebt.setCardBrand("--");
                        paymentDebt.setNumeroLote("--");
                        paymentDebt.setValorAbono(0.0);
                        paymentDebt.setValorReten(saved.getRetentionTotal());
                        paymentDebt.setActive(true);
                        paymentDebtsRepository.save(paymentDebt);
                        savePayments[0] = false;
                        log.info("DebtsToPayServices saveRetentionInPaymentDebts DONE paymentDebt: {}", paymentDebt.getId());
                    }

                    if(savePayments[1]){
                        PaymentDebts paymentDebt = new PaymentDebts();
                        paymentDebt.setCreditsDebts(savedCreditDebt);
                        paymentDebt.setDatePayment(saved.getFecha());
                        paymentDebt.setNoDocument("EFECTIVO");
                        paymentDebt.setNoAccount("--");
                        paymentDebt.setDocumentNumber(saved.getBillNumber());
                        paymentDebt.setModePayment("EFC");
                        paymentDebt.setTypePayment("PAC");
                        paymentDebt.setDetail("PAGO EN EFECTIVO");
                        paymentDebt.setBanco("--");
                        paymentDebt.setCardBrand("--");
                        paymentDebt.setNumeroLote("--");
                        paymentDebt.setValorAbono(saved.getTotal());
                        paymentDebt.setValorReten(0.0);
                        paymentDebt.setActive(true);
                        paymentDebt.setProviderName(saved.getProvider().getName());
                        PaymentDebts paymentSaved = paymentDebtsRepository.save(paymentDebt);
                        log.info("DebtsToPayServices saveRetentionInPaymentDebts EFC paymentSaved: {}", paymentSaved.getId());

                        DailybookCe dailybookCe = new DailybookCe();
                        dailybookCe.setActive(true);
                        dailybookCe.setDateRecordBook(saved.getFecha());
                        dailybookCe.setBillNumber(saved.getBillNumber());
                        dailybookCe.setNameBank("--");
                        dailybookCe.setNumCheque("--");
                        dailybookCe.setClientProvName(saved.getProvider().getName());
                        dailybookCe.setRuc(saved.getProvider().getRuc());
                        dailybookCe.setCodeTypeContab("2");
                        dailybookCe.setTypeContab("COMP. DE EGRESO");
                        dailybookCe.setGeneralDetail(saved.getProvider().getName() + " Fact. " + saved.getBillNumber());

                        String seqString = String.valueOf(saved.getUserIntegridad().getCashier().getDailyCeNumberSeq() + 1);
                        dailybookCe.setDailyCeSeq(seqString);

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 6 - seqString.length(); i++) {
                            sb.append('0');
                        }

                        dailybookCe.setDailyCeStringSeq(sb.toString() + seqString);
                        dailybookCe.setDailyCeStringUserSeq("PAGO GENERADO " + sb.toString() + seqString);
                        dailybookCe.setSubTotalDoce(saved.getSubTotalDoce());
                        dailybookCe.setIva(saved.getIva());
                        dailybookCe.setSubTotalCero(saved.getSubTotalCero());
                        dailybookCe.setTotal(saved.getTotal());
                        dailybookCe.setProvider(saved.getProvider());
                        dailybookCe.setUserIntegridad(saved.getUserIntegridad());
                        dailybookCe.setSubsidiary(saved.getSubsidiary());

                        DetailDailybookContab itema = new DetailDailybookContab();
                        itema.setTypeContab("COMP. DE EGRESO");
                        itema.setCodeConta(paymentSaved.getCtaCtableProvider());
                        itema.setDescrip("PROVEEDORES LOCALES");
                        itema.setTipo("DEBITO (D)");
                        itema.setBaseImponible(saved.getTotal());
                        itema.setName(saved.getProvider().getName() + " Fact. " + saved.getBillNumber());
                        itema.setDeber(String.valueOf(saved.getTotal()));

                        DetailDailybookContab itemb = new DetailDailybookContab();
                        itemb.setTypeContab("COMP. DE EGRESO");
                        itemb.setCodeConta(paymentSaved.getCtaCtableBanco());
                        itemb.setDescrip(paymentSaved.getBanco());
                        itemb.setTipo("CREDITO (C)");
                        itemb.setBaseImponible(saved.getTotal());
                        itemb.setName("Cancela Fact. " + paymentSaved.getDocumentNumber() + ", a: " + paymentSaved.getProviderName() + ", con " + paymentSaved.getModePayment()+ " Nro. " + paymentSaved.getNoDocument());
                        itemb.setHaber(String.valueOf(saved.getTotal()));

                        List<DetailDailybookContab> detailDailybookContab = new ArrayList<>();
                        detailDailybookContab.add(itema);
                        detailDailybookContab.add(itemb);
                        dailybookCe.setDetailDailybookContab(detailDailybookContab);
                        DailybookCe dailySaved = dailybookCeServices.createDailybookCe(dailybookCe);
                        log.info("DebtsToPayServices saveRetentionInPaymentDebts dailySaved CE : {}", dailySaved.getId());

                        ComprobantePago comprobantePago = new ComprobantePago();
                        comprobantePago.setActive(true);
                        comprobantePago.setDateComprobante(saved.getFecha());
                        comprobantePago.setProviderName(paymentSaved.getProviderName());
                        comprobantePago.setProviderRuc(saved.getProvider().getRuc());

                        seqString = String.valueOf(saved.getUserIntegridad().getCashier().getCompPagoNumberSeq() + 1);
                        comprobantePago.setComprobanteSeq(seqString);

                        sb = new StringBuilder();
                        for (int i = 0; i < 6 - seqString.length(); i++) {
                            sb.append('0');
                        };
                        comprobantePago.setComprobanteStringSeq(sb.toString() + seqString);

                        comprobantePago.setComprobanteConcep("Pago de Fact. Nro: " + paymentSaved.getDocumentNumber() + " a: " + paymentSaved.getProviderName());
                        comprobantePago.setComprobanteEstado("PROCESADO");
                        comprobantePago.setBillNumber(paymentSaved.getDocumentNumber());
                        comprobantePago.setNumCheque(paymentSaved.getDocumentNumber());
                        comprobantePago.setNameBank(paymentSaved.getBanco());
                        comprobantePago.setCodeConta(paymentSaved.getCtaCtableBanco());
                        comprobantePago.setPaymentDebtId(String.valueOf(paymentSaved.getId()));
                        comprobantePago.setSubTotalDoce(saved.getSubTotalDoce());
                        comprobantePago.setIva(saved.getIva());
                        comprobantePago.setTotal(saved.getTotal());
                        comprobantePago.setProvider(saved.getProvider());
                        comprobantePago.setUserIntegridad(saved.getUserIntegridad());
                        comprobantePago.setSubsidiary(saved.getSubsidiary());

                        List<DetailComprobantePago> detailComprobantePago = new ArrayList<>();
                        DetailComprobantePago itemac = new DetailComprobantePago();
                        itemac.setCodeConta(paymentSaved.getCtaCtableProvider());
                        itemac.setDescrip("PROVEEDORES LOCALES");
                        itemac.setTipo("DEBITO (D)");
                        itemac.setBaseImponible(saved.getTotal());
                        itemac.setName("Pago de Fact. Nro: " + paymentSaved.getDocumentNumber() + " a: " + paymentSaved.getProviderName());
                        itemac.setDeber(String.valueOf(saved.getTotal()));

                        DetailComprobantePago itembc = new DetailComprobantePago();
                        itembc.setCodeConta(paymentSaved.getCtaCtableBanco());
                        itembc.setDescrip(paymentSaved.getBanco());
                        itembc.setTipo("CREDITO (C)");
                        itembc.setBaseImponible(saved.getTotal());
                        itembc.setName("Cancela Fact. Nro: " + paymentSaved.getDocumentNumber());
                        itembc.setHaber(String.valueOf(saved.getTotal()));

                        detailComprobantePago.add(itemac);
                        detailComprobantePago.add(itembc);
                        comprobantePago.setDetailComprobantePago(detailComprobantePago);

                        ComprobantePago coprobanteSaved = comprobantePagoServices.createComprobantePagoNoAsync(comprobantePago);
                        log.info("DebtsToPayServices saveRetentionInPaymentDebts coprobanteSaved : {}", coprobanteSaved.getId());

                        Cashier cashier = saved.getUserIntegridad().getCashier();
                        cashier.setCompPagoNumberSeq(cashier.getCompPagoNumberSeq() + 1);
                        cashier.setDailyCeNumberSeq(cashier.getDailyCeNumberSeq() + 1);
                        cashierServices.updateCashier(cashier);

                        log.info("DebtsToPayServices saveRetentionInPaymentDebts Cashier sequence Updated ");

                        savePayments[1] =  false;
                        log.info("DebtsToPayServices saveCashInPaymentDebts DONE");
                    }
                });
            } 
        });
        log.info("DebtsToPayServices savePagosAndCreditsOfDebts DONE");
    }
    
    void updateRetention(DebtsToPay saved) {
        retentionId = UUID.fromString(saved.getRetentionId());
        Iterable<Retention> retentions = retentionRepository.findRetentionByIdAndStringSeq(retentionId, saved.getRetentionNumber());
        retentions.forEach(retention -> {
            retention.setListsNull();
            retention.setFatherListToNull();
            retention.setDebtsToPayId(saved.getId().toString());
            retention.setDebtsSeq(saved.getDebtsSeq());
            retentionRepository.save(retention);
        });
        log.info("DebtsToPayServices updateRetention DONE");
    }
    
    //Desactivación o Anulación de los Debts
    public DebtsToPay deactivateDebtsToPay(DebtsToPay debtsToPay) throws BadRequestException {
        if (debtsToPay.getId() == null) {
            throw new BadRequestException("Invalid DebtsToPay");
        }
        DebtsToPay debtsToPayToDeactivate = debtsToPayRepository.findOne(debtsToPay.getId());
        debtsToPayToDeactivate.setListsNull();
        debtsToPayToDeactivate.setActive(false);
        debtsToPayRepository.save(debtsToPayToDeactivate);
        log.info("DebtsToPayServices deactivateDebtsToPay: {}", debtsToPayToDeactivate.getId());
        return debtsToPayToDeactivate;
    }
    
    //Reporte de Compras
    public List<DebtsReport> getDebtsToPayByUserClientIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("DebtsToPayServices getDebtsToPayByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<DebtsReport> debtsReportList = new ArrayList<>();
        debts.forEach(debt -> {
            debt.setListsNull();
            Long endDateLong = debt.getFecha();
            List<PagoDebts> pagosDebts = getPagosDebtsToPay(debt);
            for (PagoDebts pagoDebts: pagosDebts) {
                if (pagoDebts.getCreditsDebts() != null) {
                    for (CreditsDebts creditDebts: pagoDebts.getCreditsDebts()) {
                        if (endDateLong < creditDebts.getFecha()) {
                            endDateLong = creditDebts.getFecha();
                        }
                    }
                }
            }

            Iterable<DailybookCxP> dailys = dailybookCxPRepository.findDailybookCxPByUserClientIdAndProvIdAndBillNumber(userClientId, debt.getProvider().getId(), debt.getBillNumber());
            String[] dailyNumber = {""};
            dailys.forEach(daily -> {
                daily.setFatherListToNull();
                daily.setListsNull();
                dailyNumber[0] = daily.getDailycxpStringSeq();
            });

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(debt.getFecha()));
            String status = debt.isActive() ? "ACTIVA" : "ANULADA";
            String endDate = dateFormat.format(new Date(endDateLong));
            
            DebtsReport debtsReport = new DebtsReport(date, debt.getProvider().getCodeIntegridad(), debt.getProvider().getRazonSocial(), debt.getProvider().getRuc(), debt.getDebtsSeq(), debt.getBillNumber(), debt.getAuthorizationNumber(), debt.getBuyTypeVoucher(), debt.getPurchaseType(),
                                          status, debt.getObservacion(), debt.getRetentionNumber(), debt.getSubTotalDoce(), debt.getIva(), debt.getSubTotalCero(), debt.getTotal(), endDate, debt.getUserIntegridad().getCashier().getNameNumber(), debt.getSubsidiary().getName(),
                                          debt.getUserIntegridad().getFirstName() + " " + debt.getUserIntegridad().getLastName(), dailyNumber[0]);
            
            debtsReportList.add(debtsReport);
        });
        return debtsReportList;
    }
    
    //Carga los Detalles y Pagos hacia un Debt
    private void populateChildren(DebtsToPay debtsToPay) {
        List<PagoDebts> pagoList = getPagosDebtsToPay(debtsToPay);
	List<DetailDebtsToPay> detailDebtsToPayList = new ArrayList<>();
	Iterable<DetailDebtsToPay> debtsDetail = detailDebtsToPayRepository.findByDebtsToPay(debtsToPay);
        debtsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDebtsToPay(null);
            detailDebtsToPayList.add(detail);
	});
        debtsToPay.setPagos(pagoList);
	debtsToPay.setDetailDebtsToPay(detailDebtsToPayList);
	debtsToPay.setFatherListToNull();
    }
    
    //Carga los Pagos y Créditos de un Debt
    private List<PagoDebts> getPagosDebtsToPay (DebtsToPay debtsToPay) {
        List<PagoDebts> pagoDebtsList = new ArrayList<>(); 
        Iterable<PagoDebts> pagosDebts = pagoDebtsRepository.findByDebtsToPay(debtsToPay);
        pagosDebts.forEach(pagoDebt -> {
            if ("credito".equals(pagoDebt.getMedio())) {
                Iterable<CreditsDebts> credits = creditsDebtsRepository.findByPagoDebts(pagoDebt);
                List<CreditsDebts> creditsList = new ArrayList<>();
                credits.forEach(credit -> {
                    credit.setListsNull();
                    credit.setFatherListToNull();
                    credit.setPagoDebts(null);
                    creditsList.add(credit);
                });
                pagoDebt.setCreditsDebts(creditsList);
            } else {
                pagoDebt.setListsNull();
            }
            pagoDebt.setFatherListToNull();
            pagoDebt.setDebtsToPay(null);
            pagoDebtsList.add(pagoDebt);
        });
        return pagoDebtsList;
    }
}