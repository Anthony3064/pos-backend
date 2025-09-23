package com.afb.pos_backend.client.service;

import com.afb.pos_backend.client.dto.ClientDTO;
import org.springframework.data.domain.Page;

public interface ClientService {
    ClientDTO createClient(ClientDTO client);

    Page<ClientDTO> getAllClients(int page, int size);

    Page<ClientDTO> getAllClientsOfUser(int page, int size, String uid);

    Page<ClientDTO> getAllClientsOfUser(int page, int size, String uid, String name);

    ClientDTO getClient(String uid);

    ClientDTO updateClient(ClientDTO client);

    String deleteClient(String uid);
}
