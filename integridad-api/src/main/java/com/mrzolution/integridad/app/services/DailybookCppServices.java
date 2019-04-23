package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DailybookCpp;
import com.mrzolution.integridad.app.domain.DetailDailybookCpp;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DailybookCppRepository;
import com.mrzolution.integridad.app.repositories.DetailDailybookCppRepository;
import java.util.ArrayList;
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
public class DailybookCppServices {
    @Autowired
    DailybookCppRepository dailybookCppRepository;
    @Autowired
    DetailDailybookCppRepository detailDailybookCppRepository;
    @Autowired
    CashierRepository cashierRepository;
    
    //Selecciona Diario CxP por Id
    public DailybookCpp getDailybookCppById(UUID id) {
        log.info("DailybookCppServices getDailybookCppById: {}", id);
        DailybookCpp retrieved = dailybookCppRepository.findOne(id);
        if (retrieved != null) {
            log.info("DailybookCppServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("DailybookCppServices retrieved id NULL: {}", id);
	}
        populateChildren(retrieved);
        return retrieved;
    }
    
    //Selecciona todos los Diarios CxP por UserClientId
    public Iterable<DailybookCpp> getDailybookCppByUserClientId(UUID userClientId) {
        log.info("DailybookCppServices getDailybookCppByUserClientId: {}", userClientId);
        Iterable<DailybookCpp> dailys = dailybookCppRepository.findDailybookCppByUserClientId(userClientId);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Selecciona todos los Diarios CxP por ProviderId
    public Iterable<DailybookCpp> getDailybookCppByProviderId(UUID Id) {
        log.info("DailybookCppServices getDailybookCppByProviderId: {}", Id);
        Iterable<DailybookCpp> dailys = dailybookCppRepository.findDailybookCppByProviderId(Id);
        dailys.forEach(daily -> {
            daily.setListsNull();
            daily.setFatherListToNull();
        });
        return dailys;
    }
    
    //Creación de los Diarios CxP
    public DailybookCpp createDailybookCg(DailybookCpp dailybookCpp) throws BadRequestException {
        List<DetailDailybookCpp> detailDailybookCpp = dailybookCpp.getDetailDailybookCpp();
        
        if (detailDailybookCpp == null) {
            throw new BadRequestException("Debe tener una cuenta por lo menos");
        }
        
        dailybookCpp.setActive(true);
        dailybookCpp.setDetailDailybookCpp(null);
        dailybookCpp.setFatherListToNull();
        dailybookCpp.setListsNull();
        DailybookCpp saved = dailybookCppRepository.save(dailybookCpp);
        
        Cashier cashier = cashierRepository.findOne(dailybookCpp.getUserIntegridad().getCashier().getId());
        cashier.setDailyCppNumberSeq(cashier.getDailyCppNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        detailDailybookCpp.forEach(detail -> {
            detail.setDailybookCpp(saved);
            detailDailybookCppRepository.save(detail);
            detail.setDailybookCpp(null);
        });
        
        saved.setDetailDailybookCpp(detailDailybookCpp);
        log.info("DailybookCppServices createDailybookCpp DONE id: {}", saved.getId());
        return saved;
    }
    
    //Desactivación o Anulación de los Diarios CxP
    public DailybookCpp deactivateDailybookCpp(DailybookCpp dailybookCpp) throws BadRequestException {
        if (dailybookCpp.getId() == null) {
            throw new BadRequestException("Invalid DailybookCpp");
        }
        DailybookCpp dailybookCppToDeactivate = dailybookCppRepository.findOne(dailybookCpp.getId());
        dailybookCppToDeactivate.setListsNull();
        dailybookCppToDeactivate.setActive(false);
        dailybookCppRepository.save(dailybookCppToDeactivate);
        log.info("DailybookCppServices deactivateDailybookCpp DONE id: {}", dailybookCppToDeactivate.getId());
        return dailybookCppToDeactivate;
    }
    
    //Carga los Detalles hacia un Diario CxP
    private void populateChildren(DailybookCpp dailybookCpp) {
	List<DetailDailybookCpp> detailDailybookCppList = new ArrayList<>();
	Iterable<DetailDailybookCpp> dailybookCppsDetail = detailDailybookCppRepository.findByDailybookCpp(dailybookCpp);
        dailybookCppsDetail.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setDailybookCpp(null);
            detailDailybookCppList.add(detail);
	});
	dailybookCpp.setDetailDailybookCpp(detailDailybookCppList);
	dailybookCpp.setFatherListToNull();
    }
}