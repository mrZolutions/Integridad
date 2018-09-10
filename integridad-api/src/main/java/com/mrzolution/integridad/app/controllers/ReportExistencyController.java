/**
 *
 * @author daniel-one
 */
package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.report.ReportExistency;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ReportExistencyServices;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/report")
public class ReportExistencyController {
    @Autowired
    ReportExistencyServices services;
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/existency/{userClientId}")
    public ResponseEntity getProductsByUserClientId(@PathVariable("userClientId") UUID userClientId) {
        log.info("ReportExistencyController getProductsByUserClientId: {}", userClientId);
        List<ReportExistency> response = null;
        
        try{
            
        }catch(BadRequestException e){
            log.error("RetentionController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}   
