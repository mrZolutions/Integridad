package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.report.CPResumenPaymentDebtsReport;
import com.mrzolution.integridad.app.domain.report.StatementProviderReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.PagoDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import java.math.BigDecimal;
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
public class PaymentDebtsServices {
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    PagoDebtsRepository pagoDebtsRepository;
    @Autowired
    DebtsToPayRepository debtsToPayRepository;
    
    private double nume = 0;
    private double abono = 0;
    private double resto = 0;
    private String document = "--";
    private String documentDeactivated = "--";
    private double saldo = 0;
    
    private String ruc = "--";
    private String nameProv = "--";
    private String debtsNumber = "--";
    private String billNumber = "--";
    private String retenNumber = "--";
    private String numCheque = "--";
    private String detalle = "--";
    private String observa = "--";
    
    private UUID providerId;
    private double sumTotalAbono = 0;
    private double sumTotalReten = 0;
    private double totalAbono = 0;
    private double totalReten = 0;
    private double totalTotal = 0;
    
    
    public Iterable<PaymentDebts> getPaymentDebtsByUserClientIdWithBankAndNroDocument(UUID userClientId, String banco, String nroduc) {
	Iterable<PaymentDebts> paymentsDebts = paymentDebtsRepository.findPaymentsDebtsByUserClientIdWithBankAndNroDocument(userClientId, banco, nroduc);
	paymentsDebts.forEach(paymentDebt -> {
            paymentDebt.setFatherListToNull();
            paymentDebt.setListsNull();
        });
        log.info("PaymentDebtsServices getPaymentDebtsByUserClientIdWithBankAndNroDocument: {}, {}, {}", userClientId, banco, nroduc);
	return paymentsDebts;
    }
    
    public Iterable<PaymentDebts> getPaymentDebtsByProviderId(UUID id) {
	Iterable<PaymentDebts> paymentsDebts = paymentDebtsRepository.findPaymentsDebtsByProviderId(id);
	paymentsDebts.forEach(paymentDebt -> {
            paymentDebt.setFatherListToNull();
            paymentDebt.setListsNull();
        });
        log.info("PaymentDebtsServices getPaymentDebtsByProviderId: {}", id);
	return paymentsDebts;
    }
    
    public PaymentDebts createPaymentDebts(PaymentDebts paymentDebts) {
        paymentDebts.setActive(true);
        paymentDebts.setFatherListToNull();
        paymentDebts.setListsNull();
        PaymentDebts saved = paymentDebtsRepository.save(paymentDebts);
        document = saved.getCreditsDebts().getPagoDebts().getDebtsToPay().getId().toString();
        if (saved.getCreditsDebts().getId() != null) {
            if ("PAC".equals(saved.getTypePayment())) {
                abono = saved.getValorAbono();
            } else if ("CEG".equals(saved.getTypePayment())) {
                abono = saved.getValorAbono();
            } else {
                abono = saved.getValorReten();
            }
            updateCreditsDebts(saved);
            updateDebtsToPay(saved);
        }
        log.info("PaymentDebtsServices createPaymentDebts: {}, {}", saved.getId(), saved.getDocumentNumber());
        return saved;
    }
    
    public void updateCreditsDebts(PaymentDebts saved) {
        CreditsDebts creditDebt = creditsDebtsRepository.findOne(saved.getCreditsDebts().getId());
        if(abono >= creditDebt.getValor()){
            creditDebt.setEstadoCredits("PAGADO");
        }
        creditDebt.setValor(creditDebt.getValor() - abono);
        creditDebt.setListsNull();
        creditDebt.setFatherListToNull();
        creditsDebtsRepository.save(creditDebt);
        log.info("PaymentDebtsServices updateCreditsDebts DONE");
        resto = 0;
    }
    
