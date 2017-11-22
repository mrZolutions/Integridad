package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.Line;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.repositories.BrandRepository;
import com.mrzolution.integridad.app.repositories.GroupLineRepository;
import com.mrzolution.integridad.app.repositories.LineRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
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
	
	public Line create(Line line){
		log.info("LineServices create");
		line.setActive(true);

		Line saved = lineRepository.save(line);

		saved.setListsNull();

		return saved;
	}
	
	public void update(Line line){
		log.info("LineServices update: {}", line.getId());

		line.setListsNull();
		Line updated = lineRepository.save(line);
		log.info("LineServices update id: {}", updated.getId());
	}

	public Line delete(UUID lineId) {
		log.info("LineServices delete: {}", lineId);
		Line findOne = lineRepository.findOne(lineId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}

	public Iterable<Line> getAllActivesByUserClientId(UUID userClientId){
		log.info("LineServices getAllActivesByUserClientId");
		Iterable<Line> actives = lineRepository.findByUserClienteIdAndActive(userClientId);
		actives.forEach(this::populateChildren);
		return actives;

	}

	public Iterable<Line> getAllActivesByUserClientIdLazy(UUID userClientId){
		log.info("LineServices getAllActivesByUserClientIdLazy");
		Iterable<Line> actives = lineRepository.findByUserClienteIdAndActive(userClientId);
		actives.forEach(line -> {line.setListsNull();});
		return actives;

	}

	public Line getById(UUID id){
		log.info("LineServices getById: {}", id);
		Line findOne = lineRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}

	private void populateChildren(Line line) {
		log.info("LineServices populateChildren lineId: {}", line.getId());
		List<GroupLine> groupLineList = new ArrayList<>();
		Iterable<GroupLine> groupLines = groupLineRepository.findByLineIdAndActive(line.getId());

		groupLines.forEach(productConsumer -> {
			productConsumer.setListsNull();
			productConsumer.setFatherListToNull();
			productConsumer.setLine(null);

			groupLineList.add(productConsumer);
		});

		line.setGroupLines(groupLineList);
		log.info("LineServices populateChildren FINISHED lineId: {}", line.getId());

	}
}
