package com.mrzolution.integridad.app.services;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.Pago;
import com.mrzolution.integridad.app.domain.ebill.*;
import com.mrzolution.integridad.app.domain.report.ItemReport;
import com.mrzolution.integridad.app.domain.report.SalesReport;
import com.mrzolution.integridad.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@Component
public class BillServices {
	
    @Autowired
    BillRepository billRepository;
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
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    KardexChildRepository kardexChildRepository;
            
    public String getDatil(Requirement requirement, UUID userClientId) throws Exception {
        UserClient userClient = userClientRepository.findOne(userClientId);
        if (userClient == null) {
            throw new BadRequestException("Empresa Invalida");
        }
        log.info("BillServices getDatil Empresa valida: {}", userClient.getName());
        if (requirement.getPagos() != null) {
            requirement.getPagos().forEach(pago -> {
                if ("credito".equals(pago.getMedio())) {
                    pago.setMedio("otros");
                }
            });
        }
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(requirement);
        log.info("BillServices getDatil MAPPER creado");
        
        String response = httpCallerService.post(Constants.DATIL_LINK, data, userClient);
        //String response = "OK";
        log.info("BillServices getDatil httpcall SUCCESS");
        return response;
    }

    public Iterable<Bill> getByTypeDocument(int value) {
        log.info("BillServices getByTypeDocument: {}", value);
        Iterable<Bill> bills = billRepository.findBillsByTypeDocument(value);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        return bills;
    }
	
    public Iterable<Bill> getByUserLazy(UserIntegridad user) {
        log.info("BillServices getByUserLazy: {}", user.getId());
        Iterable<Bill> bills = billRepository.findByUserIntegridad(user);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        return bills;
    }

    //Selecciona todas las Facturas del Cliente
    public Iterable<Bill> getBillByClientId(UUID id, int type) {
        log.info("BillServices getBillByClientId: {}", id);
        Iterable<Bill> bills = billRepository.findBillByClientId(id, type);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        return bills;
    }
    
    //Selecciona todas las Facturas del Cliente con Saldo != '0.00'
    public Iterable<Bill> getBillByClientIdWithSaldo(UUID id, int type) {
        log.info("BillServices getBillByClientIdWithSaldo: {}", id);
        Iterable<Bill> bills = billRepository.findBillByClientIdWithSaldo(id, type);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        return bills;
    }

