package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class RetentionServices {
	
	@Autowired
	RetentionRepository retentionRepository;
	@Autowired
	DetailRetentionRepository detailRetentionRepository;
	@Autowired
	DetailRetentionChildRepository detailRetentionChildRepository;
	@Autowired
	httpCallerService httpCallerService;
	@Autowired
	CashierRepository cashierRepository;
	@Autowired
	ProductBySubsidiairyRepository productBySubsidiairyRepository;
	@Autowired
	PagoRepository pagoRepository;
	@Autowired
	CreditsRepository creditsRepository;
	@Autowired
	UserClientRepository userClientRepository;

//	public String getDatil(Requirement requirement, UUID userClientId) throws Exception{
//		UserClient userClient = userClientRepository.findOne(userClientId);
//
//		if(userClient == null){
//			throw new BadRequestException("Empresa Invalida");
//		}
//
//		log.info("BillServices getDatil Empresa valida: {}", userClient.getName());
//		if(requirement.getPagos() != null){
//			requirement.getPagos().forEach(pago ->{
//				if("credito".equals(pago.getMedio())){
//					pago.setMedio("otros");
//				}
//			});
//		}
//
//		ObjectMapper mapper = new ObjectMapper();
//		String data = mapper.writeValueAsString(requirement);
//
//		log.info("BillServices getDatil maper creado");
//		String response = httpCallerService.post(Constants.DATIL_LINK, data, userClient);
//		log.info("BillServices getDatil httpcall success");
//		return response;
//	}

	public Iterable<Retention> getByUserLazy(UserIntegridad user){
		log.info("RetentionServices getByUserLazy: {}", user.getId());
		Iterable<Retention> retentions = retentionRepository.findByUserIntegridad(user);
		retentions.forEach(retention->{
			retention.setListsNull();
			retention.setFatherListToNull();
			});

		return retentions;
	}

//	public Iterable<Bill> getByClientIdLazy(UUID id){
//		log.info("BillServices getByClientIdLazy: {}", id);
//		Iterable<Bill> bills = billRepository.findByClientId(id);
//		bills.forEach(bill->{
//			bill.setListsNull();
//			bill.setFatherListToNull();
//		});
//
//		return bills;
//	}

	public Retention getById(UUID id) {
		log.info("RetentionServices getById: {}", id);
		Retention retrieved = retentionRepository.findOne(id);
		if(retrieved != null){
			log.info("RetentionServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("RetentionServices retrieved id NULL: {}", id);
		}

		populateChildren(retrieved);
		return retrieved;
	}

	public Retention create(Retention retention) throws BadRequestException{
		log.info("RetentionServices create");
		List<DetailRetention> details = retention.getDetailRetentions();
		if(details == null){
			throw new BadRequestException("Debe tener una retencion por lo menos");
		}

		retention.setDateCreated(new Date().getTime());
		retention.setActive(true);
		retention.setDetailRetentions(null);
		retention.setFatherListToNull();
		retention.setListsNull();
		Retention saved = retentionRepository.save(retention);

		Cashier cashier = cashierRepository.findOne(retention.getUserIntegridad().getCashier().getId());
		cashier.setBillNumberSeq(cashier.getRetentionNumberSeq() + 1);
		cashierRepository.save(cashier);

		details.forEach(detail->{
			detail.setRetention(saved);
			detailRetentionRepository.save(detail);

			detail.setRetention(null);
		});

		log.info("RetentionServices created id: {}", saved.getId());
		saved.setDetailRetentions(details);
		return saved;
	}

	public Retention update(Retention retention) throws BadRequestException{
		if(retention.getId() == null){
			throw new BadRequestException("Invalid Retention");
		}
		log.info("RetentionServices update: {}", retention.getId());
		Father<Retention, DetailRetention> father = new Father<>(retention, retention.getDetailRetentions());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailRetentionChildRepository, detailRetentionRepository);
        fatherUpdateChildren.updateChildren();

        log.info("RetentionServices CHILDREN updated: {}", retention.getId());

		retention.setListsNull();
		Retention updated = retentionRepository.save(retention);
		log.info("RetentionServices update id: {}", updated.getId());
		return updated;
	}

//
//	public Iterable<Bill> getByStringSeqAndSubId(String stringSeq, UUID subId){
//		log.info("BillServices getByStringSeq : {}, {}", stringSeq, subId);
//		Iterable<Bill> bills = billRepository.findByStringSeqAndSubsidiaryId(stringSeq, subId);
//
//		bills.forEach(retention->{
//			retention.setFatherListToNull();
//			retention.setListsNull();
//		});
//
//		return bills;
//	}
//
	private void populateChildren(Retention retention) {
		log.info("RetentionServices populateChildren retentionId: {}", retention.getId());
		List<DetailRetention> detailRetentionList = new ArrayList<>();
		Iterable<DetailRetention> retentions = detailRetentionRepository.findByRetention(retention);

		retentions.forEach(detail -> {
			detail.setListsNull();
			detail.setFatherListToNull();
			detail.setRetention(null);

			detailRetentionList.add(detail);
		});

		retention.setDetailRetentions(detailRetentionList);
		retention.setFatherListToNull();
		log.info("RetentionServices populateChildren FINISHED retentionId: {}", retention.getId());
	}

}
