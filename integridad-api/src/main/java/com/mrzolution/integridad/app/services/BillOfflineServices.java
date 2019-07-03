package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.DetailOffline;
import com.mrzolution.integridad.app.domain.PagoOffline;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillOfflineRepository;
import com.mrzolution.integridad.app.repositories.CashierRepository;
import com.mrzolution.integridad.app.repositories.DetailOfflineChildRepository;
import com.mrzolution.integridad.app.repositories.DetailOfflineRepository;
import com.mrzolution.integridad.app.repositories.PagoOfflineRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class BillOfflineServices {
    @Autowired
    BillOfflineRepository billOfflineRepository;
    @Autowired
    DetailOfflineRepository detailOfflineRepository;
    @Autowired
    DetailOfflineChildRepository detailOfflineChildRepository;
    @Autowired
    CashierRepository cashierRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    PagoOfflineRepository pagoOfflineRepository;
    @Autowired
    UserClientRepository userClientRepository;
    
    public Iterable<BillOffline> getBillsOfflineByTypeDocument(int value) {
        Iterable<BillOffline> billsOffline = billOfflineRepository.findBillsOfflineByTypeDocument(value);
        billsOffline.forEach(billOffline -> {
            billOffline.setListsNull();
            billOffline.setFatherListToNull();
        });
        log.info("BillOfflineServices getBillsOfflineByTypeDocument: {}", value);
        return billsOffline;
    }
    
    //Buscar todas las BillOffline por ID de UserIntegridad
    public Iterable<BillOffline> getBillsOfflineByUserIntegridad(UserIntegridad user) {
        Iterable<BillOffline> billsOffline = billOfflineRepository.findBillsOfflineByUserIntegridad(user);
        billsOffline.forEach(billOffline -> {
            billOffline.setListsNull();
            billOffline.setFatherListToNull();
        });
        log.info("BillOfflineServices getBillsOfflineByUserIntegridad: {}", user.getId());
        return billsOffline;
    }
    
    public Iterable<BillOffline> getBillsOfflineByStringSeqAndSubId(String stringSeq, UUID subId) {
        Iterable<BillOffline> billsOffline = billOfflineRepository.findBillsOfflineByStringSeqAndSubsidiaryId(stringSeq, subId);
        billsOffline.forEach(billOffline -> {
            billOffline.setFatherListToNull();
            billOffline.setListsNull();
        });
        log.info("BillOfflineServices getByStringSeq: {}, {}", stringSeq, subId);
        return billsOffline;
    }
    
    //Buscar todas las BillOffline por ID de Cliente
    public Iterable<BillOffline> getBillsOfflineByClientId(UUID id, int type) {
        Iterable<BillOffline> billsOffline = billOfflineRepository.findBillsOfflineByClientId(id, type);
        billsOffline.forEach(billOffline -> {
            billOffline.setListsNull();
            billOffline.setFatherListToNull();
        });
        log.info("BillOfflineServices getBillsOfflineByClientId: {}", id);
        return billsOffline;
    }
    
    //Bucar BillsOffline por ID        
    public BillOffline getBillOfflineById(UUID id) {
        BillOffline retrieved = billOfflineRepository.findOne(id);
        if (retrieved != null) {
            log.info("BillOfflineServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("BillOfflineServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        log.info("BillOfflineServices getBillOfflineById: {}", id);
        return retrieved;
    }
    
    private void populateChildren(BillOffline billOffline) {
        List<DetailOffline> detailOfflineList = getDetailsOfflineByBill(billOffline);
        List<PagoOffline> pagoOfflineList = getPagosOfflineByBill(billOffline);
        billOffline.setDetailsOffline(detailOfflineList);
        billOffline.setPagosOffline(pagoOfflineList);
        billOffline.setFatherListToNull();
    }

    private List<DetailOffline> getDetailsOfflineByBill(BillOffline billOffline) {
        List<DetailOffline> detailOfflineList = new ArrayList<>();
        Iterable<DetailOffline> detailsOffline = detailOfflineRepository.findByBillOffline(billOffline);
        detailsOffline.forEach(detailOffline -> {
            detailOffline.setListsNull();
            detailOffline.setFatherListToNull();
            detailOffline.getProduct().setFatherListToNull();
            detailOffline.getProduct().setListsNull();
            detailOffline.setBillOffline(null);
            detailOfflineList.add(detailOffline);
        });
        return detailOfflineList;
    }
       
    private List<PagoOffline> getPagosOfflineByBill(BillOffline billOffline) {
        List<PagoOffline> pagoOfflineList = new ArrayList<>();
        Iterable<PagoOffline> pagosOffline = pagoOfflineRepository.findByBillOffline(billOffline);
        pagosOffline.forEach(pagoOffline -> {
            pagoOffline.setFatherListToNull();
            pagoOffline.setBillOffline(null);
            pagoOfflineList.add(pagoOffline);
        });
        return pagoOfflineList;
    }
    
    //Inicio de Creación de las BillsOffline    
    @Async("asyncExecutor")
    public BillOffline createBillOffline(BillOffline billOffline, int typeDocument) throws BadRequestException {
        List<DetailOffline> detailsOffline = billOffline.getDetailsOffline();
        List<PagoOffline> pagosOffline = billOffline.getPagosOffline();
        if (detailsOffline == null) {
            throw new BadRequestException("Debe tener un detalle por lo menos");
        }
        if (typeDocument == 1 && pagosOffline == null) {
            throw new BadRequestException("Debe tener un pago por lo menos");
        }
        billOffline.setDateCreated(new Date().getTime());
        billOffline.setTypeDocument(typeDocument);
        billOffline.setActive(true);
        billOffline.setDetailsOffline(null);
        billOffline.setPagosOffline(null);
        billOffline.setFatherListToNull();
        billOffline.setListsNull();
        BillOffline saved = billOfflineRepository.save(billOffline);

        Cashier cashier = cashierRepository.findOne(billOffline.getUserIntegridad().getCashier().getId());
        cashier.setBillOfflineNumberSeq(cashier.getBillOfflineNumberSeq() + 1);
        cashierRepository.save(cashier);
        saveDetailsOfflineOfBillOffline(saved, detailsOffline);
        savePagosOfflineOfBillOffline(saved, pagosOffline);
        updateProductBySubsidiary(billOffline, typeDocument, detailsOffline);
        
        log.info("BillOfflineServices createBillOffline: {}, {}", saved.getId(), saved.getStringSeq());
        return saved;
    }
    
    //Almacena los Detalles de la Factura
    public void saveDetailsOfflineOfBillOffline(BillOffline saved, List<DetailOffline> detailsOffline) {
        detailsOffline.forEach(detailOffline-> {
            detailOffline.setBillOffline(saved);
            detailOfflineRepository.save(detailOffline);
            detailOffline.setBillOffline(null);
        });
        saved.setDetailsOffline(detailsOffline);
        log.info("BillOfflineServices saveDetailsOfflineOfBillOffline");
    }
    
    //Guarda el tipo de Pago y Credits
    public void savePagosOfflineOfBillOffline(BillOffline saved, List<PagoOffline> pagosOffline) {
        pagosOffline.forEach(pagoOffline -> {
            pagoOffline.setBillOffline(saved);
            PagoOffline pagoSaved = pagoOfflineRepository.save(pagoOffline);		
        });
        log.info("BillOfflineServices savePagosOfflineOfBillOffline");
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(BillOffline billOffline, int typeDocument, List<DetailOffline> detailsOffline) {
        detailsOffline.forEach(detailOffline-> {
            if (!detailOffline.getProduct().getProductType().getCode().equals("SER") && typeDocument == 1) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(billOffline.getSubsidiary().getId(), detailOffline.getProduct().getId());
                ps.setQuantity(ps.getQuantity() - detailOffline.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("BillOfflineServices updateProductBySubsidiary");
    }
    //Fin de Creación de las BillsOffline
    
    //Desactivación o Anulación de las BillsOffline
    public BillOffline deactivateBillOffline(BillOffline billOffline) throws BadRequestException {
        if (billOffline.getId() == null) {
            throw new BadRequestException("Invalid BillOffline");
        }
        BillOffline billToDeactivate = billOfflineRepository.findOne(billOffline.getId());
        billToDeactivate.setListsNull();
        billToDeactivate.setActive(false);
        billOfflineRepository.save(billToDeactivate);
        log.info("BillOfflineServices deactivateBillOffline id: {}", billOffline.getId());
        return billToDeactivate;
    }
}