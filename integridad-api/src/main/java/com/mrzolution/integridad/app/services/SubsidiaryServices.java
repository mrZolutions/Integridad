package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubsidiaryServices {
    @Autowired
    SubsidiaryRepository subsidiaryRepository;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    CashierChildRepository cashierChildRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseChildRepository warehouseChildRepository;
    @Autowired
    UserIntegridadRepository userIntegridadRepository;

    public Subsidiary createSubsidiary(Subsidiary subsidiary) {
	subsidiary.setDateCreated(new Date().getTime());
	subsidiary.setActive(true);

	List<Cashier> cashierList = subsidiary.getCashiers();
	List<Warehouse> warehouseList = subsidiary.getWarehouses();
	subsidiary.setCashiers(null);
	subsidiary.setWarehouses(null);

	if (cashierList == null || cashierList.isEmpty()) {
            throw new BadRequestException("La sucursal debe tener por lo menos una caja");
	}
	
        Subsidiary saved = subsidiaryRepository.save(subsidiary);
	cashierList.forEach(cashier -> {
            cashier.setSubsidiary(saved);
            cashierRepository.save(cashier);
            cashier.setSubsidiary(null);
	});
	warehouseList.forEach (warehouse -> {
            warehouse.setSubsidiary(subsidiary);
            warehouseRepository.save(warehouse);
            warehouse.setSubsidiary(null);
	});
	log.info("SubsidiaryServices createSubsidiary: {}", saved.getId());
	saved.setCashiers(cashierList);
	return saved;
    }
	
    public Iterable<Subsidiary> getAllActivesByUserClientId(UUID userClientId) {
	Iterable<Subsidiary> subsidiaries = subsidiaryRepository.findByUserClientIdAndActive(userClientId, true);
	subsidiaries.forEach(subsidiary -> {
            populateChildren(subsidiary);
	});
	log.info("SubsidiaryServices getAllActivesByUserClientId: {}", userClientId);
	return subsidiaries;
    }

    public Subsidiary getSubsidiaryById(UUID id) {
	Subsidiary subsidiary = subsidiaryRepository.findOne(id);
	subsidiary.setFatherListToNull();
	subsidiary.setListsNull();
        log.info("SubsidiaryServices getSubsidiaryById: {}", id);
	return subsidiary;
    }

    public Subsidiary updateSubsidiary(Subsidiary subsidiary) throws BadRequestException {
	if (subsidiary.getId() == null) {
            throw new BadRequestException("Invalid subsidiary");
	}
	log.info("SubsidiaryServices updateSubsidiary: {}", subsidiary.getName());
	Father<Subsidiary, Cashier> father = new Father<>(subsidiary, subsidiary.getCashiers());
	FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, cashierChildRepository, cashierRepository);
	fatherUpdateChildren.updateChildren();

	Father<Subsidiary, Warehouse> father2 = new Father<>(subsidiary, subsidiary.getWarehouses());
	fatherUpdateChildren = new FatherManageChildren(father2, warehouseChildRepository, warehouseRepository);
	fatherUpdateChildren.updateChildren();

	log.info("SubsidiaryServices CHILDREN updated: {}", subsidiary.getId());

	subsidiary.setListsNull();
	Subsidiary updated = subsidiaryRepository.save(subsidiary);
	log.info("SubsidiaryServices updated: {}", updated.getId());
	return updated;
    }

    private void populateChildren(Subsidiary subsidiary) {
	List<Cashier> cashierList = new ArrayList<>();
	Iterable<Cashier> cashiers = cashierRepository.findBySubsidiary(subsidiary);
	List<Warehouse> warehouseList = new ArrayList<>();
	Iterable<Warehouse> warehouses = warehouseRepository.findWarehouseBySubsidiary(subsidiary);
	List<UserIntegridad> userIntegridadList = new ArrayList<>();
	Iterable<UserIntegridad> userIntegridads = userIntegridadRepository.findBySubsidiary(subsidiary);

	cashiers.forEach(cashierConsumer -> {
            cashierConsumer.setListsNull();
            cashierConsumer.setFatherListToNull();
            cashierConsumer.setSubsidiary(null);
            cashierList.add(cashierConsumer);
	});

	warehouses.forEach(warehouseConsumer -> {
            warehouseConsumer.setListsNull();
            warehouseConsumer.setFatherListToNull();
            warehouseConsumer.setSubsidiary(null);
            warehouseList.add(warehouseConsumer);
	});

	userIntegridads.forEach(userConsumer -> {
            userConsumer.setListsNull();
            userConsumer.setFatherListToNull();
            userConsumer.setSubsidiary(null);
            userIntegridadList.add(userConsumer);
	});

	subsidiary.setCashiers(cashierList);
	subsidiary.setWarehouses(warehouseList);
	subsidiary.setUsers(userIntegridadList);
	subsidiary.setFatherListToNull();
    }
    
}