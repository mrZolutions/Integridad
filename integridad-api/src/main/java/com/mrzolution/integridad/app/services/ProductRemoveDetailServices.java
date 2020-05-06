package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.ProductRemoveDetail;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.domain.ProductWrapper;
import com.mrzolution.integridad.app.repositories.ProductRemoveDetailRepository;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductRemoveDetailServices {

	@Autowired
	ProductRemoveDetailRepository productRemoveDetailRepository;
	@Autowired
	UserIntegridadRepository userIntegridadRepository;

	@Async("asyncExecutor")
	public ProductRemoveDetail createFromWrapper(ProductWrapper productWrapper){
		log.info("ProductRemoveDetailServices createFromWrapper: {}", productWrapper.getProduct().getId());
		UserIntegridad userIntegridad = userIntegridadRepository.findOne(productWrapper.getUserId());
		ProductRemoveDetail removeDetail = new ProductRemoveDetail();
		removeDetail.setDate(productWrapper.getDate());
		removeDetail.setReason(productWrapper.getRemoveReason());
		removeDetail.setProduct(productWrapper.getProduct());
		removeDetail.setUserIntegridad(userIntegridad);
		ProductRemoveDetail saved = productRemoveDetailRepository.save(removeDetail);
		log.info("ProductRemoveDetailServices created id: {}", saved.getId());
		return saved;
	}
}
