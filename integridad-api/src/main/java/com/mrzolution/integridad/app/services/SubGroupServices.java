package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.SubGroup;
import com.mrzolution.integridad.app.repositories.GroupLineRepository;
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
	
	public SubGroup create(SubGroup subGroup){
		log.info("SubGroupervices create");
		subGroup.setActive(true);

		SubGroup saved = subGroupRepository.save(subGroup);

		saved.setListsNull();

		return saved;
	}
	
	public void update(SubGroup subgroup) {
		log.info("SubGroupervices update: {}", subgroup.getId());

		subgroup.setListsNull();
		SubGroup updated = subGroupRepository.save(subgroup);
		log.info("SubGroupervices update id: {}", updated.getId());
	}

	public SubGroup delete(UUID subgroupId) {
		log.info("SubGroupervices delete: {}", subgroupId);
		SubGroup findOne = subGroupRepository.findOne(subgroupId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}

	public Iterable<SubGroup> getAllActivesByGroupLineId(UUID groupLineId) {
		log.info("SubGroupervices getAllActivesByGroupLineId");
		Iterable<SubGroup> actives = subGroupRepository.findByGroupLineIdAndActive(groupLineId);
		actives.forEach(this::populateChildren);
		return actives;

	}

	public Iterable<SubGroup> getAllActivesByGroupLineIdLazy(UUID groupLineId) {
		log.info("SubGroupervices getAllActivesByGroupLineIdLazy");
		Iterable<SubGroup> actives = subGroupRepository.findByGroupLineIdAndActive(groupLineId);
		actives.forEach(subGroup -> {
			subGroup.setListsNull();
			subGroup.setFatherListToNull();
		});
		return actives;

	}

	public SubGroup getById(UUID id) {
		log.info("SubGroupervices getById: {}", id);
		SubGroup findOne = subGroupRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}

	private void populateChildren(SubGroup subGroup) {
		log.info("SubGroupervices populateChildren groupLineId: {}", subGroup.getId());
		List<Product> productList = new ArrayList<>();
		Iterable<Product> products= productRepository.findBySubGroupIdAndActive(subGroup.getId());

		products.forEach(productConsumer -> {
			productConsumer.setListsNull();
			productConsumer.setFatherListToNull();
			productConsumer.setSubgroup(null);

			productList.add(productConsumer);
		});

		subGroup.setProducts(productList);
		log.info("SubGroupervices populateChildren FINISHED lineId: {}", subGroup.getId());

	}
}
