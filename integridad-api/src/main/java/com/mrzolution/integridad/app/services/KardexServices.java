package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Kardex;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.report.KardexOfProductReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.KardexRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import java.math.BigDecimal;
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
public class KardexServices {
    @Autowired
    KardexRepository kardexRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    
    public UUID usrClntId;
    public UUID subsidiaryId;
    
    public double sumEntra;
    public double sumSale;
    public double sumSaldo;
    public double sumCompra;
    public double sumProm;
    public double quanty;
    
    public double totalEntra;
    public double totalCompra;
    
    public List<KardexOfProductReport> getKardexActivesByUserClientIdAndProductIdAndDates(String id, UUID prodID, long dateOne, long dateTwo) {
        log.info("KardexServices getKardexActivesByUserClientIdAndProductIdAndDates: {}, {}, {}", prodID, dateOne, dateTwo);
        Iterable<Kardex> detaKardex = kardexRepository.findKardexActivesByUserClientIdAndProductIdAndDates(id, prodID, dateOne, dateTwo);
        List<KardexOfProductReport> kardexOfProductReportList = new ArrayList<>();
        
        sumEntra = 0.0;
        sumSale = 0.0;
        sumSaldo = 0.0;
        quanty = 0.0;
        totalEntra = 0.0;
        totalCompra = 0.0;
        sumProm = 0.0;
        
        detaKardex.forEach(kard -> {
            if (kard.isActive()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
                String fechaReg = dateFormat.format(new Date(kard.getDateRegister()));
                
                if ("INGRESO".equals(kard.getObservation())) {
                    sumCompra = kard.getProdCostEach();
                    totalEntra = kard.getProdQuantity();
                    quanty = quanty + totalEntra;
                    totalCompra = totalCompra + kard.getProdTotal();
                    sumProm = totalCompra / quanty;
                }
                
                if ("INGRESO".equals(kard.getObservation()) || "NCV".equals(kard.getObservation())) {
                    sumEntra = kard.getProdQuantity();
                    sumSale = 0.0;
                } else {
                    sumEntra = 0.0;
                    sumCompra = 0.0;
                    sumSale = kard.getProdQuantity();
                }
                
                sumSaldo = sumSaldo + (sumEntra - sumSale);
                
                KardexOfProductReport kardexOfProductReport = new KardexOfProductReport(fechaReg, kard.getProdName(), kard.getDetails(), sumEntra, sumSale, sumSaldo, sumCompra, sumProm);
                
                kardexOfProductReportList.add(kardexOfProductReport);
            }
        });
        return kardexOfProductReportList;
    }
    
    @Async("asyncExecutor")
    public Kardex createKardex(Kardex kard) throws BadRequestException {
        if (kard.getProdQuantity() <= 0) {
            throw new BadRequestException("La cantidad ingresada NO puede ser menor o igual a 0");
        }
        kard.setActive(true);
        kard.setListsNull();
        kard.setFatherListToNull();
        Kardex karSvd = kardexRepository.save(kard);
        usrClntId = UUID.fromString(karSvd.getUserClientId());
        subsidiaryId = UUID.fromString(karSvd.getSubsidiaryId());

        if ("INGRESO".equals(karSvd.getObservation())) {
            //Actualización del campo (quantity) en tabla Product by Subsiduary
            ProductBySubsidiary prodSub = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(subsidiaryId, karSvd.getProduct().getId());
                prodSub.setQuantity(prodSub.getQuantity() + karSvd.getProdQuantity());
            productBySubsidiairyRepository.save(prodSub);
            
            //Actualización de los campos (quantity_cellar, cost_cellar, average_cost_suggested) en tabla Product
            Long qntCel = Long.valueOf(0);
            Double cstCel = Double.valueOf(0);
            Double avgCst = Double.valueOf(0);
            
            Product prod = productRepository.findPrdByUsrClntAndId(usrClntId, karSvd.getProduct().getId());
            qntCel = karSvd.getProdQuantity() + prod.getQuantityCellar();
            cstCel = karSvd.getProdTotal() + prod.getCostCellar();
            avgCst = cstCel / qntCel;
            BigDecimal vavgCst = new BigDecimal(avgCst);
            if (avgCst > 0) {
                vavgCst = vavgCst.setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            avgCst = vavgCst.doubleValue();
            prod.setQuantityCellar(qntCel);
            prod.setCostCellar(cstCel);
            prod.setAverageCostSuggested(avgCst);
            prod.setAverageCost(avgCst);
            productRepository.save(prod);
        } else {
            //Actualización del campo cantidad en Product by Subsiduary
            ProductBySubsidiary prodSub = productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(subsidiaryId, karSvd.getProduct().getId());
                prodSub.setQuantity(prodSub.getQuantity() - karSvd.getProdQuantity());
            productBySubsidiairyRepository.save(prodSub);            
        }
        return karSvd;
    }
}