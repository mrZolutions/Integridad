package com.mrzolution.integridad.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.report.RetentionReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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

    public String getDatil(com.mrzolution.integridad.app.domain.eretention.Retention requirement, UUID userClientId) throws Exception {
        UserClient userClient = userClientRepository.findOne(userClientId);
        if (userClient == null) {
            throw new BadRequestException("Empresa Invalida");
        }
        log.info("RetentionServices getDatil Empresa valida: {}", userClient.getName());
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(requirement);
        log.info("RetentionServices getDatil MAPPER creado");
	
        String response = httpCallerService.post(Constants.DATIL_RETENTION_LINK, data, userClient);
        //String response = "OK";
        log.info("RetentionServices getDatil httpcall DONE");
        return response;
    }

    public Iterable<Retention> getByUserLazy(UserIntegridad user) {
        Iterable<Retention> retentions = retentionRepository.findRetentionByUserIntegridad(user);
        retentions.forEach(retention -> {
            retention.setListsNull();
            retention.setFatherListToNull();
        });
        log.info("RetentionServices getByUserLazy: {}", user.getId());
        return retentions;
    }

    public Retention getRetentionById(UUID id) {
        Retention retrieved = retentionRepository.findOne(id);
        populateChildren(retrieved);
        log.info("RetentionServices getRetentionById: {}", id);
        return retrieved;
    }
    
    //Selecciona todas las Retenciones del Proveedor
    public Iterable<Retention> getRetentionByProviderId(UUID id) {
        Iterable<Retention> retentions = retentionRepository.findRetentionByProviderId(id);
        retentions.forEach(retention -> {
            retention.setListsNull();
            retention.setFatherListToNull();
        });
        log.info("RetentionServices getRetentionByProviderId: {}", id);
        return retentions;
    }
    
    //Selecciona todas las Retenciones del Proveedor y Factura de Compra
    public Iterable<Retention> getRetentionByProviderIdAndDocumentNumber(UUID id, String documentNumber) {
        Iterable<Retention> retentions = retentionRepository.findRetentionByProviderIdAndDocumentNumber(id, documentNumber);
        retentions.forEach(retention -> {
            retention.setListsNull();
            retention.setFatherListToNull();
        });
        log.info("RetentionServices getRetentionByProviderIdAndDocumentNumber: {}, {}", id, documentNumber);
        return retentions;
    }

    public Retention createRetention(Retention retention) throws BadRequestException {
        List<DetailRetention> details = retention.getDetailRetentions();
        if (details == null) {
            throw new BadRequestException("Debe tener Debe tener el codigo de contabilidaduna retencion por lo menos");
        }
        retention.setDateCreated(new Date().getTime());
        retention.setActive(true);
        retention.setDetailRetentions(null);
        retention.setFatherListToNull();
        retention.setListsNull();
        Retention saved = retentionRepository.save(retention);
        
        Cashier cashier = cashierRepository.findOne(retention.getUserIntegridad().getCashier().getId());
        cashier.setRetentionNumberSeq(cashier.getRetentionNumberSeq() + 1);
        cashierRepository.save(cashier);
            
        details.forEach(detail -> {
            detail.setRetention(saved);
            detailRetentionRepository.save(detail);
            detail.setRetention(null);
        });
            
        log.info("RetentionServices createRetention: {}, {}", saved.getId(), saved.getStringSeq());
        saved.setDetailRetentions(details);
        return saved;
    }

    public Retention updateRetention(Retention retention) throws BadRequestException {
        if (retention.getId() == null) {
            throw new BadRequestException("Invalid Retention");
        }
        log.info("RetentionServices updateRetention: {}", retention.getId());
        Father<Retention, DetailRetention> father = new Father<>(retention, retention.getDetailRetentions());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailRetentionChildRepository, detailRetentionRepository);
        fatherUpdateChildren.updateChildren();
        log.info("RetentionServices CHILDREN updated: {}", retention.getId());
        retention.setListsNull();
        Retention updated = retentionRepository.save(retention);
        log.info("RetentionServices updateRetention: {}", updated.getId());
        return updated;
    }
    
    //Desactivación o Anulación de Retenciones
    public Retention deactivateRetention(Retention retention) throws BadRequestException {
        if (retention.getId() == null) {
            throw new BadRequestException("Invalid Retention");
        }

        Retention retentionToDeactivate = retentionRepository.findOne(retention.getId());
        retentionToDeactivate.setListsNull();
        retentionToDeactivate.setActive(false);
        retentionRepository.save(retentionToDeactivate);
        log.info("RetentionServices deactivateRetention: {}", retention.getId());
        return retentionToDeactivate;
    }

    public List<RetentionReport> getAllByUserClientIdAndDates(UUID userClientId, long dateOne, long dateTwo) {
        log.info("RetentionServices getAllByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
        Iterable<Retention> retentions = retentionRepository.findAllRetentionByUserClientIdAndDates(userClientId, dateOne, dateTwo);
        List<RetentionReport> retentionReportList = new ArrayList<>();
        retentions.forEach(retention -> {
            populateChildren(retention);
            Double sum = Double.valueOf(0);
            Double baseF = Double.valueOf(0);
            Double porcenF = Double.valueOf(0);
            Double subTotalF = Double.valueOf(0);
            Double baseIva = Double.valueOf(0);
            Double porcenIva = Double.valueOf(0);
            Double subTotalIva = Double.valueOf(0);
            String codRetenFuente = null;
            String codRetenIva = null;
            if (retention.isActive()) {
                for (DetailRetention detail : retention.getDetailRetentions()) {
                    sum = Double.sum(sum, detail.getTotal());
                    if ("RETENCION EN LA FUENTE".equals(detail.getTaxType())) {
                        baseF = Double.sum(baseF, detail.getBaseImponible());
                        porcenF = Double.sum(porcenF, detail.getPercentage());
                        subTotalF = baseF * (porcenF / 100.00);
                        if (detail.getCode() != null) {
                            codRetenFuente = detail.getCode();
                        } else {
                            codRetenFuente = null;
                        }
                    }
                    if ("RETENCION EN EL IVA".equals(detail.getTaxType())) {
                        baseIva = Double.sum(baseIva, detail.getBaseImponible());
                        porcenIva = Double.sum(porcenIva, detail.getPercentage());
                        subTotalIva = baseIva * (porcenIva / 100.00);
                        if (detail.getCode() != null) {
                            codRetenIva = detail.getCode();
                        } else {
                            codRetenIva = null;
                        }
                    }
                }
            } else {
                sum = 0.0;
                baseF = 0.0;
                porcenF = 0.0;
                subTotalF = 0.0;
                codRetenFuente = "--";
                baseIva = 0.0;
                porcenIva = 0.0;
                subTotalIva = 0.0;
                codRetenIva = "--";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(retention.getDateCreated()));
            String status = retention.isActive() ? "ACTIVA" : "ANULADA";
            String docDate = dateFormat.format(new Date(retention.getDocumentDate()));
            String retDate = dateFormat.format(new Date(retention.getDateCreated()));
            
            RetentionReport saleReport= new RetentionReport(docDate, retention.getDocumentNumber(), retention.getProvider().getRazonSocial(), retention.getProvider().getRuc(), retDate, retention.getStringSeq(), retention.getClaveDeAcceso(),
                                                            retention.getEjercicioFiscal(), retention.getDebtsSeq(), status, codRetenFuente, baseF, porcenF, subTotalF, codRetenIva, baseIva, porcenIva, subTotalIva, sum,
                                                            retention.getSubsidiary().getName(), retention.getUserIntegridad().getFirstName());

            retentionReportList.add(saleReport);
        });
        return retentionReportList;
    }

    private void populateChildren(Retention retention) {
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
    }

}