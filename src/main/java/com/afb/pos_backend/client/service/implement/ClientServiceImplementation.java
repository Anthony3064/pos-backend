package com.afb.pos_backend.client.service.implement;

import com.afb.pos_backend.client.dto.ClientDTO;
import com.afb.pos_backend.client.persistence.entity.Client;
import com.afb.pos_backend.client.persistence.repository.ClientRepository;
import com.afb.pos_backend.client.service.ClientService;
import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.DuplicateResourceException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
@Log4j2
public class ClientServiceImplementation implements ClientService {

    private final ClientRepository repository;
    private final ModelMapper mapper;

    @Override
    public ClientDTO createClient(ClientDTO client) {
        if (client.getEmail() != null && !client.getEmail().isBlank() && repository.existsByEmailAndActiveTrue(client.getEmail())) {
            throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_RESOURCE_ERROR, "correo electrónico"));
        }
        Client clientEntity = repository.save(mapper.map(client, Client.class));
        return mapper.map(clientEntity, ClientDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> getAllClients(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientsEntities = repository.findAll(pageable);
        return clientsEntities.map(clientEntity -> mapper.map(clientEntity, ClientDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> getAllClientsOfUser(int page, int size, String uid, String name) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "uid"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientsEntities = repository.findByCreatedByAndActiveTrueAndCompleteNameContainingIgnoreCaseOrderByCompleteName(pageable, uid, name);
        return clientsEntities.map(clientEntity -> mapper.map(clientEntity, ClientDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> getAllClientsOfUser(int page, int size, String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "uid"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientsEntities = repository.findByCreatedByAndActiveTrueOrderByCompleteName(pageable, uid);
        return clientsEntities.map(clientEntity -> mapper.map(clientEntity, ClientDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClient(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "id"));
        }
        Client clientEntity = repository.findById(uid).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "cliente")));
        return mapper.map(clientEntity, ClientDTO.class);
    }

    @Override
    public ClientDTO updateClient(ClientDTO client) {
        if (client.getId() == null || client.getId().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "uid"));
        }
        boolean clientExists = repository.existsById(client.getId());
        if (!clientExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "cliente"));
        }
        if (client.getActive() == null) {
            client.setActive(true);
        }

        if (client.getEmail() != null
                && !client.getEmail().isEmpty()
                && !client.getEmail().isBlank()) {
            String databaseEmail = repository.findById(client.getId()).map(Client::getEmail).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "cliente")));
            if (!databaseEmail.equals(client.getEmail())) {
                if (repository.existsByEmailAndActiveTrue(client.getEmail())) {
                    throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_RESOURCE_ERROR, "correo electrónico"));
                }
            }
        }


        Client clientEntity = repository.save(mapper.map(client, Client.class));
        return mapper.map(clientEntity, ClientDTO.class);
    }

    @Override
    public String deleteClient(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "uid"));
        }
        boolean clientExists = repository.existsById(uid);
        if (!clientExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "cliente"));
        }
        int rowsAffected = repository.changeActive(uid, false);
        log.info("Se ha actualizado {}.", rowsAffected);
        return String.format("El cliente con el id: %s ha sido borrado", uid);
    }
}
