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
	groupLine.setActive(true);
	GroupLine saved = groupLineRepository.save(groupLine);
	saved.setListsNull();
        log.info("GroupLineServices createGroupLine: {}, {}", saved.getId(), saved.getName());
	return saved;
    }
	
    public void updateGroupLine(GroupLine groupLine){
	log.info("GroupLineServices updateGroupLine: {}", groupLine.getId());
	groupLine.setListsNull();
	GroupLine updated = groupLineRepository.save(groupLine);
	log.info("GroupLineServices updateGroupLine: {}", updated.getId());
    }

    public GroupLine deleteGroupLine(UUID groupLineId) {
	GroupLine findOne = groupLineRepository.findOne(groupLineId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateGroupLine(findOne);
        log.info("GroupLineServices deleteGroupLine: {}", groupLineId);
	return findOne;
    }

    public Iterable<GroupLine> getAllActivesByLineId(UUID lineId) {
	Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
	actives.forEach(this::populateChildren);
        log.info("GroupLineServices getAllActivesByLineId");
	return actives;
    }

    public Iterable<GroupLine> getAllActivesByLineIdLazy(UUID lineId) {
	Iterable<GroupLine> actives = groupLineRepository.findByLineIdAndActive(lineId);
	actives.forEach(line -> {
            line.setListsNull();
            line.setFatherListToNull();
	});
        log.info("GroupLineServices getAllActivesByLineIdLazy");
	return actives;
    }

    public GroupLine getGroupLineById(UUID id) {
	GroupLine findOne = groupLineRepository.findOne(id);
	populateChildren(findOne);
        log.info("GroupLineServices getGroupLineById: {}", id);
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