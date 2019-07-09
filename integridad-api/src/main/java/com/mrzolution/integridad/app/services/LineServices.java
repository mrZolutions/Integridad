package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.Line;
import com.mrzolution.integridad.app.repositories.GroupLineRepository;
import com.mrzolution.integridad.app.repositories.LineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class LineServices {
    @Autowired
    LineRepository lineRepository;
    @Autowired
    GroupLineRepository groupLineRepository;
	
    public Line createLine(Line line) {
	line.setActive(true);
	Line saved = lineRepository.save(line);
	saved.setListsNull();
        log.info("LineServices createLine: {}, {}", saved.getId(), saved.getName());
	return saved;
    }
	
    public void updateLine(Line line) {
	log.info("LineServices updateLine: {}", line.getId());
	line.setListsNull();
	Line updated = lineRepository.save(line);
	log.info("LineServices updateLine: {}", updated.getId());
    }

    public Line deleteLine(UUID lineId) {
	Line findOne = lineRepository.findOne(lineId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateLine(findOne);
        log.info("LineServices deleteLine: {}", lineId);
	return findOne;
    }

    public Iterable<Line> getAllActivesByUserClientId(UUID userClientId) {
	Iterable<Line> actives = lineRepository.findByUserClienteIdAndActive(userClientId);
	actives.forEach(this::populateChildren);
        log.info("LineServices getAllActivesByUserClientId");
	return actives;
    }

    public Iterable<Line> getAllActivesByUserClientIdLazy(UUID userClientId) {
	Iterable<Line> actives = lineRepository.findByUserClienteIdAndActive(userClientId);
	actives.forEach(line -> {
            line.setListsNull();
            line.setFatherListToNull();
	});
        log.info("LineServices getAllActivesByUserClientIdLazy");
	return actives;
    }

    public Line getLineById(UUID id) {
	Line findOne = lineRepository.findOne(id);
	populateChildren(findOne);
        log.info("LineServices getLineById: {}", id);
	return findOne;
    }

    private void populateChildren(Line line) {
	List<GroupLine> groupLineList = new ArrayList<>();
	Iterable<GroupLine> groupLines = groupLineRepository.findByLineIdAndActive(line.getId());
	groupLines.forEach(productConsumer -> {
            productConsumer.setListsNull();
            productConsumer.setFatherListToNull();
            productConsumer.setLine(null);
            groupLineList.add(productConsumer);
	});
	line.setGroupLines(groupLineList);
    }
}