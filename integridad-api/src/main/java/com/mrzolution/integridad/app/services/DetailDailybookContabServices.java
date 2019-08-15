package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;
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
public class DetailDailybookContabServices {
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    
    public Iterable<DetailDailybookContab> getCodeContaByDatesAndUserClientId(String code, long dateOne, long dateTwo, String id) {
        Iterable<DetailDailybookContab> details = detailDailybookContabRepository.findCodeContaByDatesAndUserClientId(code, dateOne, dateTwo, id);
        details.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getDailybookCg().setFatherListToNull();
            detail.getDailybookCg().setListsNull();
            detail.getDailybookCe().setFatherListToNull();
            detail.getDailybookCe().setListsNull();
            detail.getDailybookCi().setFatherListToNull();
            detail.getDailybookCi().setListsNull();
            detail.getDailybookCxP().setFatherListToNull();
            detail.getDailybookCxP().setListsNull();
            detail.getDailybookFv().setFatherListToNull();
            detail.getDailybookFv().setListsNull();
        });
        log.info("DetailDailybookContabServices getCodeContaByDatesAndUserClientId: {}", id);
        return details;
    }
}