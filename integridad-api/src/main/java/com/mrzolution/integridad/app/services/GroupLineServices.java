package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.SubGroup;
import com.mrzolution.integridad.app.repositories.GroupLineRepository;
import com.mrzolution.integridad.app.repositories.SubGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class GroupLineServices {

	@Autowired
	GroupLineRepository groupLineRepository;
	@Autowired
	SubGroupRepository subGroupRepository;
	
	public GroupLine create(GroupLine groupLine){
		log.info("GroupLineServices create");
		groupLine.setActive(true);

		GroupLine saved = groupLineRepository.save(groupLine);

		saved.setListsNull();

		return saved;
	}
	
	public void update(GroupLine groupLine){
		log.info("GroupLineServices update: {}", groupLine.getId());

		groupLine.setListsNull();
		GroupLine updated = groupLineRepository.save(groupLine);
		log.info("GroupLineServices update id: {}", updated.getId());
	}

	public GroupLine delete(UUID groupLineId) {
		log.info("GroupLineServices delete: {}", groupLineId);
		GroupLine findOne = groupLineRepository.findOne(groupLineId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}

	public Iterable<GroupLine> getAllActivesByLineId(UUID lineId){
		log.info("GroupLineServices getAllActivesByLineId");
		Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
		actives.forEach(this::populateChildren);
		return actives;

	}

	public Iterable<GroupLine> getAllActivesByLineIdLazy(UUID lineId){
		log.info("GroupLineServices getAllActivesByLineIdLazy");
		Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
		actives.forEach(line -> {
			line.setListsNull();
			line.setFatherListToNull();
		});
		return actives;

	}

	public GroupLine getById(UUID id){
		log.info("GroupLineServices getById: {}", id);
		GroupLine findOne = groupLineRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}

	private void populateChildren(GroupLine groupLine) {
		log.info("GroupLineServices populateChildren groupLineId: {}", groupLine.getId());
		List<SubGroup> subGroupList = new ArrayList<>();
		Iterable<SubGroup> subGroups= subGroupRepository.findByGroupLineIdAndActive(groupLine.getId());

		subGroups.forEach(subGroupConsumer -> {
			subGroupConsumer.setListsNull();
			subGroupConsumer.setFatherListToNull();
			subGroupConsumer.setGroupLine(null);

			subGroupList.add(subGroupConsumer);
		});

		groupLine.setSubGroups(subGroupList);
		log.info("GroupLineServices populateChildren FINISHED lineId: {}", groupLine.getId());

	}
}
