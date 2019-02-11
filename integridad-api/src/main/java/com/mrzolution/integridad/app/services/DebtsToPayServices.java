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
    PagoDebtsRepository pagoDebtsRepository;
    @Autowired
    CreditsDebtsRepository creditsDebtsRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    public DebtsToPay getDebtsToPayById(UUID id) {
        log.info("DebtsToPayServices getDebtsToPayById: {}", id);
        DebtsToPay retrieved = debtsToPayRepository.findOne(id);
        if (retrieved != null) {
            log.info("DebtsToPayServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DebtsToPayServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }

    void savePagosAndCreditsOfDebts(DebtsToPay saved, List<PagoDebts> pagosDebts) {
        pagosDebts.forEach(pagoDebt -> {
            List<CreditsDebts> creditsDebtsList = pagoDebt.getCreditsDebts();
            pagoDebt.setCreditsDebts(null);
            pagoDebt.setDebtsToPay(saved);
            PagoDebts pagoDebtSaved = pagoDebtsRepository.save(pagoDebt);
            if (creditsDebtsList != null) {
                creditsDebtsList.forEach(creditDebt -> {
                    creditDebt.setPagoDebts(pagoDebtSaved);
                    creditDebt.setDebtsToPayId(saved.getId().toString());
                    creditsDebtsRepository.save(creditDebt);
                });
            }
        });
        log.info("DebtsToPayServices savePagosAndCreditsOfDebts DONE");
    }
    
    public Iterable<DebtsToPay> getDebtsToPayByProviderId(UUID id) {
        log.info("DebtsToPayServices getDebtsByProviderId: {}", id);
        Iterable<DebtsToPay> debts = debtsToPayRepository.findDebtsToPayByProviderId(id);
        debts.forEach(debt -> {
            debt.setListsNull();
            debt.setFatherListToNull();
        });
        return debts;
    }
    
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
        saved.setDetailDebtsToPay(detailDebtsToPay);
        log.info("DebtsToPayServices createDebtsToPay DONE id: {}", saved.getId());
        return saved;
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
        log.info("DebtsToPayServices deactivateDebtsToPay DONE id: {}", debtsToPayToDeactivate.getId());
        return debtsToPayToDeactivate;
    }
    
    private void populateChildren(DebtsToPay debtsToPay) {
	List<DetailDebtsToPay> detailDebtsToPayList = new ArrayList<>();
	Iterable<DetailDebtsToPay> debts = detailDebtsToPayRepository.findByDebtsToPay(debtsToPay);
	List<PagoDebts> pagoList = getPagosDebtsToPay(debtsToPay);
        debts.forEach(detail -> {
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