    //Bucar Bills por ID        
    public Bill getBillById(UUID id) {
        log.info("BillServices getBillById: {}", id);
        Bill retrieved = billRepository.findOne(id);
        if (retrieved != null) {
            log.info("BillServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("BillServices retrieved id NULL: {}", id);
        }		
        populateChildren(retrieved);
        return retrieved;
    }

//Inicio de Creación de las Bills    
    @Async("asyncExecutor")
    public Bill createBill(Bill bill, int typeDocument) throws BadRequestException {
        List<Detail> details = bill.getDetails();
        List<Pago> pagos = bill.getPagos();
        List<Kardex> detailsKardex = bill.getDetailsKardex();
        if (details == null) {
            throw new BadRequestException("Debe tener un detalle por lo menos");
        }
        if (typeDocument == 1 && pagos == null) {
            throw new BadRequestException("Debe tener un pago por lo menos");
        }
        bill.setDateCreated(new Date().getTime());
        bill.setTypeDocument(typeDocument);
        bill.setActive(true);
        bill.setDetails(null);
        bill.setDetailsKardex(null);
        bill.setPagos(null);
        bill.setFatherListToNull();
        bill.setListsNull();
        Bill saved = billRepository.save(bill);

        // typeDocument 1 is Bill 0 is Quotation
        if (typeDocument == 1) {
            Cashier cashier = cashierRepository.findOne(bill.getUserIntegridad().getCashier().getId());
            cashier.setBillNumberSeq(cashier.getBillNumberSeq() + 1);
            cashierRepository.save(cashier);
            // (2) Excepción Ferretería Lozada No actualiza Kardex
            if ("A-2".equals(bill.getClient().getUserClient().getEspTemp())) {
                saveDetailsBill(saved, details);
                savePagosAndCreditsBill(saved, pagos);
                updateProductBySubsidiary(bill, typeDocument, details);
            } else {
                saveDetailsBill(saved, details);
                saveKardex(saved, detailsKardex);
                savePagosAndCreditsBill(saved, pagos);
                updateProductBySubsidiary(bill, typeDocument, details);
            }
        } else {
            Cashier cashier = cashierRepository.findOne(bill.getUserIntegridad().getCashier().getId());
            cashier.setQuotationNumberSeq(cashier.getQuotationNumberSeq() + 1);
            cashierRepository.save(cashier);
            saveDetailsQuotation(saved, details);
        }
        log.info("BillServices createBill DONE id: {}", saved.getId());
        return saved;
    }
    
    //Almacena los Detalles de la Factura
    public void saveDetailsBill(Bill saved, List<Detail> details) {
        details.forEach(detail-> {
            detail.setBill(saved);
            detailRepository.save(detail);
            detail.setBill(null);
        });
        saved.setDetails(details);
        log.info("BillServices saveDetailsBill DONE");
    }
    
    //Almacena los Detalles en Kardex
    public void saveKardex(Bill saved, List<Kardex> detailsKardex) {
        detailsKardex.forEach(detailk -> {
            detailk.setBill(saved);
            kardexRepository.save(detailk);
            detailk.setBill(null);
        });
        saved.setDetailsKardex(detailsKardex);
        log.info("BillServices saveKardex DONE");
    }
    
    //Almacena los Detalles de la Cotización
    public void saveDetailsQuotation(Bill saved, List<Detail> details) {
        details.forEach(detail-> {
            detail.setBill(saved);
            detailRepository.save(detail);
            detail.setBill(null);
        });
        saved.setDetails(details);
        log.info("BillServices saveDetailsQuotation DONE");
    }
    
    //Guarda el tipo de Pago y Credits
    public void savePagosAndCreditsBill(Bill saved, List<Pago> pagos) {
        pagos.forEach(pago -> {
            List<Credits> creditsList = pago.getCredits();
            pago.setCredits(null);
            pago.setBill(saved);
            Pago pagoSaved = pagoRepository.save(pago);		
            if (creditsList != null) {
                creditsList.forEach(credit -> {
                    credit.setPago(pagoSaved);
                    credit.setBillId(saved.getId().toString());
                    creditsRepository.save(credit);
                });
            }    
        });
        log.info("BillServices savePagosAndCreditsBill DONE");
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(Bill bill, int typeDocument, List<Detail> details) {
        details.forEach(detail-> {
            if (!detail.getProduct().getProductType().getCode().equals("SER") && typeDocument == 1) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(bill.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() - detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("BillServices updateProductBySubsidiary DONE");
    }
//Fin de Creación de las Bills

    //Desactivación o Anulación de las Bills
    public Bill deactivateBill(Bill bill) throws BadRequestException {
        if (bill.getId() == null) {
            throw new BadRequestException("Invalid Bill");
        }

        Bill billToDeactivate = billRepository.findOne(bill.getId());
        billToDeactivate.setListsNull();
        billToDeactivate.setActive(false);
        billRepository.save(billToDeactivate);
        log.info("BillServices deactivateBill DONE id: {}", bill.getId());
        return billToDeactivate;
    }

    //Actualización de las Bills
    public Bill updateBill(Bill bill) throws BadRequestException {
        if (bill.getId() == null) {
            throw new BadRequestException("Invalid Bill");
        }
        log.info("BillServices updateBill: {}", bill.getId());
        Father<Bill, Detail> father = new Father<>(bill, bill.getDetails());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailChildRepository, detailRepository);
        fatherUpdateChildren.updateChildren();
        log.info("BillServices CHILDREN updated: {}", bill.getId());
        bill.setListsNull();
        Bill updated = billRepository.save(bill);
        log.info("BillServices updateBill DONE id: {}", updated.getId());
        return updated;
    }

    public Iterable<Bill> getByStringSeqAndSubId(String stringSeq, UUID subId) {
        log.info("BillServices getByStringSeq : {}, {}", stringSeq, subId);
        Iterable<Bill> bills = billRepository.findByStringSeqAndSubsidiaryId(stringSeq, subId);
        bills.forEach(bill -> {
            bill.setFatherListToNull();
            bill.setListsNull();
        });
        return bills;
    }

    //Reporte de Productos
    public List<ItemReport> getBySubIdAndDatesActives(UUID userClientId, long dateOne, long dateTwo) {
        log.info("BillServices getByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Bill> bills = billRepository.findByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
        Set<UUID> productIds = new HashSet<>();
        bills.forEach(bill-> {
            populateChildren(bill);
            for (Detail detail: bill.getDetails()) {
                productIds.add(detail.getProduct().getId());
            }
        });	
        return loadListItems(Lists.newArrayList(bills), productIds);
    }
    
    private List<ItemReport> loadListItems(List<Bill> bills, Set<UUID> productIds) {
        List<ItemReport> reportList = new ArrayList<>();
        for (UUID uuidCurrent: productIds) {
            Double quantityTotal = new Double(0);
            Double subTotalTotal = new Double(0);
            Double discountTotal = new Double(0);
            Double ivaTotal = new Double(0);
            Double totalTotal = new Double(0);
            String code = "";
            String desc = "";
            for (Bill bill: bills) {
                for (Detail detail: bill.getDetails()) {
                    if (uuidCurrent.equals(detail.getProduct().getId())) {
                        Double discount = Double.valueOf(Double.valueOf(bill.getDiscountPercentage())/100) * detail.getTotal();
                        ItemReport item = new ItemReport(detail.getProduct().getId(),"", bill.getStringSeq(), detail.getProduct().getCodeIntegridad(),
					detail.getProduct().getName(),Double.valueOf(detail.getQuantity()), detail.getCostEach(), detail.getTotal(), discount, ((detail.getTotal()-discount) * 0.12), ((detail.getTotal()-discount) * 1.12));
                        quantityTotal += item.getQuantity();
                        subTotalTotal += item.getSubTotal();
                        discountTotal += item.getDiscount();
                        ivaTotal += item.getIva();
                        totalTotal += item.getTotal();
                        code = detail.getProduct().getCodeIntegridad();
                        desc = detail.getProduct().getName();
                        reportList.add(item);
                    }
                }
            }
            ItemReport itemTotal = new ItemReport(uuidCurrent, "R", "", code,
			desc, quantityTotal, null, subTotalTotal, discountTotal, ivaTotal, totalTotal);

            reportList.add(itemTotal);
        }
        return reportList;
    }

    //Reporte de Ventas
    public List<SalesReport> getAllBySubIdAndDates(UUID userClientId, long dateOne, long dateTwo){
        log.info("BillServices getAllBySubIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Bill> bills = billRepository.findAllByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<SalesReport> salesReportList = new ArrayList<>();
        bills.forEach(bill-> {
            bill.setListsNull();
            Long endDateLong = bill.getDateCreated();
            List<Pago> pagos = getPagosByBill(bill);
            for (Pago pago: pagos) {
                if (pago.getCredits() != null) {
                    for (Credits credit: pago.getCredits()) {
                        if (endDateLong < credit.getFecha()) {
                            endDateLong = credit.getFecha();
                        }
                    }
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(bill.getDateCreated()));
            String status = bill.isActive() ? "ACTIVA" : "ANULADA";
            String endDate = dateFormat.format(new Date(endDateLong));

            SalesReport saleReport= new SalesReport(date, bill.getClient().getCodApp(), bill.getClient().getName(), bill.getClient().getIdentification(),
			bill.getStringSeq(), status, bill.getOtir(), bill.getBaseTaxes(), bill.getDiscount(),bill.getBaseNoTaxes(), bill.getIva(), bill.getTotal(), endDate, bill.getUserIntegridad().getCashier().getNameNumber(),
			null, bill.getSubsidiary().getName(), bill.getUserIntegridad().getFirstName() + " " + bill.getUserIntegridad().getLastName());

            salesReportList.add(saleReport);
        });
        return salesReportList;
    }

    private void populateChildren(Bill bill) {
        List<Detail> detailList = getDetailsByBill(bill);
        List<Pago> pagoList = getPagosByBill(bill);
        List<Kardex> detailsKardexList = getDetailsKardexByBill(bill);
        bill.setDetails(detailList);
        bill.setDetailsKardex(detailsKardexList);
        bill.setPagos(pagoList);
        bill.setFatherListToNull();
    }

    private List<Detail> getDetailsByBill(Bill bill) {
        List<Detail> detailList = new ArrayList<>();
        Iterable<Detail> details = detailRepository.findByBill(bill);
        details.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setBill(null);
            detailList.add(detail);
        });
        return detailList;
    }
    
    private List<Kardex> getDetailsKardexByBill(Bill bill) {
        List<Kardex> detailsKardexList = new ArrayList<>();
        Iterable<Kardex> detailsKardex = kardexRepository.findByBill(bill);
        detailsKardex.forEach (detail -> {
            detail.getBill().setListsNull();
            detail.getBill().setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.setBill(null);
            detail.setCellar(null);
            detailsKardexList.add(detail);
        });
        return detailsKardexList;
    }
    
    private List<Pago> getPagosByBill(Bill bill) {
        List<Pago> pagoList = new ArrayList<>();
        Iterable<Pago> pagos = pagoRepository.findByBill(bill);
        pagos.forEach(pago -> {
            if ("credito".equals(pago.getMedio())) {
                Iterable<Credits> credits = creditsRepository.findByPago(pago);
                List<Credits> creditsList = new ArrayList<>();
                credits.forEach(credit -> {
                    credit.setListsNull();
                    credit.setFatherListToNull();
                    credit.setPago(null);
                    creditsList.add(credit);
                });
                pago.setCredits(creditsList);
            } else {
                pago.setListsNull();
            }
            pago.setFatherListToNull();
            pago.setBill(null);
            pagoList.add(pago);
        });
        return pagoList;
    }

}