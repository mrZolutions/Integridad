package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class ProviderServices {
    @Autowired
    ProviderRepository providerRepository;
    
    public Iterable<Provider> getProviderByUserClientIdAndRuc(UUID userClientId, String ruc) {
	Iterable<Provider> providers = providerRepository.findProviderByUserClientIdAndRuc(userClientId, ruc);
	providers.forEach(provider -> {
            provider.setFatherListToNull();
            provider.setListsNull();
        });
	return providers;
    }

    public Provider createProvider(Provider provider) {
	if (provider.getCodeIntegridad() == null) {
            throw new BadRequestException("Debe tener el codigo de contabilidad");
	}
        
	log.info("ProviderServices create: {}", provider.getName());
	provider.setDateCreated(new Date().getTime());
	provider.setActive(true);
	Provider saved = providerRepository.save(provider);
	log.info("ProviderServices created id: {}", saved.getId());
	return saved;
    }

    public void updateProvider(Provider provider) throws BadRequestException {
	if (provider.getId() == null) {
		throw new BadRequestException("Invalid Provider");
	}
	log.info("ProviderServices update: {}", provider.getName());
	provider.setListsNull();
	Provider updated = providerRepository.save(provider);
	log.info("ProviderServices updated id: {}", updated.getId());
    }

    public Provider getProviderById(UUID id) {
	log.info("ProviderServices getProviderById: {}", id);
	Provider retrieved = providerRepository.findOne(id);
	if (retrieved != null) {
            log.info("ProviderServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("ProviderServices retrieved id NULL: {}", id);
	}
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
    public Iterable<Provider> getAllLazy() {
	log.info("ProviderServices getAllLazy");
	Iterable<Provider> providers = providerRepository.findByActive(true);
	for (Provider provider : providers) {
            provider.setListsNull();
            provider.setFatherListToNull();
	}
	log.info("ProviderServices getAllLazy size retrieved: {}", Iterables.size(providers));
	return providers;
    }

    public Iterable<Provider> getLazyByUserClient(UUID id) {
	log.info("ProviderServices getLazyByUserClient");
	Iterable<Provider> providers = providerRepository.findProviderByUserClientId(id);
	for (Provider provider : providers) {
            provider.setListsNull();
            provider.setFatherListToNull();
	}
	log.info("ProviderServices getLazyByUserClient size retrieved: {}", Iterables.size(providers));
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
