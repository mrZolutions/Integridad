package com.mrzolution.integridad.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrzolution.integridad.app.domain.CreditNote;
import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.domain.report.ItemReport;
import com.mrzolution.integridad.app.domain.report.SalesReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class CreditNoteServices {

	@Autowired
	CreditNoteRepository creditNoteRepository;
	@Autowired
	DetailRepository detailRepository;
	@Autowired
	DetailChildRepository detailChildRepository;
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

	public String getDatil(com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, UUID userClientId) throws Exception{
		UserClient userClient = userClientRepository.findOne(userClientId);

		if(userClient == null){
			throw new BadRequestException("Empresa Invalida");
		}

		log.info("CreditNoteServices getDatil Empresa valida: {}", userClient.getName());
		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(requirement);

		log.info("CreditNoteServices getDatil maper creado");
		String response = httpCallerService.post(Constants.DATIL_CREDIT_NOTE_LINK, data, userClient);
//		String response = "OK";
		log.info("CreditNoteServices getDatil httpcall success");
		return response;
	}
	
//	public Iterable<Bill> getByUserLazy(UserIntegridad user){
//		log.info("BillServices getByUserLazy: {}", user.getId());
//		Iterable<Bill> bills = billRepository.findByUserIntegridad(user);
//		bills.forEach(bill->{
//			bill.setListsNull();
//			bill.setFatherListToNull();
//			});
//
//		return bills;
//	}
//
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
//
//	public Bill getById(UUID id) {
//		log.info("BillServices getById: {}", id);
//		Bill retrieved = billRepository.findOne(id);
//		if(retrieved != null){
//			log.info("BillServices retrieved id: {}", retrieved.getId());
//		} else {
//			log.info("BillServices retrieved id NULL: {}", id);
//		}
//
//		populateChildren(retrieved);
//		return retrieved;
//	}
//
	public CreditNote create(CreditNote creditNote) throws BadRequestException{
		log.info("CreditNoteServices create");
		List<Detail> details = creditNote.getDetails();
		if(details == null){
			throw new BadRequestException("Debe tener un detalle por lo menos");
		}

		creditNote.setDateCreated(new Date().getTime());
		creditNote.setActive(true);
		creditNote.setDetails(null);
		creditNote.setFatherListToNull();
		creditNote.setListsNull();
		CreditNote saved = creditNoteRepository.save(creditNote);

		Cashier cashier = cashierRepository.findOne(creditNote.getUserIntegridad().getCashier().getId());
		cashier.setCreditNoteNumberSeq(cashier.getCreditNoteNumberSeq() + 1);
		cashierRepository.save(cashier);

		details.forEach(detail->{
			detail.setCreditNote(saved);
			detailRepository.save(detail);

//			if(!detail.getProduct().getProductType().getCode().equals("SER")){
//				ProductBySubsidiary ps =productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(creditNote.getSubsidiary().getId(), detail.getProduct().getId());
//				ps.setQuantity(ps.getQuantity() - detail.getQuantity());
//				productBySubsidiairyRepository.save(ps);
//			}

			detail.setCreditNote(null);
		});

		log.info("CreditNoteServices created id: {}", saved.getId());
		saved.setDetails(details);
		saved.setFatherListToNull();

		return saved;
	}
//
//	public Bill deactivate(Bill bill) throws BadRequestException{
//		if(bill.getId() == null){
//			throw new BadRequestException("Invalid Bill");
//		}
//
//		Bill billToDeactivate = billRepository.findOne(bill.getId());
//		billToDeactivate.setListsNull();
//
//		billToDeactivate.setActive(false);
//
//		billRepository.save(billToDeactivate);
//
//		return billToDeactivate;
//	}
//
//	public Bill update(Bill bill) throws BadRequestException{
//		if(bill.getId() == null){
//			throw new BadRequestException("Invalid Bill");
//		}
//		log.info("BillServices update: {}", bill.getId());
//		Father<Bill, Detail> father = new Father<>(bill, bill.getDetails());
//        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailChildRepository, detailRepository);
//        fatherUpdateChildren.updateChildren();
//
//        log.info("BillServices CHILDREN updated: {}", bill.getId());
//
//		bill.setListsNull();
//		Bill updated = billRepository.save(bill);
//		log.info("BillServices update id: {}", updated.getId());
//		return updated;
//	}
//
//	public Iterable<Bill> getByStringSeqAndSubId(String stringSeq, UUID subId){
//		log.info("BillServices getByStringSeq : {}, {}", stringSeq, subId);
//		Iterable<Bill> bills = billRepository.findByStringSeqAndSubsidiaryId(stringSeq, subId);
//
//		bills.forEach(bill->{
//			bill.setFatherListToNull();
//			bill.setListsNull();
//		});
//
//		return bills;
//	}
//
//	public List<ItemReport> getBySubIdAndDatesActives(UUID userClientId, long dateOne, long dateTwo){
//		log.info("BillServices getByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
//		Iterable<Bill> bills = billRepository.findByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
//
//		Set<UUID> productIds = new HashSet<>();
//		bills.forEach(bill-> {
//			populateChildren(bill);
//
//			for (Detail detail: bill.getDetails()) {
//				productIds.add(detail.getProduct().getId());
//			}
//		});
//
//		return loadListItems(Lists.newArrayList(bills), productIds);
//	}
//
//	public List<SalesReport> getAllBySubIdAndDates(UUID userClientId, long dateOne, long dateTwo){
//		log.info("BillServices getAllBySubIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
//		Iterable<Bill> bills = billRepository.findAllByUserClientIdAndDates(userClientId, dateOne, dateTwo);
//		List<SalesReport> salesReportList = new ArrayList<>();
//
//		bills.forEach(bill-> {
//			bill.setListsNull();
//			Long endDateLong = bill.getDateCreated();
//			List<Pago> pagos = getPagosByBill(bill);
//			for(Pago pago: pagos){
//				if(pago.getCredits() != null){
//					for (Credits credit: pago.getCredits()){
//						if(endDateLong < credit.getFecha()){
//							endDateLong = credit.getFecha();
//						}
//					}
//				}
//			}
//
//			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//			String date = dateFormat.format(new Date(bill.getDateCreated()));
//			String status = bill.isActive() ? "ACTIVA" : "ANULADA";
//			String endDate = dateFormat.format(new Date(endDateLong));
//
//			SalesReport saleReport= new SalesReport(date, bill.getClient().getCodApp(), bill.getClient().getName(), bill.getClient().getIdentification(),
//					bill.getStringSeq(), status, bill.getOtir(), bill.getSubTotal(), bill.getDiscount(), bill.getIva(), bill.getTotal(), endDate, bill.getUserIntegridad().getCashier().getNameNumber(),
//					null, bill.getSubsidiary().getName(), bill.getUserIntegridad().getFirstName() + " " + bill.getUserIntegridad().getLastName());
//
//			salesReportList.add(saleReport);
//		});
//
//		return salesReportList;
//	}
//
//	private void populateChildren(Bill bill) {
//		log.info("BillServices populateChildren billId: {}", bill.getId());
//		List<Detail> detailList = getDetailsByBill(bill);
//        List<Pago> pagoList = getPagosByBill(bill);
//
//		bill.setDetails(detailList);
//		bill.setPagos(pagoList);
//		bill.setFatherListToNull();
//		log.info("BillServices populateChildren FINISHED billId: {}", bill.getId());
//	}
//
//	private List<Detail> getDetailsByBill(Bill bill){
//		List<Detail> detailList = new ArrayList<>();
//		Iterable<Detail> details = detailRepository.findByBill(bill);
//		details.forEach(detail -> {
//			detail.setListsNull();
//			detail.setFatherListToNull();
//			detail.getProduct().setFatherListToNull();
//			detail.getProduct().setListsNull();
//			detail.setBill(null);
//
//			detailList.add(detail);
//		});
//
//		return detailList;
//	}
//
//	private List<Pago> getPagosByBill(Bill bill){
//		List<Pago> pagoList = new ArrayList<>();
//		Iterable<Pago> pagos = pagoRepository.findByBill(bill);
//
//		pagos.forEach(pago ->{
//			if("credito".equals(pago.getMedio())){
//				Iterable<Credits> credits = creditsRepository.findByPago(pago);
//				List<Credits> creditsList = new ArrayList<>();
//
//				credits.forEach(credit ->{
//					credit.setListsNull();
//					credit.setFatherListToNull();
//					credit.setPago(null);
//
//					creditsList.add(credit);
//				});
//
//				pago.setCredits(creditsList);
//			} else {
//				pago.setListsNull();
//			}
//			pago.setFatherListToNull();
//			pago.setBill(null);
//
//			pagoList.add(pago);
//		});
//
//		return pagoList;
//	}
//
//	private List<ItemReport> loadListItems(List<Bill> bills, Set<UUID> productIds){
//		List<ItemReport> reportList = new ArrayList<>();
//
//		for(UUID uuidCurrent: productIds){
//			Double quantityTotal = new Double(0);
//			Double subTotalTotal = new Double(0);
//			Double discountTotal = new Double(0);
//			Double ivaTotal = new Double(0);
//			Double totalTotal = new Double(0);
//			String code = "";
//			String desc = "";
//			for (Bill bill: bills) {
//				for(Detail detail: bill.getDetails()){
//					if(uuidCurrent.equals(detail.getProduct().getId())){
//						Double discount = Double.valueOf(Double.valueOf(bill.getDiscountPercentage())/100) * detail.getTotal();
//						ItemReport item = new ItemReport(detail.getProduct().getId(),"", bill.getStringSeq(), detail.getProduct().getCodeIntegridad(),
//								detail.getProduct().getName(),Double.valueOf(detail.getQuantity()), detail.getCostEach(), detail.getTotal(), discount, ((detail.getTotal()-discount) * 0.12), ((detail.getTotal()-discount) * 1.12));
//						quantityTotal += item.getQuantity();
//						subTotalTotal += item.getSubTotal();
//						discountTotal += item.getDiscount();
//						ivaTotal += item.getIva();
//						totalTotal += item.getTotal();
//						code = detail.getProduct().getCodeIntegridad();
//						desc = detail.getProduct().getName();
//
//						reportList.add(item);
//					}
//				}
//			}
//
//			ItemReport itemTotal = new ItemReport(uuidCurrent, "R", "", code,
//					desc, quantityTotal, null, subTotalTotal, discountTotal, ivaTotal, totalTotal);
//
//			reportList.add(itemTotal);
//		}
//
//		return reportList;
//	}

}
