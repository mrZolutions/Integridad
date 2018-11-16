package com.mrzolution.integridad.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.CreditNote;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import org.springframework.scheduling.annotation.Async;

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
        PaymentRepository paymentRepository;
        @Autowired
        CreditsRepository creditsRepository;
	@Autowired
	UserClientRepository userClientRepository;
        @Autowired
        BillRepository billRepository;
        
        private String document = "";
        private double valor = 0.0;
        private String doc = "";
        private int numC = 1;
        private double sum = 0.0;
        private String saldo = "";
        private double sumado = 0.0;

	public String getDatil(com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, UUID userClientId) throws Exception{
            UserClient userClient = userClientRepository.findOne(userClientId);

            if(userClient == null){
		throw new BadRequestException("Empresa Invalida");
            }

            log.info("CreditNoteServices getDatil Empresa valida: {}", userClient.getName());
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(requirement);
            log.info("CreditNoteServices getDatil maper creado");
		
            //String response = httpCallerService.post(Constants.DATIL_CREDIT_NOTE_LINK, data, userClient);
            String response = "OK";
            log.info("CreditNoteServices getDatil httpcall success");
            return response;
	};
	
	public CreditNote create(CreditNote creditNote) throws BadRequestException{
            log.info("CreditNoteServices create");
            List<Detail> details = creditNote.getDetails();
            if(details == null){
            	throw new BadRequestException("Debe tener un detalle por lo menos");
            }
                
            document = creditNote.getBillSeq();
            creditNote.setDateCreated(new Date().getTime());
            creditNote.setActive(true);
            creditNote.setDetails(null);
            creditNote.setFatherListToNull();
            creditNote.setListsNull();
            CreditNote saved = creditNoteRepository.save(creditNote);

            Cashier cashier = cashierRepository.findOne(creditNote.getUserIntegridad().getCashier().getId());
            cashier.setCreditNoteNumberSeq(cashier.getCreditNoteNumberSeq() + 1);
            cashierRepository.save(cashier);

            details.forEach(detail -> {
		detail.setCreditNote(saved);
		detailRepository.save(detail);
		detail.setCreditNote(null);
            });

            log.info("CreditNoteServices created id: {}", saved.getId());
            updateCreditsAndPayment(saved, document);
            updateBill(creditNote, document);
            saved.setDetails(details);
            saved.setFatherListToNull();

            return saved;
	};
        
        @Async
        public void updateCreditsAndPayment(CreditNote saved, String document){
            Credits docNumber = creditsRepository.findByBillId(document);
            doc = docNumber.getBillId();
            if (doc.equals(document) && docNumber.getPayNumber() == numC){
                valor = docNumber.getValor();
                docNumber.setValor(valor - saved.getTotal());
                Credits spCretits =  creditsRepository.save(docNumber);
            
                Payment specialPayment = new Payment();
                specialPayment.setCredits(spCretits);
                specialPayment.setDatePayment(saved.getDateCreated());
                specialPayment.setNoDocument(saved.getStringSeq());
                specialPayment.setNoAccount(null);
                specialPayment.setDocumentNumber(saved.getDocumentStringSeq());
                specialPayment.setTypePayment("NTC");
                specialPayment.setDetail("ABONO POR NOTA DE CREDITO");
                specialPayment.setModePayment("NTC");
                specialPayment.setValorAbono(0.0);
                specialPayment.setValorReten(0.0);
                specialPayment.setValorNotac(saved.getTotal());
                paymentRepository.save(specialPayment);
            }
            log.info("CreditNoteServices Credits and Payment updated");
            valor = 0.0;
        };
        
        @Async
        public void updateBill(CreditNote creditNote, String document){
            Bill bill = billRepository.findOne(creditNote.getCredits().getPago().getBill().getId());
            String nbillId = bill.getId().toString();
            if (nbillId.equals(document)){
                saldo = bill.getSaldo();
                valor = Double.parseDouble(saldo);
                sumado = valor - sum;
                BigDecimal vsumado = new BigDecimal(sumado);
                vsumado = vsumado.setScale(2, BigDecimal.ROUND_HALF_UP);
                saldo = String.valueOf(vsumado);
                bill.setSaldo(saldo);
                billRepository.save(bill);
            }
            log.info("CreditNoteServices Bill saldo updated");
            sumado = 0.0;
            valor = 0.0;
        };
}
