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
	
    public GroupLine createGroupLine(GroupLine groupLine) {
	log.info("GroupLineServices createGroupLine");
	groupLine.setActive(true);
	GroupLine saved = groupLineRepository.save(groupLine);
	saved.setListsNull();
	return saved;
    }
	
    public void updateGroupLine(GroupLine groupLine){
	log.info("GroupLineServices updateGroupLine: {}", groupLine.getId());
	groupLine.setListsNull();
	GroupLine updated = groupLineRepository.save(groupLine);
	log.info("GroupLineServices updateGroupLine DONE id: {}", updated.getId());
    }

    public GroupLine deleteGroupLine(UUID groupLineId) {
	log.info("GroupLineServices deleteGroupLine: {}", groupLineId);
	GroupLine findOne = groupLineRepository.findOne(groupLineId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateGroupLine(findOne);
	return findOne;
    }

    public Iterable<GroupLine> getAllActivesByLineId(UUID lineId) {
	log.info("GroupLineServices getAllActivesByLineId");
	Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
	actives.forEach(this::populateChildren);
	return actives;
    }

    public Iterable<GroupLine> getAllActivesByLineIdLazy(UUID lineId) {
	log.info("GroupLineServices getAllActivesByLineIdLazy");
	Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
	actives.forEach(line -> {
            line.setListsNull();
            line.setFatherListToNull();
	});
	return actives;
    }

    public GroupLine getGroupLineById(UUID id) {
	log.info("GroupLineServices getGroupLineById: {}", id);
	GroupLine findOne = groupLineRepository.findOne(id);
	populateChildren(findOne);
	return findOne;
    }

    private void populateChildren(GroupLine groupLine) {
	List<SubGroup> subGroupList = new ArrayList<>();
	Iterable<SubGroup> subGroups= subGroupRepository.findByGroupLineIdAndActive(groupLine.getId());
	subGroups.forEach(subGroupConsumer -> {
            subGroupConsumer.setListsNull();
            subGroupConsumer.setFatherListToNull();
            subGroupConsumer.setGroupLine(null);
            subGroupList.add(subGroupConsumer);
	});
	groupLine.setSubGroups(subGroupList);
    }
}
