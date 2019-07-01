package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Warehouse;
import com.mrzolution.integridad.app.repositories.WarehouseRepository;
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
public class WarehouseServices {
    @Autowired
    WarehouseRepository warehouseRepository;
    
    public Warehouse getWarehouseById(UUID id) {
	Warehouse retrieved = warehouseRepository.findOne(id);
	if (retrieved != null) {
            log.info("WarehouseServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("WarehouseServices retrieved id NULL: {}", id);
	}
        log.info("WarehouseServices getWarehouseById DONE: {}", id);
	return retrieved;
    }
      
    public Iterable<Warehouse> getWarehouseByUserClient(UUID id) {
	Iterable<Warehouse> warehouses = warehouseRepository.findWarehouseByUserClientId(id);
	for (Warehouse warehouse : warehouses) {
            warehouse.setListsNull();
            warehouse.setFatherListToNull();
	}
	log.info("WarehouseServices getWarehouseByUserClient DONE");
	return warehouses;
    }
    
}