    public void updateDebtsToPay(PaymentDebts saved) {
        DebtsToPay debt = debtsToPayRepository.findOne(saved.getCreditsDebts().getPagoDebts().getDebtsToPay().getId());
        Double saldo = Double.valueOf(debt.getSaldo()) - abono;
        BigDecimal vresto = new BigDecimal(saldo);
        if (resto <= 0) {
            vresto = vresto.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            vresto = vresto.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        debt.setSaldo(vresto.doubleValue());
        debt.setListsNull();
        debt.setFatherListToNull();
        debtsToPayRepository.save(debt);
        log.info("PaymentDebtsServices updateDebtsToPay DONE");
        resto = 0;
    }
    
    public List<CPResumenPaymentDebtsReport> getPaymentDebtsByUserClientIdAndDates(UUID id, long dateOne, long dateTwo) {
        sumTotalAbono = 0;
        sumTotalReten = 0;
        totalAbono = 0;
        totalReten = 0;
        
        log.info("PaymentDebtsServices getPaymentDebtsByUserClientIdAndDates: {}, {}, {}", id, dateOne, dateTwo);
        Iterable<PaymentDebts> paymentsDebts = paymentDebtsRepository.findPaymentsDebtsByUserClientIdAndDates(id, dateOne, dateTwo);
        List<CPResumenPaymentDebtsReport> cpResumenPaymentDebtsReportList = new ArrayList<>();
        
        if (Iterables.size(paymentsDebts) > 0) {
            PaymentDebts firstPayment = Iterables.getFirst(paymentsDebts, new PaymentDebts());
            providerId = firstPayment.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getId();
        }
        
        paymentsDebts.forEach(paymentDebt -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaPago = dateFormat.format(new Date(paymentDebt.getDatePayment()));
            
            if (providerId != null && providerId.equals(paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getId())) {
                sumTotalAbono = Double.sum(sumTotalAbono, paymentDebt.getValorAbono());
                sumTotalReten = Double.sum(sumTotalReten, paymentDebt.getValorReten());
            } else {
                providerId = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getId();
                CPResumenPaymentDebtsReport resumenPaymentDebtsReport = new CPResumenPaymentDebtsReport("SUB-TOTAL ", null, null, null, null, null, null, null, null, sumTotalAbono, sumTotalReten);
                cpResumenPaymentDebtsReportList.add(resumenPaymentDebtsReport);
                sumTotalAbono = paymentDebt.getValorAbono();
                sumTotalReten = paymentDebt.getValorReten();
            }
            
            if ("CHQ".equals(paymentDebt.getModePayment())) {
                numCheque = paymentDebt.getNoDocument();
            } else {
                numCheque = "--";
            }
            
            ruc = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getRuc();
            nameProv = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getName();
            debtsNumber = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getDebtsSeq();
            billNumber = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getBillNumber();
            
            CPResumenPaymentDebtsReport resumenPaymentDebtsReport = new CPResumenPaymentDebtsReport(ruc, nameProv, debtsNumber, billNumber, paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getTotal(),
                                                                        paymentDebt.getTypePayment(), paymentDebt.getModePayment(), numCheque, fechaPago, paymentDebt.getValorAbono(), paymentDebt.getValorReten());
            cpResumenPaymentDebtsReportList.add(resumenPaymentDebtsReport);
            
            totalAbono = Double.sum(totalAbono, paymentDebt.getValorAbono());
            totalReten = Double.sum(totalReten, paymentDebt.getValorReten());
        });
        CPResumenPaymentDebtsReport resumenPaymentDebtsReport = new CPResumenPaymentDebtsReport("SUB-TOTAL ", null, null, null, null, null, null, null, null, sumTotalAbono, sumTotalReten);
        cpResumenPaymentDebtsReportList.add(resumenPaymentDebtsReport);
        
        CPResumenPaymentDebtsReport totResumenPaymentDebtsReport = new CPResumenPaymentDebtsReport("TOTAL GENERAL ", null, null, null, null, null, null, null, null, totalAbono, totalReten);
        cpResumenPaymentDebtsReportList.add(totResumenPaymentDebtsReport);
        
        return cpResumenPaymentDebtsReportList;
    }
    
