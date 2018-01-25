package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.ClientRepository;
import com.mrzolution.integridad.app.repositories.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ProviderServices {

	@Autowired
	ProviderRepository providerRepository;

	public Provider create(Provider provider){
		if(provider.getCodeIntegridad() == null){
			throw new BadRequestException("Debe tener el codigo de contabilidad");
		}
		log.info("ProviderServices create: {}", provider.getName());
		provider.setDateCreated(new Date().getTime());
		provider.setActive(true);
		Provider saved = providerRepository.save(provider);
		log.info("ProviderServices created id: {}", saved.getId());
		return saved;
	}
	
//	public void update(Client client) throws BadRequestException{
//		if(client.getId() == null){
//			throw new BadRequestException("Invalid Client");
//		}
//		log.info("ClientServices update: {}", client.getName());
//		client.setListsNull();
//		Client updated = clientRepository.save(client);
//		log.info("ClientServices updated id: {}", updated.getId());
//	}
//
	public Provider getById(UUID id){
		log.info("ProviderServices getById: {}", id);
		Provider retrieved = providerRepository.findOne(id);
		if(retrieved != null){
			log.info("ProviderServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("ProviderServices retrieved id NULL: {}", id);
		}

//		populateChildren(retrieved);
		return retrieved;
	}
//
//	public Iterable<Client> getAll(){
//		log.info("ClientServices getAll");
//		Iterable<Client> clients = clientRepository.findAll();
//		for (Client client : clients) {
//			populateChildren(client);
//		}
//		log.info("ClientServices getAll size retrieved: {}", Iterables.size(clients));
//		return clients;
//	}
//
	public Iterable<Provider> getAllLazy(){
		log.info("ProviderServices getAllLazy");
		Iterable<Provider> providers = providerRepository.findByActive(true);
		for (Provider provider : providers) {
			provider.setListsNull();
			provider.setFatherListToNull();
		}
		log.info("ProviderServices getAllLazy size retrieved: {}", Iterables.size(providers));
		return providers;
	}
//
//	public Iterable<Client> getAllLazyByUserClientid(UUID userClientId){
//		log.info("ClientServices getAllLazyByUserClientid id: {}", userClientId);
//		Iterable<Client> clients = clientRepository.findActivesByUserClientId(userClientId);
//		for (Client client : clients) {
//			client.setListsNull();
//			client.setFatherListToNull();
//		}
//		log.info("ClientServices getAllLazyByUserClientid size retrieved: {}", Iterables.size(clients));
//		return clients;
//	}
//
//	private void populateChildren(Client client) {
//		log.info("ClientServices populateChildren clientId: {}", client.getId());
//		List<Bill> billList = new ArrayList<>();
//		Iterable<Bill> bills= billRepository.findByClient(client);
//
//		for (Bill bill : bills) {
//			bill.setFatherListToNull();
//			bill.setListsNull();
//			bill.setClient(null);
//
//			billList.add(bill);
//		}
//
//		client.setBills(billList);
//		log.info("ClientServices populateChildren FINISHED clientId: {}", client.getId());
//	}

}
