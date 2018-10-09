package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DetailRetentionClient;
import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRetentionClientRepository;
import com.mrzolution.integridad.app.repositories.RetentionClientRepository;
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
public class RetentionClientServices {
    @Autowired
    RetentionClientRepository retentionClientRepository;
    @Autowired
    DetailRetentionClientRepository detailRetentionClientRepository;
    @Autowired
    DetailRetentionClientChildRepository detailRetentionClientChildRepository;
    
    public RetentionClient getById(UUID id) {
	log.info("RetentionClientServices getById: {}", id);
	RetentionClient retrieved = retentionClientRepository.findOne(id);
	if(retrieved != null){
            log.info("RetentionClientServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("RetentionClientServices retrieved id NULL: {}", id);
	}
	populateChildren(retrieved);
	
        return retrieved;
    }
    
    public RetentionClient create(RetentionClient retentionClient) throws BadRequestException{
	log.info("RetentionClientServices create");
	List<DetailRetentionClient> details = retentionClient.getDetailRetentionClient();
	if(details == null){
            throw new BadRequestException("Debe tener Debe tener el codigo de contabilidad una retencion por lo menos");
	}

	retentionClient.setDocumentDate(new Date().getTime());
	retentionClient.setDetailRetentionClient(null);
	retentionClient.setFatherListToNull();
	retentionClient.setListsNull();
	RetentionClient saved = retentionClientRepository.save(retentionClient);

	details.forEach(detail->{
            detail.setRetentionClient(saved);
            detailRetentionClientRepository.save(detail);
            detail.setRetentionClient(null);
	});
	log.info("RetentionClientServices created id: {}", saved.getId());
	saved.setDetailRetentionClient(details);
	
        return saved;
    }
    
    private void populateChildren(RetentionClient retentionClient) {
	log.info("RetentionClientServices populateChildren retentionClientId: {}", retentionClient.getId());
	List<DetailRetentionClient> detailRetentionList = new ArrayList<>();
	Iterable<DetailRetentionClient> retentions = detailRetentionClientRepository.findByRetentionClient(retentionClient);
	retentions.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.setRetentionClient(null);
            detailRetentionList.add(detail);
	});

	retentionClient.setDetailRetentionClient(detailRetentionList);
	retentionClient.setFatherListToNull();
	log.info("RetentionServices populateChildren FINISHED retentionId: {}", retentionClient.getId());
    }
}
