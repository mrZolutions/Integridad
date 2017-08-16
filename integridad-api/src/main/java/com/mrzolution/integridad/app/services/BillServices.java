package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.DetailChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BillServices {
	
	@Autowired
	BillRepository billRepository;
	@Autowired
	DetailRepository detailRepository;
	@Autowired
	DetailChildRepository detailChildRepository;
	
	public Iterable<Bill> getByUserLazy(UserIntegridad user){
		log.info("BillServices getByUserLazy: {}", user.getId());
		Iterable<Bill> bills = billRepository.findByUserIntegridad(user);
		bills.forEach(bill->{
			bill.setListsNull();
			bill.setFatherListToNull();
			});
		
		return bills;
	}
	
	public Bill getById(UUID id) {
		log.info("BillServices getById: {}", id);
		Bill retrieved = billRepository.findOne(id);
		if(retrieved != null){
			log.info("BillServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("BillServices retrieved id NULL: {}", id);
		}
		
		populateChildren(retrieved);
		return retrieved;
	}
	
	public Bill create(Bill bill){
		log.info("BillServices create");
		bill.setDateCreated(new Date().getTime());
		bill.setActive(true);
		Bill saved = billRepository.save(bill);
		log.info("BillServices created id: {}", saved.getId());
		return saved;
	}
	
	public Bill update(Bill bill) throws BadRequestException{
		if(bill.getId() == null){
			throw new BadRequestException("Invalid Bill");
		}
		log.info("BillServices update: {}", bill.getId());
		Father<Bill, Detail> father = new Father<>(bill, bill.getDetails());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailChildRepository, detailRepository);
        fatherUpdateChildren.updateChildren();

        log.info("BillServices CHILDREN updated: {}", bill.getId());
        
		bill.setListsNull();
		Bill updated = billRepository.save(bill);
		log.info("BillServices update id: {}", updated.getId());
		return updated;
	}
	
	private void populateChildren(Bill bill) {
		log.info("BillServices populateChildren billId: {}", bill.getId());
		List<Detail> detailList = new ArrayList<>();
		Iterable<Detail> details = detailRepository.findByBill(bill);
		
		details.forEach(detail -> {
			detail.setListsNull();
			detail.setFatherListToNull();
			detail.setBill(null);
			
			detailList.add(detail);
		});
		
		bill.setDetails(detailList);
		bill.setFatherListToNull();
		log.info("BillServices populateChildren FINISHED clientId: {}", bill.getId());
	}

}
