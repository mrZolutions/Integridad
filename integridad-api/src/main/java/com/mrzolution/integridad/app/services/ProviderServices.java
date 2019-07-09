package com.mrzolution.integridad.app.services;

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
        log.info("ProviderServices getProviderByUserClientIdAndRuc: {}, {}", userClientId, ruc);
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
	log.info("ProviderServices createProvider: {}", saved.getId());
	return saved;
    }

    public void updateProvider(Provider provider) throws BadRequestException {
	if (provider.getId() == null) {
		throw new BadRequestException("Invalid Provider");
	}
	log.info("ProviderServices update: {}", provider.getName());
	provider.setListsNull();
	Provider updated = providerRepository.save(provider);
	log.info("ProviderServices updateProvider: {}", updated.getId());
    }

    public Provider getProviderById(UUID id) {
	Provider retrieved = providerRepository.findOne(id);
	if (retrieved != null) {
            log.info("ProviderServices retrieved id: {}", retrieved.getId());
	} else {
            log.info("ProviderServices retrieved id NULL: {}", id);
	}
        log.info("ProviderServices getProviderById: {}", id);
	return retrieved;
    }

    public Iterable<Provider> getAllLazy() {
	Iterable<Provider> providers = providerRepository.findByActive(true);
	for (Provider provider : providers) {
            provider.setListsNull();
            provider.setFatherListToNull();
	}
	log.info("ProviderServices getAllLazy");
        return providers;
    }

    public Iterable<Provider> getLazyByUserClient(UUID id) {
	Iterable<Provider> providers = providerRepository.findProviderByUserClientId(id);
	for (Provider provider : providers) {
            provider.setListsNull();
            provider.setFatherListToNull();
	}
	log.info("ProviderServices getLazyByUserClient: {}", id);
	return providers;
    }
    
}