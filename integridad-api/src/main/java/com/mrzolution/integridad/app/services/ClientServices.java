package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.ClientRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientServices {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    BillRepository billRepository;
    
    public Iterable<Client> getClientByUserClientAndIdentification(UUID userClientId, String identification) {
	Iterable<Client> clients = clientRepository.findClientByUserClientAndIdentification(userClientId, identification);
	clients.forEach(client -> {
            client.setFatherListToNull();
            client.setListsNull();
        });
	return clients;
    }
	
    public Client createClient(Client client) {
        if (client.getCodApp() == null) {
            throw new BadRequestException("Debe tener el codigo de contabilidad");
        }
        
        client.setDateCreated(new Date().getTime());
        client.setActive(true);
        Client saved = clientRepository.save(client);
        log.info("ClientServices createClient DONE id: {}", saved.getId());
        return saved;
    }
	
    public void updateClient(Client client) throws BadRequestException {
        if (client.getId() == null) {
            throw new BadRequestException("Invalid Client");
        }
        log.info("ClientServices updateClient: {}", client.getName());
        client.setListsNull();
        Client updated = clientRepository.save(client);
        log.info("ClientServices updateClient DONE id: {}", updated.getId());
    }
	
    public Client getClientById(UUID id) {
        log.info("ClientServices getClientById: {}", id);
        Client retrieved = clientRepository.findOne(id);
        if (retrieved != null) {
            log.info("ClientServices retrieved id: {}", retrieved.getId());
        } else {
            log.info("ClientServices retrieved id NULL: {}", id);
        }	
        populateChildren(retrieved);
        return retrieved;
    }
	
    public Iterable<Client> getAllClient() {
        log.info("ClientServices getAllClient");
        Iterable<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            populateChildren(client);
        }
        log.info("ClientServices getAllClient size retrieved: {}", Iterables.size(clients));
        return clients;
    }
	
    public Iterable<Client> getAllClientActives() {
        log.info("ClientServices getAllClientActives");
        Iterable<Client> clients = clientRepository.findByActive(true);
        for (Client client : clients) {
            client.setListsNull();
            client.setFatherListToNull();
        }
        log.info("ClientServices getAllClientActives size retrieved: {}", Iterables.size(clients));
        return clients;
    }

    public Iterable<Client> getAllLazyByUserClientid(UUID userClientId) {
        log.info("ClientServices getAllLazyByUserClientid id: {}", userClientId);
        Iterable<Client> clients = clientRepository.findActivesByUserClientId(userClientId);
        for (Client client : clients) {
            client.setListsNull();
            client.setFatherListToNull();
        }
        log.info("ClientServices getAllLazyByUserClientid size retrieved: {}", Iterables.size(clients));
        return clients;
    }
	
    private void populateChildren(Client client) {
        List<Bill> billList = new ArrayList<>();
        Iterable<Bill> bills= billRepository.findByClient(client);	
        for (Bill bill : bills) {
            bill.setFatherListToNull();
            bill.setListsNull();
            bill.setClient(null);		
            billList.add(bill);
        }
        client.setBills(billList);
    }

}
