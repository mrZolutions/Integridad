package com.mrzolution.integridad.app.services;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.Pago;
import com.mrzolution.integridad.app.domain.ebill.*;
import com.mrzolution.integridad.app.domain.report.CashClosureReport;
import com.mrzolution.integridad.app.domain.report.ItemReport;
import com.mrzolution.integridad.app.domain.report.SalesReport;
import com.mrzolution.integridad.app.repositories.*;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
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
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ComprobanteCobroServices comprobanteCobroService;

    public String getDatil(Requirement requirement, UUID userClientId) throws Exception {
        UserClient userClient = userClientRepository.findOne(userClientId);
        if (userClient.getApiKey() == null || "".equals(userClient.getApiKey())) {
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

//        String response = httpCallerService.post(Constants.DATIL_LINK, data, userClient);
        String response ="{\n" +
                "  \"id\": \"abcdef09876123cea56784f01\",\n" +
                "  \"ambiente\":1,\n" +
                "  \"tipo_emision\":1,\n" +
                "  \"secuencial\":148,\n" +
                "  \"fecha_emision\":\"2019-09-28T11:28:56.782Z\",\n" +
                "  \"clave_acceso\": \"2802201501091000000000120010010000100451993736618\"}";

        log.info("BillServices getDatil httpcall DONE");
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
        Iterable<Bill> bills = billRepository.findByUserIntegridad(user);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        log.info("BillServices getByUserLazy: {}", user.getId());
        return bills;
    }

    //Selecciona todas las Facturas del Cliente
    public Iterable<Bill> getBillByClientId(UUID id, int type) {
        Iterable<Bill> bills = billRepository.findBillByClientId(id, type);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        log.info("BillServices getBillByClientId: {}", id);
        return bills;
    }
    
    //Selecciona todas las Facturas sin Nota de Cŕedito Aplicada del Cliente
    public Iterable<Bill> getBillByClientIdAndNoCN(UUID id, int type) {
        Iterable<Bill> bills = billRepository.findBillByClientIdAndNoCN(id, type);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        log.info("BillServices getBillByClientIdAndNoCN: {}", id);
        return bills;
    }
    
    //Selecciona todas las Facturas del Cliente con Saldo != '0.00'
    public Iterable<Bill> getBillByClientIdWithSaldo(UUID id, int type) {
        Iterable<Bill> bills = billRepository.findBillByClientIdWithSaldo(id, type);
        bills.forEach(bill -> {
            bill.setListsNull();
            bill.setFatherListToNull();
        });
        log.info("BillServices getBillByClientIdWithSaldo: {}", id);
        return bills;
    }

    //Bucar Bills por ID        
    public Bill getBillById(UUID id) {
        Bill retrieved = billRepository.findOne(id);		
        populateChildren(retrieved);
        log.info("BillServices getBillById: {}", id);
        return retrieved;
    }

    //Inicio de Creación de las Bills
    @Async("asyncExecutor")
    public Bill createBill(Bill bill, ComprobanteCobro comprobante, int typeDocument) throws BadRequestException {
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

        saveChildrenOnCreation(bill, saved, typeDocument, details, pagos, detailsKardex);

        if(pagos.size() ==1 && pagos.get(0).getMedio() != null && pagos.get(0).getMedio().equals("efectivo")){
            //************************************************************************************
            Credits credit = creditsRepository.findByBillId(saved.getId().toString());

            Payment payment = new Payment();
            payment.setTypePayment("PAC");
            payment.setModePayment("EFC");
            payment.setDetail("PAGO TOTAL EFECTIVO");
            payment.setDatePayment(new Date().getTime());
            payment.setNoDocument(saved.getStringSeq());
            payment.setDocumentNumber(saved.getStringSeq());
            payment.setValorAbono(saved.getTotal());
            payment.setValorNotac(Double.valueOf(0));
            payment.setValorReten(Double.valueOf(0));
            payment.setActive(true);
            payment.setCredits(credit);

            Payment paymentSaved = paymentRepository.save(payment);

            comprobante.setPaymentId(paymentSaved.getId().toString());
            comprobanteCobroService.createComprobanteCobro(comprobante);

            //************************************************************************************
        }

        log.info("BillServices createBill: {}, {}", saved.getId(), saved.getStringSeq());
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
            detailk.setActive(true);
            detailk.setBill(saved);
            kardexRepository.save(detailk);
            detailk.setBill(null);
        });
        saved.setDetailsKardex(detailsKardex);
        log.info("BillServices saveKardex DONE");
    }
    
    //Almacena los Detalles de la Cotización
    public void saveDetailsQuotation(Bill saved, List<Detail> details) {
        details.forEach(detail -> {
            detail.setBill(saved);
            detailRepository.save(detail);
            detail.setBill(null);
        });
        saved.setDetails(details);
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
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(Bill bill, int typeDocument, List<Detail> details) {
        details.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER") && typeDocument == 1) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(bill.getSubsidiary().getId(), detail.getProduct().getId());
                if (ps == null) {
                    throw new BadRequestException("ERROR: Producto NO encontrado");
                } else {
                    ps.setQuantity(ps.getQuantity() - detail.getQuantity());
                    productBySubsidiairyRepository.save(ps);
                }
            }
        });
        log.info("BillServices updateProductBySubsidiary DONE");
    }
    //Fin de Creación de las Bills
    
    //Actualización de las Bills
    public Bill updateBill(Bill bill) throws BadRequestException {
        if (bill.getId() == null) {
            throw new BadRequestException("Invalid Bill");
        }
        Father<Bill, Detail> father = new Father<>(bill, bill.getDetails());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailChildRepository, detailRepository);
        fatherUpdateChildren.updateChildren();
        bill.setListsNull();
        Bill updated = billRepository.save(bill);
        log.info("BillServices updateBill id: {}", updated.getId());
        return updated;
    }

    //Desactivación o Anulación de las Bills
    @Async("asyncExecutor")
    public Bill deactivateBill(Bill bill) throws BadRequestException {
        if (bill.getId() == null) {
            throw new BadRequestException("Invalid Bill");
        }
        Bill billToDeactivate = billRepository.findOne(bill.getId());
        billToDeactivate.setActive(false);
        billToDeactivate.setListsNull();
        Bill saved = billRepository.save(billToDeactivate);
        populateChildren(saved);
        updateKardexOfDeactivatedBill(saved);
        updatePSOfDeactivatedBill(saved, saved.getDetails());
        log.info("BillServices deactivateBill: {}, {}", billToDeactivate.getId(), billToDeactivate.getStringSeq());
        return billToDeactivate;
    }
    
    public void updatePSOfDeactivatedBill(Bill deactivated, List<Detail> details) {
        details.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(deactivated.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() + detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("BillServices updatePSOfDeactivatedBill DONE");
    }
    
    public void updateKardexOfDeactivatedBill(Bill deactivated) {
        Iterable<Kardex> kardexs = kardexRepository.findByBillId(deactivated.getId());
        kardexs.forEach(kardex -> {
            kardex.setDetails(kardex.getDetails() + " -- ANULADA");
            kardex.setActive(false);
            kardex.setListsNull();
            kardex.setFatherListToNull();
            kardexRepository.save(kardex);
        });
    }

    //Busca las Bills por Numero de Sequencia y Subsidiaria
    public Iterable<Bill> getByStringSeqAndSubId(String stringSeq, UUID subId) {
        log.info("BillServices getByStringSeqAndSubId : {}, {}", stringSeq, subId);
        Iterable<Bill> bills = billRepository.findByStringSeqAndSubsidiaryId(stringSeq, subId);
        bills.forEach(bill -> {
            bill.setFatherListToNull();
            bill.setListsNull();
        });
        return bills;
    }

    //Reporte de Productos Vendidos
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
            Double iva = new Double(0);
            Double total = new Double(0);
            String code = "";
            String desc = "";
            for (Bill bill: bills) {
                for (Detail detail: bill.getDetails()) {
                    if (uuidCurrent.equals(detail.getProduct().getId())) {
                        if (detail.getProduct().isIva()) {
                            iva = 0.12;
                            total = 1.12;
                        } else {
                            iva = 0.0;
                            total = 1.0;
                        }
                        Double discount = Double.valueOf(Double.valueOf(bill.getDiscountPercentage())/100) * detail.getTotal();
                        ItemReport item = new ItemReport(detail.getProduct().getId(),"", bill.getStringSeq(), detail.getProduct().getCodeIntegridad(),
					detail.getProduct().getName(),Double.valueOf(detail.getQuantity()), detail.getCostEach(), detail.getTotal(), discount, ((detail.getTotal()- discount) * iva), ((detail.getTotal()- discount) * total));
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
            ItemReport itemTotal = new ItemReport(uuidCurrent, "SUB-TOTAL", "", code,
			desc, quantityTotal, null, subTotalTotal, discountTotal, ivaTotal, totalTotal);

            reportList.add(itemTotal);
        }
        return reportList;
    }

    //Reporte de Ventas
    public List<SalesReport> getAllBySubIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("BillServices getAllBySubIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Bill> bills = billRepository.findAllByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<SalesReport> salesReportList = new ArrayList<>();
        bills.forEach(bill-> {
            bill.setListsNull();
            Long endDateLong = bill.getDateCreated();
            List<Pago> pagos = getPagosByBill(bill);
            String paymentMode = "";
            for (Pago pago: pagos) {
                if(pago.getMedio() != null){
                    paymentMode = pago.getMedio().toUpperCase();
                }

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
			bill.getStringSeq(), bill.getClaveDeAcceso(), status, bill.getOtir(), bill.getBaseTaxes(), bill.getDiscount(),bill.getBaseNoTaxes(), bill.getIva(), bill.getTotal(), endDate, bill.getUserIntegridad().getCashier().getNameNumber(),
			null, bill.getSubsidiary().getName(), bill.getUserIntegridad().getFirstName() + " " + bill.getUserIntegridad().getLastName(), paymentMode);

            salesReportList.add(saleReport);
        });
        return salesReportList;
    }
    
    //Reporte de Cierre de Caja
    public List<CashClosureReport> getForCashClosureReport(UUID userClientId, long dateOne, long dateTwo) {
        log.info("BillServices getForCashClosureReport: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Pago> pagos = pagoRepository.findForCashClosureReport(userClientId, dateOne, dateTwo);
        List<CashClosureReport> cashClosureReportList = new ArrayList<>();
        pagos.forEach(pago -> {
            pago.setListsNull();
            Long endDateLong = pago.getBill().getDateCreated();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String endDate = dateFormat.format(new Date(endDateLong));
            
            CashClosureReport cashClosureReport = new CashClosureReport(endDate, pago.getBill().getStringSeq(), pago.getBill().getBaseTaxes(), pago.getBill().getBaseNoTaxes(), pago.getBill().getSubTotal(),
                              pago.getBill().getIva(), pago.getBill().getTotal(), pago.getMedio(), pago.getCardBrand(), pago.getNumeroLote(), pago.getChequeAccount(), pago.getChequeBank(), pago.getChequeNumber());
            
            cashClosureReportList.add(cashClosureReport);
        });
        return cashClosureReportList;
    }

    @Async("asyncExecutor")
    private void saveChildrenOnCreation(Bill bill, Bill saved, int typeDocument, List<Detail> details, List<Pago> pagos, List<Kardex> detailsKardex){
        // typeDocument 1 is Bill 0 is Quotation
        if (typeDocument == 1) {
            Cashier cashier = cashierRepository.findOne(bill.getUserIntegridad().getCashier().getId());
            cashier.setBillNumberSeq(cashier.getBillNumberSeq() + 1);
            cashierRepository.save(cashier);
            // Excepción PPE, Dental, Lozada NO actualizan Kardex
            if ("A-1".equals(bill.getClient().getUserClient().getEspTemp()) || "A-2".equals(bill.getClient().getUserClient().getEspTemp()) || "A-N".equals(bill.getClient().getUserClient().getEspTemp())) {
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
    }
       
    private void populateChildren(Bill bill) {
        List<Detail> detailList = getDetailsByBill(bill);
        List<Pago> pagoList = getPagosByBill(bill);
        List<Kardex> detailsKardexList = getDetailsKardexByBill(bill);
        bill.setDetails(detailList);
        bill.setPagos(pagoList);
        bill.setDetailsKardex(detailsKardexList);
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