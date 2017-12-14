package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.DetailChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRepository;
import com.mrzolution.integridad.app.repositories.SubsidiaryRepository;

import lombok.extern.slf4j.Slf4j;

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
	SubsidiaryRepository subsidiaryRepository;
	@Autowired
	httpCallerService httpCallerService;

	public String getDatil() throws Exception{
		String url = "https://link.datil.co/invoices/issue";
		String data = "{\n" +
				"  \"ambiente\":1," +
				"  \"tipo_emision\":1," +
				"  \"secuencial\":148," +
				"  \"fecha_emision\":\"2015-02-28T11:28:56.782Z\"," +
				"  \"emisor\":{" +
				"    \"ruc\":\"0910000000001\"," +
				"    \"obligado_contabilidad\":true," +
				"    \"contribuyente_especial\":\"12345\"," +
				"    \"nombre_comercial\":\"XYZ Corp\"," +
				"    \"razon_social\":\"XYZ Corporación S.A.\"," +
				"    \"direccion\":\"Av. Primera 234 y calle 5ta\"," +
				"    \"establecimiento\":{" +
				"      \"punto_emision\":\"002\"," +
				"      \"codigo\":\"001\"," +
				"      \"direccion\":\"Av. Primera 234 y calle 5ta\"" +
				"    }" +
				"  }," +
				"  \"moneda\":\"USD\"," +
				"  \"informacion_adicional\":{" +
				"    \"Tiempo de entrega\":\"5 días\"" +
				"  }," +
				"  \"totales\":{" +
				"    \"total_sin_impuestos\":4359.54," +
				"    \"impuestos\":[" +
				"      {" +
				"        \"base_imponible\":0.0," +
				"        \"valor\":0.0," +
				"        \"codigo\":\"2\"," +
				"        \"codigo_porcentaje\":\"0\"" +
				"      }," +
				"      {" +
				"        \"base_imponible\":4359.54," +
				"        \"valor\":523.14," +
				"        \"codigo\":\"2\"," +
				"        \"codigo_porcentaje\":\"2\"" +
				"      }" +
				"    ]," +
				"    \"importe_total\":4882.68," +
				"    \"propina\":0.0," +
				"    \"descuento\":0.0" +
				"  }," +
				"  \"comprador\":{" +
				"    \"email\":\"juan.perez@xyz.com\"," +
				"    \"identificacion\":\"0987654321\"," +
				"    \"tipo_identificacion\":\"05\"," +
				"    \"razon_social\":\"Juan Pérez\"," +
				"    \"direccion\":\"Calle única Numero 987\"," +
				"    \"telefono\":\"046029400\"" +
				"  }," +
				"  \"items\":[" +
				"    {" +
				"      \"cantidad\":622.0," +
				"      \"codigo_principal\": \"ZNC\"," +
				"      \"codigo_auxiliar\": \"050\"," +
				"      \"precio_unitario\": 7.01," +
				"      \"descripcion\": \"Zanahoria granel  50 Kg.\"," +
				"      \"precio_total_sin_impuestos\": 4360.22," +
				"      \"impuestos\": [" +
				"        {" +
				"          \"base_imponible\":4359.54," +
				"          \"valor\":523.14," +
				"          \"tarifa\":12.0," +
				"          \"codigo\":\"2\"," +
				"          \"codigo_porcentaje\":\"2\"" +
				"        }" +
				"      ]," +
				"      \"detalles_adicionales\": {" +
				"        \"Peso\":\"5000.0000\"" +
				"      }," +
				"      \"descuento\": 0.0," +
				"      \"unidad_medida\": \"Kilos\"" +
				"    }" +
				"  ]," +
				"  \"valor_retenido_iva\": 70.40," +
				"  \"valor_retenido_renta\": 29.60," +
				"  \"credito\": {" +
				"    \"fecha_vencimiento\": \"2015-03-28\"," +
				"    \"monto\": 34.21" +
				"  }," +
				"  \"pagos\": [" +
				"    {" +
				"      \"medio\": \"cheque\"," +
				"      \"total\": 4882.68," +
				"      \"propiedades\": {" +
				"        \"numero\": \"1234567890\"," +
				"        \"banco\": \"Banco Pacífico\"" +
				"      }" +
				"    }" +
				"  ]," +
				"  \"compensaciones\": [" +
				"    {" +
				"      \"codigo\": 1," +
				"      \"tarifa\": 2," +
				"      \"valor\": 2.00" +
				"    }" +
				"  ]," +
				"  \"exportacion\": {" +
				"    \"incoterm\": {" +
				"      \"termino\": \"CIF\"," +
				"      \"lugar\": \"Guayaquil\"," +
				"      \"total_sin_impuestos\": \"CIF\"" +
				"    }," +
				"    \"origen\": {" +
				"      \"codigo_pais\":\"EC\"," +
				"      \"puerto\": \"Guayaquil\"" +
				"    }," +
				"    \"destino\": {" +
				"      \"codigo_pais\":\"CN\"," +
				"      \"puerto\": \"China\"" +
				"    }," +
				"    \"codigo_pais_adquisicion\": \"EC\"," +
				"    \"totales\": {" +
				"      \"flete_internacional\": 1000.00," +
				"      \"seguro_internacional\": 200.00," +
				"      \"gastos_aduaneros\": 800," +
				"      \"otros_gastos_transporte\": 350.00" +
				"    }" +
				"  }" +
				"}";
		String response = httpCallerService.post(url, data);
		return response;
	}
	
	public Iterable<Bill> getByUserLazy(UserIntegridad user){
		log.info("BillServices getByUserLazy: {}", user.getId());
		Iterable<Bill> bills = billRepository.findByUserIntegridad(user);
		bills.forEach(bill->{
			bill.setListsNull();
			bill.setFatherListToNull();
			});
		
		return bills;
	}
	
	public Bill getById(UUID id) {
		log.info("BillServices getById: {}", id);
		Bill retrieved = billRepository.findOne(id);
		if(retrieved != null){
			log.info("BillServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("BillServices retrieved id NULL: {}", id);
		}
		
		populateChildren(retrieved);
		return retrieved;
	}
	
	public Bill create(Bill bill) throws BadRequestException{
		log.info("BillServices create");
		List<Detail> details = bill.getDetails();
		
		if(details == null){
			throw new BadRequestException("Debe tener un detalle por lo menos");
		}
		
		bill.setDateCreated(new Date().getTime());
		bill.setActive(true);
		bill.setDetails(null);
		bill.setFatherListToNull();
		bill.setListsNull();
		Bill saved = billRepository.save(bill);
		
		Subsidiary subsidiary =  subsidiaryRepository.findOne(bill.getSubsidiary().getId());
		subsidiary.setBillNumberSeq(subsidiary.getBillNumberSeq() + 1);
		subsidiaryRepository.save(subsidiary);
		
		details.forEach(detail->{
			detail.setBill(saved);
			detailRepository.save(detail);
			detail.setBill(null);
		});
		
		log.info("BillServices created id: {}", saved.getId());
		saved.setDetails(details);
		return saved;
	}
	
	public Bill update(Bill bill) throws BadRequestException{
		if(bill.getId() == null){
			throw new BadRequestException("Invalid Bill");
		}
		log.info("BillServices update: {}", bill.getId());
		Father<Bill, Detail> father = new Father<>(bill, bill.getDetails());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, detailChildRepository, detailRepository);
        fatherUpdateChildren.updateChildren();

        log.info("BillServices CHILDREN updated: {}", bill.getId());
        
		bill.setListsNull();
		Bill updated = billRepository.save(bill);
		log.info("BillServices update id: {}", updated.getId());
		return updated;
	}
	
	private void populateChildren(Bill bill) {
		log.info("BillServices populateChildren billId: {}", bill.getId());
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
		
		bill.setDetails(detailList);
		bill.setFatherListToNull();
		log.info("BillServices populateChildren FINISHED billId: {}", bill.getId());
	}

}
