package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.domain.report.EspecificMajorReport;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;
import java.math.BigDecimal;
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
public class DetailDailybookContabServices {
    @Autowired
    DetailDailybookContabRepository detailDailybookContabRepository;
    
    private double sumSaldo;
    private double totalDeber;
    private double totalHaber;
    private double totalSaldo;
    
    public List<EspecificMajorReport> getEspecificMajorReportByUserClientIdAndDates(String id, String code, long dateOne, long dateTwo) {
        log.info("DetailDailybookContabServices getEspecificMajorReportByUserClientIdAndDates");
        Iterable<DetailDailybookContab> details = detailDailybookContabRepository.findEspecificMajorByUserClientIdAndDates(id, code, dateOne, dateTwo);
        List<EspecificMajorReport> especificMajorReportList = new ArrayList<>();
        sumSaldo = 0.0;
        totalDeber = 0.0;
        totalHaber = 0.0;
        totalSaldo = 0.0;
        details.forEach(detail -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fechaBook = dateFormat.format(new Date(detail.getDateDetailDailybook()));
            
            Double sumDeber = Double.valueOf(0);
            Double sumHaber = Double.valueOf(0);
            
            if ("DEBITO (D)".equals(detail.getTipo())) {
                sumDeber = detail.getBaseImponible();
            } else if ("CREDITO (C)".equals(detail.getTipo())){
                sumHaber = detail.getBaseImponible();
            } else {
                sumDeber = 0.0;
                sumHaber = 0.0;
            }
            
            sumSaldo = sumSaldo + (sumDeber - sumHaber);
            BigDecimal vsaldo = new BigDecimal(sumSaldo);
            if (sumSaldo == 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else if (sumSaldo < 0) {
                vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else {
                vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            sumSaldo = vsaldo.doubleValue();
            
            totalDeber = Double.sum(totalDeber, sumDeber);
            totalHaber = Double.sum(totalHaber, sumHaber);
            
            totalSaldo = totalDeber - totalHaber;
            BigDecimal vtotalSaldo = new BigDecimal(totalSaldo);
            if (totalSaldo == 0) {
                vtotalSaldo = vtotalSaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else if (totalSaldo < 0) {
                vtotalSaldo = vtotalSaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
            } else {
                vtotalSaldo = vtotalSaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            totalSaldo = vtotalSaldo.doubleValue();
            
            EspecificMajorReport especificMajorReport = new EspecificMajorReport(fechaBook, detail.getTypeContab(), detail.getDailybookNumber(), detail.getName(),
                                                                                 detail.getNumCheque(), sumDeber, sumHaber, sumSaldo);
            especificMajorReportList.add(especificMajorReport);
        });
        EspecificMajorReport especificMajorReports = new EspecificMajorReport("TOTAL GENERAL", null, null, null, null, totalDeber, totalHaber, totalSaldo);
        especificMajorReportList.add(especificMajorReports);
        
        return especificMajorReportList;
    }
}