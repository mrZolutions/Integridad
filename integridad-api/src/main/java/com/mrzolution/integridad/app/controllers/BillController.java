package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.domain.report.ItemReport;
import com.mrzolution.integridad.app.domain.report.SalesReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/bill")
public class BillController {

	@Autowired
	BillServices service;

	@RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
	public ResponseEntity getDatil(@RequestBody Requirement requirement, @PathVariable("id") UUID userClientId){
		log.info("BillController getAllDatil");
		String response = null;
		try {
			response = service.getDatil(requirement, userClientId);
		}catch(Exception e) {
			log.error("BillController getDatil Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/type_document/{value}")
	public ResponseEntity getByTypeDocument(@PathVariable("value") int value){
		log.info("BillController getByTypeDocument");
		Iterable<Bill> response =null;
		try {
			response = service.getByTypeDocument(value);
		}catch(BadRequestException e) {
			log.error("BillController getByTypeDocument Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);

	}

	@RequestMapping(method = RequestMethod.GET, value="/bill/client/{id}")
	public ResponseEntity getBillByClientId(@PathVariable("id") UUID id){
		log.info("BillController getByClientId: {}", id);
		Iterable<Bill> response = null;
		try {
			response = service.getByClientIdAndTypeLazy(id, 1);
		}catch(BadRequestException e) {
			log.error("BillController getByClientId Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/quotation/client/{id}")
	public ResponseEntity getQuotationByClientId(@PathVariable("id") UUID id){
		log.info("BillController getByClientId: {}", id);
		Iterable<Bill> response = null;
		try {
			response = service.getByClientIdAndTypeLazy(id, 0);
		}catch(BadRequestException e) {
			log.error("BillController getByClientId Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/seq/{seq}/{subId}")
	public ResponseEntity getByStringSeq(@PathVariable("seq") String stringSeq, @PathVariable("subId") UUID subId){
		log.info("BillController getByStringSeq: {}, {}", stringSeq, subId);
		Iterable<Bill> response = null;
		try {
			response = service.getByStringSeqAndSubId(stringSeq, subId);
		}catch(BadRequestException e) {
			log.error("BillController getByStringSeq Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/rep/{userClientId}/{dateOne}/{dateTwo}")
	public ResponseEntity getByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo){
		log.info("BillController getByUserClientIdAndDatesActives: {}, {}, {}", userClientId, dateOne, dateTwo);
		List<ItemReport> response = null;
		try {
			response = service.getBySubIdAndDatesActives(userClientId,dateOne,dateTwo);
		}catch(BadRequestException e) {
			log.error("BillController getByUserClientIdAndDates Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/rep/sales/{userClientId}/{dateOne}/{dateTwo}")
	public ResponseEntity getAllByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo){
		log.info("BillController getByUserClientIdAndDatesActives: {}, {}, {}", userClientId, dateOne, dateTwo);
		List<SalesReport> response = null;
		try {
			response = service.getAllBySubIdAndDates(userClientId, dateOne, dateTwo);
		}catch(BadRequestException e) {
			log.error("BillController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity getById(@PathVariable("id") UUID id){
		log.info("BillController getId: {}", id);
		Bill response = null;
		try {
			response = service.getById(id);
		}catch(BadRequestException e) {
			log.error("BillController getId Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Bill>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.POST, value="/{typeDocument}" )
    public ResponseEntity create(@RequestBody Bill bill, @PathVariable("typeDocument") int typeDocument){
		log.info("BillController create  bill: {} , quotation: {}", bill.getBillSeq(), bill.getQuotationSeq());
		Bill response = null;
		try {
			response = service.create(bill, typeDocument);
		}catch(BadRequestException e) {
			log.error("BillController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Bill>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity dactivate(@RequestBody Bill bill){
		log.info("BillController dactivate: {}", bill.getId());
		try {
			service.deactivate(bill);
		}catch(BadRequestException e) {
			log.error("BillController dactivate Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

}
