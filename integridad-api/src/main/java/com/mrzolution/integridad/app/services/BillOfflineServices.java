package com.mrzolution.integridad.app.services;

import com.google.common.collect.Lists;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.report.ItemOfflineReport;
import com.mrzolution.integridad.app.domain.report.SalesOfflineReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    KardexChildRepository kardexChildRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ComprobanteCobroServices comprobanteCobroService;
    @Autowired
    CreditsRepository creditsRepository;
    @Autowired
    DailybookCiServices dailybookCiServices;
    @Autowired
    DailybookFvServices dailybookFvServices;
    @Autowired
    ConfigCuentasServices configCuentasServices;
    @Autowired
    CuentaContableByProductRepository cuentaContableByProductRepository;
    
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
       
    //Inicio de Creación de las BillsOffline    
    @Async("asyncExecutor")
    public BillOffline createBillOffline(BillOffline billOffline, ComprobanteCobro comprobante, DailybookCi dailybookCi, int typeDocument) throws BadRequestException {
        List<DetailOffline> detailsOffline = billOffline.getDetailsOffline();
        List<PagoOffline> pagosOffline = billOffline.getPagosOffline();
        List<Kardex> detailsKardexOffline = billOffline.getDetailsKardexOffline();
        if (detailsOffline == null) {
            throw new BadRequestException("Debe tener un detalle por lo menos");
        }
        if (typeDocument == 1 && pagosOffline == null) {
            throw new BadRequestException("Debe tener un pago por lo menos");
        }
        
        billOffline.setTypeDocument(typeDocument);
        billOffline.setActive(true);
        billOffline.setDetailsOffline(null);
        billOffline.setPagosOffline(null);
        billOffline.setDetailsKardexOffline(null);
        billOffline.setFatherListToNull();
        billOffline.setListsNull();
        BillOffline saved = billOfflineRepository.save(billOffline);

        Cashier cashier = cashierRepository.findOne(billOffline.getUserIntegridad().getCashier().getId());
        cashier.setBillOfflineNumberSeq(cashier.getBillOfflineNumberSeq() + 1);
        cashierRepository.save(cashier);
        
        saveDetailsOfflineOfBillOffline(saved, detailsOffline);
        saveKardexOfBillOffline(saved, detailsKardexOffline);
        savePagosOfflineOfBillOffline(saved, pagosOffline);
        updateProductBySubsidiary(billOffline, typeDocument, detailsOffline);

        if(pagosOffline.size() ==1 && pagosOffline.get(0).getMedio() != null  && pagosOffline.get(0).getMedio().equals("efectivo")){
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
            payment.setDatePaymentCreated(String.valueOf(saved.getDateCreated()));

            Payment paymentSaved = paymentRepository.save(payment);

            comprobante.setPaymentId(paymentSaved.getId().toString());
            comprobanteCobroService.createComprobanteCobro(comprobante);

            dailybookCiServices.createDailybookAsinCi(dailybookCi);
            //************************************************************************************
        }

        saveDailyBookFv(saved, detailsOffline);
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
        log.info("BillOfflineServices saveDetailsOfflineOfBillOffline DONE");
    }
    
    //Almacena los Detalles en Kardex
    public void saveKardexOfBillOffline(BillOffline saved, List<Kardex> detailsKardexOffline) {
        detailsKardexOffline.forEach(detailkoff -> {
            detailkoff.setActive(true);
            detailkoff.setBillOffline(saved);
            kardexRepository.save(detailkoff);
            detailkoff.setBillOffline(null);
        });
        saved.setDetailsKardexOffline(detailsKardexOffline);
        log.info("BillOfflineServices saveKardexOfBillOffline DONE");
    }
    
    //Guarda el tipo de Pago y Credits
    public void savePagosOfflineOfBillOffline(BillOffline saved, List<PagoOffline> pagosOffline) {
        pagosOffline.forEach(pagoOffline -> {
            List<Credits> creditsList = pagoOffline.getCredits();
            pagoOffline.setCredits(null);
            pagoOffline.setBillOffline(saved);
            PagoOffline pagoSaved = pagoOfflineRepository.save(pagoOffline);
            if (creditsList != null) {
                creditsList.forEach(credit -> {
                    credit.setPagoOffline(pagoSaved);
                    credit.setBillId(saved.getId().toString());
                    creditsRepository.save(credit);
                });
            }
        });
    }
    
    //Actualiza la cantidad de Productos (Existencia)
    public void updateProductBySubsidiary(BillOffline billOffline, int typeDocument, List<DetailOffline> detailsOffline) {
        detailsOffline.forEach(detailOffline-> {
            if (!detailOffline.getProduct().getProductType().getCode().equals("SER") && typeDocument == 1) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(billOffline.getSubsidiary().getId(), detailOffline.getProduct().getId());
                if (ps == null) {
                    throw new BadRequestException("ERROR: Producto NO encontrado");
                } else {
                    ps.setQuantity(ps.getQuantity() - detailOffline.getQuantity());
                    productBySubsidiairyRepository.save(ps);
                }
            }
        });
        log.info("BillOfflineServices updateProductBySubsidiary");
    }
    //Fin de Creación de las BillsOffline
    
    //Desactivación o Anulación de las BillsOffline
    @Async("asyncExecutor")
    public BillOffline deactivateBillOffline(BillOffline billOffline) throws BadRequestException {
        if (billOffline.getId() == null) {
            throw new BadRequestException("Invalid BillOffline");
        }
        BillOffline billToDeactivate = billOfflineRepository.findOne(billOffline.getId());
        billToDeactivate.setListsNull();
        billToDeactivate.setActive(false);
        BillOffline savedOff = billOfflineRepository.save(billToDeactivate);
        populateChildren(savedOff);
        updatePSdeactivatedBillOffline(savedOff, savedOff.getDetailsOffline());
        log.info("BillOfflineServices deactivateBillOffline: {}, {}", savedOff.getId(), savedOff.getStringSeq());
        return billToDeactivate;
    }
    
    public void updatePSdeactivatedBillOffline(BillOffline deactivated, List<DetailOffline> details) {
        details.forEach(detail -> {
            if (!detail.getProduct().getProductType().getCode().equals("SER")) {
                ProductBySubsidiary ps = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(deactivated.getSubsidiary().getId(), detail.getProduct().getId());
                ps.setQuantity(ps.getQuantity() + detail.getQuantity());
                productBySubsidiairyRepository.save(ps);
            }
        });
        log.info("BillOfflineServices updatePSdeactivatedBillOffline DONE");
    }
    
    //Reporte de Ventas Offline
    public List<SalesOfflineReport> getAllBySubIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("BillOfflineServices getAllBySubIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<BillOffline> billsOffline = billOfflineRepository.findAllByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<SalesOfflineReport> salesOfflineReportList = new ArrayList<>();
        billsOffline.forEach(billOff-> {
            billOff.setListsNull();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(billOff.getDateCreated()));
            String status = billOff.isActive() ? "ACTIVA" : "ANULADA";

            SalesOfflineReport saleOfflineReport= new SalesOfflineReport(date, billOff.getClient().getCodApp(), billOff.getClient().getName(), billOff.getClient().getIdentification(),
			billOff.getStringSeq(), status, billOff.getBaseTaxes(), billOff.getDiscount(), billOff.getBaseNoTaxes(), billOff.getIva(), billOff.getTotal(), billOff.getUserIntegridad().getCashier().getNameNumber(),
			billOff.getSubsidiary().getName(), billOff.getUserIntegridad().getFirstName() + " " + billOff.getUserIntegridad().getLastName());

            salesOfflineReportList.add(saleOfflineReport);
        });
        return salesOfflineReportList;
    }
    
    //Reporte de Productos Vendidos Offline
    public List<ItemOfflineReport> getBySubIdAndDatesActives(UUID userClientId, long dateOne, long dateTwo) {
        log.info("BillOfflineServices getByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<BillOffline> billsOffline = billOfflineRepository.findByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
        Set<UUID> productIds = new HashSet<>();
        billsOffline.forEach(billOff -> {
            populateChildren(billOff);
            for (DetailOffline detail: billOff.getDetailsOffline()) {
                productIds.add(detail.getProduct().getId());
            }
        });	
        return loadListItemsOffline(Lists.newArrayList(billsOffline), productIds);
    }
    
    private List<ItemOfflineReport> loadListItemsOffline(List<BillOffline> billsOffline, Set<UUID> productIds) {
        List<ItemOfflineReport> reportList = new ArrayList<>();
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
            for (BillOffline billOff: billsOffline) {
                for (DetailOffline detail: billOff.getDetailsOffline()) {
                    if (uuidCurrent.equals(detail.getProduct().getId())) {
                        if (detail.getProduct().isIva()) {
                            iva = 0.12;
                            total = 1.12;
                        } else {
                            iva = 0.0;
                            total = 1.0;
                        }
                        Double discount = Double.valueOf(Double.valueOf(billOff.getDiscountPercentage())/100) * detail.getTotal();
                        ItemOfflineReport item = new ItemOfflineReport(detail.getProduct().getId(), billOff.getStringSeq(), detail.getProduct().getCodeIntegridad(),
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
            ItemOfflineReport itemOfflineTotal = new ItemOfflineReport(uuidCurrent, "SUB-TOTAL", code,
			desc, quantityTotal, null, subTotalTotal, discountTotal, ivaTotal, totalTotal);

            reportList.add(itemOfflineTotal);
        }
        return reportList;
    }
    
    private void populateChildren(BillOffline billOffline) {
        List<DetailOffline> detailOfflineList = getDetailsOfflineByBill(billOffline);
        List<PagoOffline> pagoOfflineList = getPagosOfflineByBill(billOffline);
        List<Kardex> detailsKardexOfflineList = getDetailsKardexOfflineByBill(billOffline);
        billOffline.setDetailsOffline(detailOfflineList);
        billOffline.setPagosOffline(pagoOfflineList);
        billOffline.setDetailsKardexOffline(detailsKardexOfflineList);
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
    
    private List<Kardex> getDetailsKardexOfflineByBill(BillOffline billOffline) {
        List<Kardex> detailsKardexOfflineList = new ArrayList<>();
        Iterable<Kardex> detailsKardexOffline = kardexRepository.findByBillOffline(billOffline);
        detailsKardexOffline.forEach(detailkardex -> {
            detailkardex.getBillOffline().setListsNull();
            detailkardex.getBillOffline().setFatherListToNull();
            detailkardex.getProduct().setFatherListToNull();
            detailkardex.getProduct().setListsNull();
            detailkardex.setBillOffline(null);
            detailsKardexOfflineList.add(detailkardex);
        });
        return detailsKardexOfflineList;
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

    private void saveDailyBookFv(BillOffline saved, List<DetailOffline> details){
        DailybookFv dailybookFv = new DailybookFv();

        dailybookFv.setActive(true);
        dailybookFv.setDateRecordBook(saved.getDateCreated());
        dailybookFv.setBillNumber(saved.getStringSeq());
        dailybookFv.setClientProvName(saved.getClient().getName());
        //TODO change this two types (fields) -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        dailybookFv.setCodeTypeContab("6");
        dailybookFv.setTypeContab("COMP. DE FACT-VENTA");
        dailybookFv.setGeneralDetail("FACTURA N. " + saved.getStringSeq());

        Long newSeq = saved.getUserIntegridad().getCashier().getDailyFvNumberSeq() + 1;
        String sequence = String.valueOf(newSeq);

        dailybookFv.setDailyFvSeq(sequence);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6 - sequence.length(); i++) {
            sb.append('0');
        }
        dailybookFv.setDailyFvStringSeq(sb.toString() + sequence);
        dailybookFv.setDailyFvStringUserSeq("ASIENTO DE VENTA GENERADO " + sb.toString() + sequence);
        dailybookFv.setSubTotalDoce(saved.getBaseTaxes());
        dailybookFv.setSubTotalCero(saved.getBaseNoTaxes());
        dailybookFv.setIva(saved.getIva());
        dailybookFv.setTotal(saved.getTotal());
        dailybookFv.setClient(saved.getClient());
        dailybookFv.setUserIntegridad(saved.getUserIntegridad());
        dailybookFv.setSubsidiary(saved.getSubsidiary());

        List<DetailDailybookContab> dailyDetails = new ArrayList<>();
        dailyDetails.add(createDetialDailySale(saved, sb.toString() + sequence, saved.getTotal(), null, saved.getClient().getCodConta(), "Clientes Locales"));

        ConfigCuentas configCuentas = configCuentasServices.getCuentasByUserCliendIdAndOptionCode(saved.getSubsidiary().getUserClient().getId(), "IVAVENTAS");
        dailyDetails.add(createDetialDailySale(saved, sb.toString() + sequence, null,
                saved.getIva(), configCuentas == null ? "-" : configCuentas.getCode(), configCuentas == null ? "-" :configCuentas.getDescription()));

        Map<CuentaContable, Double> accounts = new HashMap<>();
        for(DetailOffline det : details){
            Iterable<CuentaContableByProduct> cuentas =  cuentaContableByProductRepository.findByProductId(det.getProduct().getId());
            List<CuentaContableByProduct> cuentasByProduct = Lists.newArrayList(cuentas);
            for(int i = 0; i < cuentasByProduct.size(); i++){
                if("VENTA".equals(cuentasByProduct.get(i).getType())){
                    Double value = accounts.get(cuentasByProduct.get(i).getCuentaContable());
                    if(accounts.isEmpty() || value == null){
                        accounts.put(cuentasByProduct.get(i).getCuentaContable(), det.getTotal());
                    } else {
                        accounts.replace(cuentasByProduct.get(i).getCuentaContable(), value + det.getTotal());
                    }
                    i = cuentasByProduct.size();
                }
            }
        }

        for(Map.Entry<CuentaContable, Double> entry : accounts.entrySet()){
            CuentaContable cuentaContable = entry.getKey();
            dailyDetails.add(createDetialDailySale(saved, sb.toString() + sequence, null, entry.getValue(), cuentaContable.getCode(), cuentaContable.getName()));
        };

        dailybookFv.setDetailDailybookContab(dailyDetails);
        DailybookFv  dailybookFvSaved = dailybookFvServices.createDailybookFv(dailybookFv);

        Cashier cahsierToUpdate = cashierRepository.findOne(saved.getUserIntegridad().getCashier().getId());
        cahsierToUpdate.setDailyFvNumberSeq(newSeq);
        cashierRepository.save(cahsierToUpdate);

        log.info("BillServices createBill dailyFV created:{}", dailybookFvSaved.getId());
    }

    private DetailDailybookContab createDetialDailySale(BillOffline saved, String sequence, Double deber, Double haber, String codeConta, String descConta){
        DetailDailybookContab detailDailybookContab = new DetailDailybookContab();
        detailDailybookContab.setTypeContab("COMP. DE VENTAS");
        detailDailybookContab.setName("FACTURA N. " + saved.getStringSeq());
        detailDailybookContab.setDateDetailDailybook(saved.getDateCreated());
        detailDailybookContab.setDailybookNumber(sequence);
        detailDailybookContab.setUserClientId(saved.getUserIntegridad().getSubsidiary().getUserClient().getId().toString());
        detailDailybookContab.setActive(true);

        detailDailybookContab.setCodeConta(codeConta);
        detailDailybookContab.setDescrip(descConta);
        detailDailybookContab.setTipo(deber == null ? "CREDITO (C)" : "DEBITO (D)");
        detailDailybookContab.setBaseImponible(deber == null ? haber : deber);
        detailDailybookContab.setDeber(String.valueOf(deber));
        detailDailybookContab.setHaber(String.valueOf(haber));

        return detailDailybookContab;
    }

}