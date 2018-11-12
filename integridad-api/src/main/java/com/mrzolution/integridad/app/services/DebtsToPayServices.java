package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.DetailDebtsToPay;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.DebtsToPayRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayChildRepository;
import com.mrzolution.integridad.app.repositories.DetailDebtsToPayRepository;
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
    
    public DebtsToPay getById(UUID id) {
        log.info("DebtsToPayServices getById: {}", id);
        DebtsToPay retrieved = debtsToPayRepository.findOne(id);
        if(retrieved != null){
            log.info("DebtsToPayServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DebtsToPayServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    };
    
    public DebtsToPay create(DebtsToPay debtsToPay) throws BadRequestException{
        log.info("DebtsToPayServices preparing for create new Debts");
        List<DetailDebtsToPay> details = debtsToPay.getDetailDebtsToPay();
        debtsToPay.setDetailDebtsToPay(null);
        debtsToPay.setFatherListToNull();
        debtsToPay.setListsNull();
        DebtsToPay saved = debtsToPayRepository.save(debtsToPay);
        details.forEach(detail -> {
            detail.setDebtsToPay(saved);
            detailDebtsToPayRepository.save(detail);
            detail.setDebtsToPay(null);
        });
        log.info("DebtsToPayServices Debts created id: {}", saved.getId());
        saved.setDetailDebtsToPay(details);
        return saved;
    };
    
    private void populateChildren(DebtsToPay debtsToPay) {
	log.info("DebtsToPayServices populateChildren debtsToPayId: {}", debtsToPay.getId());
	List<DetailDebtsToPay> detailDebtsToPayList = new ArrayList<>();
	Iterable<DetailDebtsToPay> debts = detailDebtsToPayRepository.findByDebtsToPay(debtsToPay);
	debts.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDebtsToPay(null);
            detailDebtsToPayList.add(detail);
	});

	debtsToPay.setDetailDebtsToPay(detailDebtsToPayList);
	debtsToPay.setFatherListToNull();
	log.info("DebtsToPayServices populateChildren FINISHED debtsToPayId: {}", debtsToPay.getId());
    };
}
