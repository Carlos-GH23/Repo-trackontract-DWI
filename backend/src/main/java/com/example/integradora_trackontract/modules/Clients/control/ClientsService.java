package com.example.integradora_trackontract.modules.Clients.control;

import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.modules.Clients.model.ClientsRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ClientsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientsService.class);
    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository) {
        this.clientsRepository= clientsRepository;
    }

    //Busqueda de clientes inactivos
    @Transactional(readOnly = true)
    public List<Clients> findAllByStatusIsFalse(Boolean status) {
        logger.info("Buscando clientes con estado inactivo");
        return clientsRepository.findAllByStatusIsFalse();
    }

    //Busqueda de clientes
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Clients> clients = clientsRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        if(clients.isEmpty()) {
            return new ResponseEntity<>(new Message(clients, "No hay clientes registradas", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Listado de clientes obtenido correctamente");

        return new ResponseEntity<>(new Message(clients,"Listado de clientes", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ClientsDTO dto) {
        Optional<Clients> existingClients = clientsRepository.findByName(dto.getName());
        if(existingClients.isPresent()) {
            return new ResponseEntity<>(new Message("El cliente ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName().length() > 50){
            return new ResponseEntity<>(new Message("El nombre del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getBusiness_name().length() > 50){
            return new ResponseEntity<>(new Message("El nombre del negocio del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getRepresentative_name().length() > 100){
            return new ResponseEntity<>(new Message("El nombre del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getRepresentative_surnames().length() >100){
            return new ResponseEntity<>(new Message("Los apellidos del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getEmail().length() >100){
            return new ResponseEntity<>(new Message("El email del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getPhone().length() > 15){
            return new ResponseEntity<>(new Message("El teléfono del cliente excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getStatus() == null) {
            return new ResponseEntity<>(new Message("El estado del cliente no puede ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Clients clients = new Clients(dto.getName(), dto.getBusiness_name(), dto.getRepresentative_name(),
                dto.getRepresentative_surnames(), dto.getEmail(), dto.getPhone(), true);
        clients.setStatus(true);
        clients = clientsRepository.saveAndFlush(clients);
        ClientsDTO saveDTO = new ClientsDTO(
                clients.getId(),
                clients.getName(),
                clients.getBusiness_name(),
                clients.getRepresentative_name(),
                clients.getRepresentative_surnames(),
                clients.getEmail(),
                clients.getPhone(),
                clients.isStatus()
        );
        if(clients == null) {
            return new ResponseEntity<>(new Message("El cliente no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("El registro ha sido realizado correctamente");
        return new ResponseEntity<>(new Message(saveDTO,"El cliente se registró correctamente", TypesResponse.SUCCESS), HttpStatus.CREATED);
    }

    //Actualizar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ClientsDTO dto) {
        Optional<Clients> clientsOptional = clientsRepository.findById(dto.getId());
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getBusiness_name().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del negocio del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_name().length() > 100) {
            return new ResponseEntity<>(new Message("El nombre del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_surnames().length() > 100) {
            return new ResponseEntity<>(new Message("Los apellidos del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El email del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhone().length() > 15) {
            return new ResponseEntity<>(new Message("El teléfono del cliente excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getStatus() == null) {
            return new ResponseEntity<>(new Message("El estado del cliente no puede ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Clients clients = clientsOptional.get();
        clients.setName(dto.getName());
        clients.setBusiness_name(dto.getBusiness_name());
        clients.setRepresentative_name(dto.getRepresentative_name());
        clients.setRepresentative_surnames(dto.getRepresentative_surnames());
        clients.setEmail(dto.getEmail());
        clients.setPhone(dto.getPhone());
        clients.setStatus(dto.getStatus());
        clients = clientsRepository.saveAndFlush(clients);
        if (clients == null) {
            return new ResponseEntity<>(new Message("El cliente no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Cliente actualizado correctamente");
        return new ResponseEntity<>(new Message(clients, "Cliente actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Desactivar/Activar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ClientsDTO dto) {
        Optional<Clients> clientsOptional = clientsRepository.findById(dto.getId());
        if(!clientsOptional.isPresent()){
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Clients clients = clientsOptional.get();
        clients.setStatus(!clients.isStatus());
        clients = clientsRepository.saveAndFlush(clients);
        if(clients == null){
            return new ResponseEntity<>(new Message("El status del cliente no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Cliente actualizado correctamente");
        return new ResponseEntity<>(new Message(clients, "El status del cliente fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Eliminar Cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> delete(Long id) {
        Optional<Clients> clientsOptional = clientsRepository.findById(id);
        if(!clientsOptional.isPresent()){
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Clients clients = clientsOptional.get();
        if (clients.getContracts() != null && !clients.getContracts().isEmpty()) {
            return new ResponseEntity<>(new Message("El cliente no se puede eliminar porque tiene contratos asociados", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        clientsRepository.delete(clients);
        logger.info("Cliente eliminado correctamente");
        return new ResponseEntity<>(new Message("Cliente eliminado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Clientes por nombre (PENDIENTE DE REVISION)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre no debe ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Clients> clientsOptional = clientsRepository.findByName(name);
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de cliente por nombre realizada correctamente");
        return new ResponseEntity<>(new Message(clientsOptional.get(), "Cliente encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Cliente por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById(Long id) {
        Optional<Clients> clientsOptional = clientsRepository.findById(id);
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de cliente por ID realizada correctamente");
        return new ResponseEntity<>(new Message(clientsOptional.get(), "Cliente encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Busqueda de clientes activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue() {
        List<Clients> clients = clientsRepository.findAllByStatusIsTrue();
        if (clients.isEmpty()) {
            return new ResponseEntity<>(new Message(clients, "No hay clientes activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Busqueda de clientes activos realizada correctamente");
        return new ResponseEntity<>(new Message(clients, "Clientes activos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Scheduled para revisar cuantas clientes estan activas e inactivas
    @Scheduled(cron = "0 0 0 * * ?")
    public void countClientsStatus() {
        int activeCount = clientsRepository.countByStatusIsTrue();
        int inactiveCount = clientsRepository.countByStatusIsFalse();
        logger.info("Clientes activos: {}, Clientes inactivos: {}", activeCount, inactiveCount);
    }

}
