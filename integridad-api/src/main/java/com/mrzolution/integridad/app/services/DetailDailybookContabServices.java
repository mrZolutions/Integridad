package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.domain.report.AllDailyReport;
import com.mrzolution.integridad.app.domain.report.EspecificMajorReport;
import com.mrzolution.integridad.app.domain.report.GeneralMajorReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    DailybookCxPRepository dailybookCxPRepository;
    @Autowired
    DailybookCeRepository dailybookCeRepository;
    @Autowired
    DailybookCgRepository dailybookCgRepository;
    @Autowired
    DailybookCiRepository dailybookCiRepository;
    @Autowired
    DailybookFvRepository dailybookFvRepository;
    
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
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
                String fechaBook = dateFormat.format(new Date(detail.getDateDetailDailybook()));
            
                Double sumaDeber = Double.valueOf(0);
                Double sumaHaber = Double.valueOf(0);
            
                if ("DEBITO (D)".equals(detail.getTipo())) {
                    sumaDeber = detail.getBaseImponible();
                } else if ("CREDITO (C)".equals(detail.getTipo())){
                    sumaHaber = detail.getBaseImponible();
                } else {
                    sumaDeber = 0.0;
                    sumaHaber = 0.0;
                }
            
                sumSaldo = sumSaldo + (sumaDeber - sumaHaber);
                BigDecimal vsaldo = new BigDecimal(sumSaldo);
                if (sumSaldo == 0) {
                    vsaldo = vsaldo.setScale(0, BigDecimal.ROUND_HALF_UP);
                } else if (sumSaldo < 0) {
                    vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    vsaldo = vsaldo.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                sumSaldo = vsaldo.doubleValue();
            
                totalDeber = Double.sum(totalDeber, sumaDeber);
                totalHaber = Double.sum(totalHaber, sumaHaber);
                
                totalSaldo = totalDeber - totalHaber;
            
                EspecificMajorReport especificMajorReport = new EspecificMajorReport(fechaBook, detail.getTypeContab(), detail.getDailybookNumber(), detail.getName(),
                                                                                     detail.getNumCheque(), sumaDeber, sumaHaber, sumSaldo);
                especificMajorReportList.add(especificMajorReport);
            }
        });
        EspecificMajorReport especificMajorReports = new EspecificMajorReport("TOTAL GENERAL:", null, null, null, null, totalDeber, totalHaber, totalSaldo);
        especificMajorReportList.add(especificMajorReports);
        
        return especificMajorReportList;
    }
    
    public List<GeneralMajorReport> getGenMajorReportByUsrClntIdAndCodeContaAndDate(String id, String codeOne, String codeTwo, long dateOne, long dateTwo) {
        totalDeber = 0.0;
        totalHaber = 0.0;
        totalSaldo = 0.0;
        sumSaldo = 0.0;

        sumSubTotDeber = 0.0;
        sumSubTotHaber = 0.0;
        sumSubTotSaldo = 0.0;
        
        log.info("DetailDailybookContabServices getGeneralMajorReportByUsrClntIdAndCtaCtbleAndDate");
        Iterable<DetailDailybookContab> detailGen = detailDailybookContabRepository.findGeneralMajorByUsrClntIdAndCodeContaAndDate(id, codeOne, codeTwo, dateOne, dateTwo);
        List<GeneralMajorReport> generalMajorReportList = new ArrayList<>();
        
        if (Iterables.size(detailGen) > 0) {
            DetailDailybookContab firstDetail = Iterables.getFirst(detailGen, new DetailDailybookContab());
            codeContab = firstDetail.getCodeConta();
        }
        
        detailGen.forEach(detGen -> {
            if (detGen.isActive()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
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

    public List<AllDailyReport> getAllDailyReportByUserClntIdAndDate(String id, long dateOne, long dateTwo){
        log.info("DetailDailybookContabServices getAllDailyReportByUserClntIdAndDate: {}, {}, {}", id, dateOne, dateTwo);
        Iterable<DetailDailybookContab> detailCe = detailDailybookContabRepository.findDailyCeByUsrClntIdAndAndDate(id, dateOne, dateTwo);
        Iterable<DetailDailybookContab> detailCg = detailDailybookContabRepository.findDailyCgByUsrClntIdAndAndDate(id, dateOne, dateTwo);
        Iterable<DetailDailybookContab> detailCi = detailDailybookContabRepository.findDailyCiByUsrClntIdAndAndDate(id, dateOne, dateTwo);
        Iterable<DetailDailybookContab> detailCxp = detailDailybookContabRepository.findDailyCxpByUsrClntIdAndAndDate(id, dateOne, dateTwo);
        Iterable<DetailDailybookContab> detailFv = detailDailybookContabRepository.findDailyFvByUsrClntIdAndAndDate(id, dateOne, dateTwo);

        Iterable<DetailDailybookContab> detailTotal = Iterables.concat(detailCe, detailCg, detailCi, detailCxp, detailFv);
        List<AllDailyReport> arrayReturn = new ArrayList<AllDailyReport>();
        long[] arrayValidator = {0};
        String [] arrayValidatorThree = {null};
        AllDailyReport [] arrayValidatorTwo = {null};

        detailTotal.forEach(detGen -> {
            if(detGen.getDateDetailDailybook() != arrayValidator[0] || !detGen.getDailybookNumber().equals(arrayValidatorThree[0])){
                if(arrayValidator[0] != 0){
                    arrayReturn.add(arrayValidatorTwo[0]);
                    arrayValidatorTwo[0] = null;
                }
                arrayValidator[0] = detGen.getDateDetailDailybook();
                arrayValidatorThree[0] = detGen.getDailybookNumber();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
                String fechaBook = dateFormat.format(new Date(detGen.getDateDetailDailybook()));
                String tipoDocumento = new String();
                String clienteProveedor = new String();
                String detalle = new String();
                String numero =  detGen.getDailybookNumber();
                String numeroFactura = new String();


                if(detGen.getDailybookCg() != null){
                    tipoDocumento = "CONTABILIDAD GENERAL";
                    detalle = detGen.getDailybookCg().getGeneralDetail();
                    clienteProveedor = "--";
                    numeroFactura = "--";
                }
                if(detGen.getDailybookCe() != null){
                    tipoDocumento = "COMPROBANTE EGRESO";
                    detalle = detGen.getDailybookCe().getGeneralDetail();
                    clienteProveedor = detGen.getDailybookCe().getClientProvName();
                    numeroFactura = detGen.getDailybookCe().getBillNumber();
                }
                if(detGen.getDailybookCi() != null){
                    tipoDocumento = "COMPROBANTE INGRESO";
                    detalle = detGen.getDailybookCi().getGeneralDetail();
                    clienteProveedor = detGen.getDailybookCi().getClientProvName();
                    numeroFactura = detGen.getDailybookCi().getBillNumber();
                }
                if(detGen.getDailybookCxP() != null){
                    tipoDocumento = "CUENTAS POR PAGAR";
                    detalle = detGen.getDailybookCxP().getGeneralDetail();
                    clienteProveedor = detGen.getDailybookCxP().getClientProvName();
                    numeroFactura = detGen.getDailybookCxP().getBillNumber();
                }
                if(detGen.getDailybookFv() != null){
                    tipoDocumento = "FACTURACION VENTAS";
                    detalle = detGen.getDailybookFv().getGeneralDetail();
                    clienteProveedor = detGen.getDailybookFv().getClientProvName();
                    numeroFactura = detGen.getDailybookFv().getBillNumber();
                }

                AllDailyReport data = new AllDailyReport(fechaBook, tipoDocumento, clienteProveedor,
                        detalle, numero, numeroFactura, Double.valueOf(0), Double.valueOf(0));
                arrayValidatorTwo[0] = data;

            }

            if(detGen.getDeber() != null && !detGen.getDeber().equals("null")){
                arrayValidatorTwo[0].setDeber(Double.sum(Double.valueOf(detGen.getDeber()), arrayValidatorTwo[0].getDeber()));
            }
            if(detGen.getHaber() != null && !detGen.getHaber().equals("null")){
                arrayValidatorTwo[0].setHaber(Double.sum(Double.valueOf(detGen.getHaber()), arrayValidatorTwo[0].getHaber()));
            }
        });

        if(arrayValidatorTwo[0] != null){
            arrayReturn.add(arrayValidatorTwo[0]);
            arrayValidatorTwo[0] = null;
        }

        return arrayReturn;
    }

    public UUID upsertDailyBooks(List<DetailDailybookContab> details, String type, String dailyId) throws BadRequestException {

        if(details.size() <= 0) {
            throw new BadRequestException("Debe existir por lo menos un detalle");
        }

        UUID result = null;

        DailybookCxP dailyCxP = null;
        DailybookCe dailyCe = null;
        DailybookCg dailyCg = null;
        DailybookCi dailyCi = null;
        DailybookFv dailyFv = null;

        Iterable<DetailDailybookContab> detailOld = null;

        switch(type.toUpperCase()) {
            case "CXP":
                dailyCxP = dailybookCxPRepository.findOne(UUID.fromString(dailyId));
                dailyCxP.setListsNull();
                dailyCxP.setFatherListToNull();
                detailOld = detailDailybookContabRepository.findByDailybookCxP(dailyCxP);
                result = dailyCxP.getId();
                break;
            case "CE":
                dailyCe = dailybookCeRepository.findOne(UUID.fromString(dailyId));
                dailyCe.setListsNull();
                dailyCe.setFatherListToNull();
                detailOld = detailDailybookContabRepository.findByDailybookCe(dailyCe);
                result = dailyCe.getId();
                break;
            case "CG":
                dailyCg = dailybookCgRepository.findOne(UUID.fromString(dailyId));
                dailyCg.setListsNull();
                dailyCg.setFatherListToNull();
                detailOld = detailDailybookContabRepository.findByDailybookCg(dailyCg);
                result = dailyCg.getId();
                break;
            case "CI":
                dailyCi = dailybookCiRepository.findOne(UUID.fromString(dailyId));
                dailyCi.setListsNull();
                dailyCi.setFatherListToNull();
                detailOld = detailDailybookContabRepository.findByDailybookCi(dailyCi);
                result = dailyCi.getId();
                break;
            case "FV":
                dailyFv = dailybookFvRepository.findOne(UUID.fromString(dailyId));
                dailyFv.setListsNull();
                dailyFv.setFatherListToNull();
                detailOld = detailDailybookContabRepository.findByDailybookFv(dailyFv);
                result = dailyFv.getId();
                break;
            default:
                throw new BadRequestException("Type incorrecto");
        }

        for (DetailDailybookContab detGen : detailOld) {
            Optional<DetailDailybookContab> first = details.stream().filter(det -> det.getId().equals(detGen.getId())).findFirst();

            if (first.isPresent()) {
                DetailDailybookContab detailToSave = first.get();
                detailToSave.setDailybookCe(dailyCe);
                detailToSave.setDailybookCxP(dailyCxP);
                detailToSave.setDailybookCi(dailyCi);
                detailToSave.setDailybookCg(dailyCg);
                detailToSave.setDailybookFv(dailyFv);
                detailDailybookContabRepository.save(detailToSave);
                details.remove(detailToSave);
            } else {
                detGen.setDailybookCe(dailyCe);
                detGen.setDailybookCxP(dailyCxP);
                detGen.setDailybookCi(dailyCi);
                detGen.setDailybookCg(dailyCg);
                detGen.setDailybookFv(dailyFv);
                detGen.setActive(false);
                detailDailybookContabRepository.delete(detGen.getId());
            }
        }


        for(DetailDailybookContab det : details){
            det.setDailybookCe(dailyCe);
            det.setDailybookCxP(dailyCxP);
            det.setDailybookCi(dailyCi);
            det.setDailybookCg(dailyCg);
            det.setDailybookFv(dailyFv);
            String base = "0";

            if(det.getHaber() != null){
                base = det.getHaber();
                det.setTipo("CREDITO (C)");
            }

            if(det.getDeber() != null){
                base = det.getDeber();
                det.setTipo("DEBITO (D)");
            }

            det.setBaseImponible(Double.valueOf(base));
            detailDailybookContabRepository.save(det);
        }
        return result;
    }
}