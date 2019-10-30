package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.domain.report.EspecificMajorReport;
import com.mrzolution.integridad.app.domain.report.GeneralMajorReport;
import com.mrzolution.integridad.app.repositories.DetailDailybookContabRepository;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private double sumDeber;
    private double sumHaber;
    private double totalDeber;
    private double totalHaber;
    private double totalSaldo;
    
    private double sumSaldoPrev;
    private double totalDeberPrev;
    private double totalHaberPrev;
    private double totalSaldoPrev;
    
    private double sumSubTotDeber;
    private double sumSubTotHaber;
    private double sumSubTotSaldo;
    
    private String codeContab;
    
    public List<EspecificMajorReport> getEspfcMajorReportByUserClientIdAndDates(String id, String code, long dateOne, long dateTwo) {
        log.info("DetailDailybookContabServices getEspecificMajorReportByUserClientIdAndDates");
        Iterable<DetailDailybookContab> previousDetails = detailDailybookContabRepository.findPreviousEspecificMajorByUsrClntIdAndDate(id, code, dateOne);
        Iterable<DetailDailybookContab> details = detailDailybookContabRepository.findEspecificMajorByUsrClntIdAndDates(id, code, dateOne, dateTwo);
        List<EspecificMajorReport> especificMajorReportList = new ArrayList<>();
        sumSaldoPrev = 0.0;
        totalDeberPrev = 0.0;
        totalHaberPrev = 0.0;
        totalSaldoPrev = 0.0;
        
        previousDetails.forEach(previous -> {
            if (previous.isActive()) {
                Double sumDeberPrev = Double.valueOf(0);
                Double sumHaberPrev = Double.valueOf(0);
            
                if ("DEBITO (D)".equals(previous.getTipo())) {
                    sumDeberPrev = previous.getBaseImponible();
                } else if ("CREDITO (C)".equals(previous.getTipo())){
                    sumHaberPrev = previous.getBaseImponible();
                } else {
                    sumDeberPrev = 0.0;
                    sumHaberPrev = 0.0;
                }
            
                sumSaldoPrev = sumSaldoPrev + (sumDeberPrev - sumHaberPrev);
                BigDecimal vsaldoPrev = new BigDecimal(sumSaldoPrev);
                if (sumSaldoPrev == 0) {
                    vsaldoPrev = vsaldoPrev.setScale(0, BigDecimal.ROUND_HALF_UP);
                } else if (sumSaldoPrev < 0) {
                    vsaldoPrev = vsaldoPrev.setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    vsaldoPrev = vsaldoPrev.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                sumSaldoPrev = vsaldoPrev.doubleValue();
            
                totalDeberPrev = Double.sum(totalDeberPrev, sumDeberPrev);
                totalHaberPrev = Double.sum(totalHaberPrev, sumHaberPrev);
            
                totalSaldoPrev = totalDeberPrev - totalHaberPrev;
                BigDecimal vtotalSaldoPrev = new BigDecimal(totalSaldoPrev);
                if (totalSaldoPrev == 0) {
                    vtotalSaldoPrev = vtotalSaldoPrev.setScale(0, BigDecimal.ROUND_HALF_UP);
                } else if (totalSaldoPrev < 0) {
                    vtotalSaldoPrev = vtotalSaldoPrev.setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    vtotalSaldoPrev = vtotalSaldoPrev.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                totalSaldoPrev = vtotalSaldoPrev.doubleValue();
            }
        });
        EspecificMajorReport previousMajorReports = new EspecificMajorReport("SALDO ANTERIOR:", null, null, null, null, totalDeberPrev, totalHaberPrev, totalSaldoPrev);
        especificMajorReportList.add(previousMajorReports);
        
        sumSaldo = totalSaldoPrev;
        totalDeber = 0.0;
        totalHaber = 0.0;
        
        details.forEach(detail -> {
            if (detail.isActive()) {
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
                    vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                sumSaldo = vsaldo.doubleValue();
            
                totalDeber = Double.sum(totalDeber, sumDeber);
                totalHaber = Double.sum(totalHaber, sumHaber);
                
                totalSaldo = totalDeber - totalHaber;
            
                EspecificMajorReport especificMajorReport = new EspecificMajorReport(fechaBook, detail.getTypeContab(), detail.getDailybookNumber(), detail.getName(),
                                                                                 detail.getNumCheque(), sumDeber, sumHaber, sumSaldo);
                especificMajorReportList.add(especificMajorReport);
            }
        });
        EspecificMajorReport especificMajorReports = new EspecificMajorReport("TOTAL GENERAL:", null, null, null, null, totalDeber, totalHaber, totalSaldo);
        especificMajorReportList.add(especificMajorReports);
        
        return especificMajorReportList;
    }
    
    public List<GeneralMajorReport> getGenMajorReportByUsrClntIdAndCodeContaAndDate(String id, String codeOne, String codeTwo, long dateOne) {
        totalDeber = 0.0;
        totalHaber = 0.0;
        totalSaldo = 0.0;
        sumSaldo = 0.0;

        sumSubTotDeber = 0.0;
        sumSubTotHaber = 0.0;
        sumSubTotSaldo = 0.0;
        
        log.info("DetailDailybookContabServices getGeneralMajorReportByUsrClntIdAndCtaCtbleAndDate");
        Iterable<DetailDailybookContab> detailGen = detailDailybookContabRepository.findGeneralMajorByUsrClntIdAndCodeContaAndDate(id, codeOne, codeTwo, dateOne);
        List<GeneralMajorReport> generalMajorReportList = new ArrayList<>();
        
        if (Iterables.size(detailGen) > 0) {
            DetailDailybookContab firstDetail = Iterables.getFirst(detailGen, new DetailDailybookContab());
            codeContab = firstDetail.getCodeConta();
        }
        
        detailGen.forEach(detGen -> {
            if (detGen.isActive()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String fechaBook = dateFormat.format(new Date(detGen.getDateDetailDailybook()));                                
                if (codeContab != null && codeContab.equals(detGen.getCodeConta())) {
                    sumDeber = 0.0;
                    sumHaber = 0.0;
                    if ("DEBITO (D)".equals(detGen.getTipo())) {
                        sumDeber = detGen.getBaseImponible();
                    } else if ("CREDITO (C)".equals(detGen.getTipo())){
                        sumHaber = detGen.getBaseImponible();
                    }
                    sumSaldo = sumSaldo + (sumDeber - sumHaber);
                    sumSubTotDeber = sumSubTotDeber + sumDeber;
                    sumSubTotHaber = sumSubTotHaber + sumHaber;
                    sumSubTotSaldo = sumSubTotDeber - sumSubTotHaber;
                } else {
                    sumDeber = 0.0;
                    sumHaber = 0.0;
                    codeContab = detGen.getCodeConta();
                    if ("DEBITO (D)".equals(detGen.getTipo())) {
                        sumDeber = detGen.getBaseImponible();
                    } else if ("CREDITO (C)".equals(detGen.getTipo())){
                        sumHaber = detGen.getBaseImponible();
                    }
                    GeneralMajorReport generalMajorReport = new GeneralMajorReport(null, null, null, null, null, "SUB-TOTAL:", sumSubTotDeber, sumSubTotHaber, sumSubTotSaldo, "--");
                    generalMajorReportList.add(generalMajorReport);
                    sumSubTotDeber = sumDeber;
                    sumSubTotHaber = sumHaber;
                    sumSaldo = sumDeber - sumHaber;
                    sumSubTotSaldo = sumSubTotDeber - sumSubTotHaber;
                }
            
                totalDeber = Double.sum(totalDeber, sumDeber);
                totalHaber = Double.sum(totalHaber, sumHaber);
                totalSaldo = totalDeber - totalHaber;
            
                GeneralMajorReport generalMajorReport = new GeneralMajorReport(detGen.getCodeConta(), fechaBook, detGen.getTypeContab(), detGen.getDailybookNumber(), detGen.getName(),
                                                                               detGen.getNumCheque(), sumDeber, sumHaber, sumSaldo, "--");
                generalMajorReportList.add(generalMajorReport);
            }
        });
        GeneralMajorReport generalMajorReport = new GeneralMajorReport(null, null, null, null, null, "SUB-TOTAL:", sumSubTotDeber, sumSubTotHaber, sumSubTotSaldo, "--");
        generalMajorReportList.add(generalMajorReport);
        
        GeneralMajorReport generalMajorReports = new GeneralMajorReport("TOTAL GENERAL:", null, null, null, null, null, totalDeber, totalHaber, totalSaldo, "");
        generalMajorReportList.add(generalMajorReports);
        
        return generalMajorReportList;
    }
}