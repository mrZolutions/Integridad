package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.PagoDebtsRepository;
import com.mrzolution.integridad.app.repositories.PaymentDebtsRepository;
import java.math.BigDecimal;
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
public class PaymentDebtsServices {
    @Autowired
    PaymentDebtsRepository paymentDebtsRepository;
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    PagoDebtsRepository pagoDebtsRepository;
    @Autowired
    DebtsToPayRepository debtsToPayRepository;
    
    private UUID idCreditsDebts;
    private double nume = 0.0;
    private double abono = 0.0;
    private String estadoCambio = "";
    private double resto = 0.0;
    private String document = "";
    private double saldo = 0.0;
    private double sumado = 0.0;
    
    public PaymentDebts createPaymentDebts(PaymentDebts paymentDebts) {
        PaymentDebts saved = paymentDebtsRepository.save(paymentDebts);
        document = saved.getCreditsDebts().getPagoDebts().getDebtsToPay().getId().toString();
        if (saved.getCreditsDebts().getId() != null) {
            idCreditsDebts = saved.getCreditsDebts().getId();
            abono = saved.getValorAbono();
            updateCreditsDebts(idCreditsDebts);
            updateDebtsToPay(paymentDebts, document);
        }
        log.info("PaymentDebtsServices createPaymentDebts DONE id: {}", saved.getId());
        return saved;
    }
    
    public void updateCreditsDebts(UUID creditsDebts) {
        CreditsDebts cambio = creditsDebtsRepository.findOne(idCreditsDebts);
        nume = cambio.getValor();
        resto = nume - abono;
        cambio.setValor(resto);
        creditsDebtsRepository.save(cambio);
        resto = 0.0;
        log.info("PaymentDebtsServices updateCreditsDebts DONE");
    }
    
    public void updateDebtsToPay(PaymentDebts paymentDebts, String document) {
        DebtsToPay debts = debtsToPayRepository.findOne(paymentDebts.getCreditsDebts().getPagoDebts().getDebtsToPay().getId());
        String debtsToPayId = debts.getId().toString();
        if (debtsToPayId.equals(document)) {
            saldo = debts.getSaldo();
            sumado = saldo - abono;
            BigDecimal vsumado = new BigDecimal(sumado);
            vsumado = vsumado.setScale(4, BigDecimal.ROUND_HALF_UP);
            saldo = vsumado.doubleValue();
            debts.setSaldo(saldo);
            debtsToPayRepository.save(debts);
        }
        log.info("PaymentDebtsServices updateDebtsToPay DONE");
    }

}
