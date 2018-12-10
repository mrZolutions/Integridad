package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.DetailDebtsToPay;
import com.mrzolution.integridad.app.domain.PagoDebts;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.CreditsDebtsRepository;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayChildRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.PagoDebtsRepository;
import java.util.ArrayList;
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
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    public DebtsToPay getById(UUID id) {
        log.info("DebtsToPayServices getById: {}", id);
        DebtsToPay retrieved = debtsToPayRepository.findOne(id);
        if (retrieved != null) {
            log.info("DebtsToPayServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DebtsToPayServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }

    void savePagosAndCreditsOfDebts(DebtsToPay saved, List<PagoDebts> pagos) {
        pagos.forEach (pago -> {
            List<CreditsDebts> creditsList = pago.getCredits();
            pago.setCredits(null);
            pago.setDebtsToPay(saved);
            PagoDebts pagoSaved = pagoDebtsRepository.save(pago);
            if (creditsList != null) {
                creditsList.forEach (credit -> {
                    credit.setPagoDebts(pagoSaved);
                    creditsDebtsRepository.save(credit);
                });
            }
        });
    }
    
    public Iterable<DebtsToPay> getDebtsByProviderId(UUID id) {
        log.info("DebtsToPayServices getDebtsByProviderId: {}", id);
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsByProviderId(id);
        debts.forEach (debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        return debts;
    }
    
    public DebtsToPay create(DebtsToPay debtsToPay) throws BadRequestException{
        log.info("DebtsToPayServices preparing for create new Debts");
        List<DetailDebtsToPay> details = debtsToPay.getDetailDebtsToPay();
        List<PagoDebts> pagos = debtsToPay.getPagos();
        if (details == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        if (pagos == null) {
            throw new BadRequestException("Debe tener un pago por lo menos");
        }
        debtsToPay.setDetailDebtsToPay(null);
        debtsToPay.setPagos(null);
        debtsToPay.setFatherListToNull();
        debtsToPay.setListsNull();
        DebtsToPay saved = debtsToPayRepository.save(debtsToPay);
        
        Cashier cashier = cashierRepository.findOne(debtsToPay.getUserIntegridad().getCashier().getId());
        cashier.setDebtsNumberSeq(cashier.getDebtsNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        details.forEach (detail -> {
            detail.setDebtsToPay(saved);
            detailDebtsToPayRepository.save(detail);
            detail.setDebtsToPay(null);
        });
        savePagosAndCreditsOfDebts(saved, pagos);
        saved.setDetailDebtsToPay(details);
        log.info("DebtsToPayServices Debts created id: {}", saved.getId());
        return saved;
    }
    
    private void populateChildren(DebtsToPay debtsToPay){
	List<DetailDebtsToPay> detailDebtsToPayList = new ArrayList<>();
	Iterable<DetailDebtsToPay> debts = detailDebtsToPayRepository.findByDebtsToPay(debtsToPay);
	List<PagoDebts> pagoList = getPagosDebtsToPay(debtsToPay);
        debts.forEach (detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDebtsToPay(null);
            detailDebtsToPayList.add(detail);
	});
        debtsToPay.setPagos(pagoList);
	debtsToPay.setDetailDebtsToPay(detailDebtsToPayList);
	debtsToPay.setFatherListToNull();
    }
    
    private List<PagoDebts> getPagosDebtsToPay (DebtsToPay debtsToPay) {
        List<PagoDebts> pagoList = new ArrayList<>(); 
        Iterable<PagoDebts> pagos = pagoDebtsRepository.findByDebtsToPay(debtsToPay);
        pagos.forEach (pago -> {
            if ("credito".equals(pago.getMedio())) {
                Iterable<CreditsDebts> credits = creditsDebtsRepository.findByPagoDebts(pago);
                List<CreditsDebts> creditsList = new ArrayList<>();
                credits.forEach (credit -> {
                    credit.setListsNull();
                    credit.setFatherListToNull();
                    credit.setPagoDebts(null);
                    creditsList.add(credit);
                });
                pago.setCredits(creditsList);
            } else {
                pago.setListsNull();
            }
            pago.setFatherListToNull();
            pago.setDebtsToPay(null);
            pagoList.add(pago);
        });
        return pagoList;
    }
    
}