    public List<StatementProviderReport> getStatementProviderReport(UUID id, long dateTwo) {
        totalAbono = 0;
        totalReten = 0;
        saldo = 0;
        
        log.info("PaymentDebtsServices getStatementProviderReport: {}, {}", id, dateTwo);
        Iterable<PaymentDebts> paymentsDebts = paymentDebtsRepository.findStatementProviderReport(id, dateTwo);
        List<StatementProviderReport> statementProviderReportList = new ArrayList<>();
        
        paymentsDebts.forEach(paymentDebt -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaPago = dateFormat.format(new Date(paymentDebt.getDatePayment()));
            String fechaCompra = dateFormat.format(new Date(paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getFecha()));
            String fechaVence = dateFormat.format(new Date(paymentDebt.getCreditsDebts().getFecha()));
                       
            if ("CHQ".equals(paymentDebt.getModePayment())) {
                numCheque = paymentDebt.getNoDocument();
            } else if ("TRF".equals(paymentDebt.getModePayment())) {
                numCheque = paymentDebt.getNoDocument();
            } else if ("DEP".equals(paymentDebt.getModePayment())) {
                numCheque = paymentDebt.getNoDocument();
            } else {
                numCheque = "--";
            }
            
            ruc = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getRuc();
            nameProv = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getProvider().getName();
            debtsNumber = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getDebtsSeq();
            billNumber = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getBillNumber();
            observa = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getObservacion();
            detalle = paymentDebt.getDetail();
            totalTotal = paymentDebt.getCreditsDebts().getPagoDebts().getDebtsToPay().getTotal();
            saldo = totalTotal - (paymentDebt.getValorAbono() + paymentDebt.getValorReten());
            
            if ("RET".equals(paymentDebt.getTypePayment())) {
                retenNumber = paymentDebt.getNoDocument();
                totalTotal = 0;
            } else {
                retenNumber = "--";
            }
            
            StatementProviderReport statementProviderReport = new StatementProviderReport(ruc, nameProv, fechaCompra, fechaVence, fechaPago, billNumber, debtsNumber, 
                                                                        retenNumber, detalle, observa, paymentDebt.getModePayment(), numCheque, totalTotal,
                                                                        paymentDebt.getValorAbono(), paymentDebt.getValorReten(), saldo);
            statementProviderReportList.add(statementProviderReport);
            
            totalAbono = Double.sum(totalAbono, paymentDebt.getValorAbono());
            totalReten = Double.sum(totalReten, paymentDebt.getValorReten());
        });
        //StatementProviderReport statementProviderReport = new StatementProviderReport("TOTAL ", null, null, null, null, null, null, null, null, sumTotal, sumTotalAbono, sumTotalReten, sumTotalValor);
        //statementProviderReportList.add(statementProviderReport);
        
        return statementProviderReportList;
    }
    
    @Async("asyncExecutor")
    public PaymentDebts deactivatePaymentDebts(PaymentDebts payment) throws BadRequestException {
        if (payment.getId() == null) {
            throw new BadRequestException("Invalid PaymentDebts");
        }
        PaymentDebts paymentDebtsToDeactivate = paymentDebtsRepository.findOne(payment.getId());
        paymentDebtsToDeactivate.setListsNull();
        paymentDebtsToDeactivate.setDetail("ABONO ANULADO");
        paymentDebtsToDeactivate.setActive(false);
        PaymentDebts deactivated = paymentDebtsRepository.save(paymentDebtsToDeactivate);
        documentDeactivated = deactivated.getCreditsDebts().getPagoDebts().getDebtsToPay().getId().toString();
        abono = deactivated.getValorAbono();
        updateCreditsDebtsAndDebtsToPayOfPaymentDebtsDeactivated(deactivated, documentDeactivated);
        log.info("PaymentDebtsServices deactivatePaymentDebts: {}", paymentDebtsToDeactivate.getId());
        return paymentDebtsToDeactivate;
    }
    
    public void updateCreditsDebtsAndDebtsToPayOfPaymentDebtsDeactivated(PaymentDebts deactivated, String document) {
        CreditsDebts cambio = creditsDebtsRepository.findOne(deactivated.getCreditsDebts().getId());
        nume = cambio.getValor();
        resto = nume + abono;
        BigDecimal vrestado = new BigDecimal(resto);
        vrestado = vrestado.setScale(2, BigDecimal.ROUND_HALF_UP);
        cambio.setValor(vrestado.doubleValue());
        CreditsDebts creditDebtsSaved = creditsDebtsRepository.save(cambio);
        if (creditDebtsSaved != null) {
            DebtsToPay debts = debtsToPayRepository.findOne(deactivated.getCreditsDebts().getPagoDebts().getDebtsToPay().getId());
            String debtsToPayId = debts.getId().toString();
            if (debtsToPayId.equals(document)) {
                BigDecimal vsumado = new BigDecimal(creditDebtsSaved.getValor());
                vsumado = vsumado.setScale(2, BigDecimal.ROUND_HALF_UP);
                debts.setSaldo(vsumado.doubleValue());
                debtsToPayRepository.save(debts);
            }
        }
        log.info("PaymentDebtsServices updateCreditsDebtsAndDebtsToPayOfPaymentDebtsDeactivated DONE");
        nume = 0;
        resto = 0;
    }
}