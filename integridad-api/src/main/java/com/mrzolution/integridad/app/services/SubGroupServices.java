package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.SubGroup;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import com.mrzolution.integridad.app.repositories.SubGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SubGroupServices {
    @Autowired
    SubGroupRepository subGroupRepository;
    @Autowired
    ProductRepository productRepository;
	
    public SubGroup createSubGroup(SubGroup subGroup) {
	subGroup.setActive(true);
	SubGroup saved = subGroupRepository.save(subGroup);
	saved.setListsNull();
        log.info("SubGroupervices createSubGroup: {}, {}", saved.getId(), saved.getName());
	return saved;
    }
	
    public void updateSubGroup(SubGroup subgroup) {
	log.info("SubGroupervices updateSubGroup: {}", subgroup.getId());
	subgroup.setListsNull();
	SubGroup updated = subGroupRepository.save(subgroup);
	log.info("SubGroupervices updateSubGroup: {}", updated.getId());
    }

    public SubGroup deleteSubGroup(UUID subgroupId) {
	SubGroup findOne = subGroupRepository.findOne(subgroupId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateSubGroup(findOne);
        log.info("SubGroupervices deleteSubGroup: {}", subgroupId);
	return findOne;
    }

    public Iterable<SubGroup> getAllActivesByGroupLineId(UUID groupLineId) {
	Iterable<SubGroup> actives = subGroupRepository.findByGroupLineIdAndActive(groupLineId);
	actives.forEach(this::populateChildren);
        log.info("SubGroupervices getAllActivesByGroupLineId: {}", groupLineId);
	return actives;
    }

    public Iterable<SubGroup> getAllActivesByGroupLineIdLazy(UUID groupLineId) {
	Iterable<SubGroup> actives = subGroupRepository.findByGroupLineIdAndActive(groupLineId);
	actives.forEach(subGroup -> {
            subGroup.setListsNull();
            subGroup.setFatherListToNull();
	});
        log.info("SubGroupervices getAllActivesByGroupLineIdLazy: {}", groupLineId);
	return actives;
    }

    public SubGroup getSubGroupById(UUID id) {
	SubGroup findOne = subGroupRepository.findOne(id);
	populateChildren(findOne);
        log.info("SubGroupervices getSubGroupById: {}", id);
	return findOne;
    }

    private void populateChildren(SubGroup subGroup) {
	List<Product> productList = new ArrayList<>();
	Iterable<Product> products= productRepository.findBySubGroupIdAndActive(subGroup.getId());
	products.forEach(productConsumer -> {
            productConsumer.setListsNull();
            productConsumer.setFatherListToNull();
            productConsumer.setSubgroup(null);
            productList.add(productConsumer);
	});
	subGroup.setProducts(productList);
    }
}