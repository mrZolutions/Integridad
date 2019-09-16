package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Kardex;
import com.mrzolution.integridad.app.domain.report.KardexOfProductReport;
import com.mrzolution.integridad.app.repositories.KardexRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    public double sumEntra;
    public double sumSale;
    public double sumSaldo;
    public double sumCompra;
    public double sumProm;
    public double quanty;
    
    public double totalEntra;
    public double totalCompra;
    
    public List<KardexOfProductReport> getKardexActivesByUserClientIdAndProductIdAndDates(String id, UUID prodID, long dateOne, long dateTwo) {
        log.info("KardexServices getKardexActivesByUserClientIdAndProductIdAndDates");
        Iterable<Kardex> detaKardex = kardexRepository.findKardexActivesByUserClientIdAndProductIdAndDates(id, prodID, dateOne, dateTwo);
        List<KardexOfProductReport> kardexOfProductReportList = new ArrayList<>();
        
        sumEntra = 0.0;
        sumSale = 0.0;
        sumSaldo = 0.0;
        quanty = 0.0;
        totalEntra = 0.0;
        totalCompra = 0.0;
        sumProm = 0.0;
        
        detaKardex.forEach(kard ->{
            if (kard.isActive()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
}