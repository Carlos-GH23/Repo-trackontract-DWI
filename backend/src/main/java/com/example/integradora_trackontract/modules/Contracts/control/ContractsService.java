package com.example.integradora_trackontract.modules.Contracts.control;

import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesDTO;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesRepository;
import com.example.integradora_trackontract.modules.Clients.control.ClientsService;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.modules.Clients.model.ClientsRepository;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsDTO;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsRepository;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ContractsService {
    private static final Logger logger = LoggerFactory.getLogger(ContractsService.class);
    private final ContractsRepository contractsRepository;
    private final ClientsRepository clientsRepository;
    private final CategoriesRepository categoriesRepository;


    @Autowired
    public ContractsService(ContractsRepository contractsRepository, ClientsRepository clientsRepository, CategoriesRepository categoriesRepository) {
        this.contractsRepository = contractsRepository;
        this.clientsRepository = clientsRepository;
        this.categoriesRepository = categoriesRepository;

    }

    //Busqueda de contractos inactivos
    @Transactional(readOnly = true)
    public List<Contracts> findAllByStatusIsFalse(Boolean status) {
        logger.info("Buscando contratos con estado inactivo");
        return contractsRepository.findAllByStatusIsFalse();
    }

    //Busqueda de contratos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Object[]> contractsData = contractsRepository.findAllContractsWithBasicInfo();
        logger.info("La búsqueda ha sido realizada correctamente");
        
        if (contractsData.isEmpty()) {
            return new ResponseEntity<>(new Message(contractsData, "No hay contratos registrados", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        // Convertir Object[] a Map para evitar referencias circulares
        List<java.util.Map<String, Object>> contracts = contractsData.stream()
            .map(row -> {
                java.util.Map<String, Object> contract = new java.util.HashMap<>();
                contract.put("id", row[0]);
                contract.put("name", row[1]);
                contract.put("description", row[2]);
                contract.put("due_date", row[3]);
                contract.put("status", row[4]);
                contract.put("client_id", java.util.Map.of("id", row[5], "name", row[6]));
                contract.put("category_id", java.util.Map.of("id", row[7], "name", row[8]));
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Listado de contratos obtenido correctamente");
        return new ResponseEntity<>(new Message(contracts, "Listado de contratos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Contratos
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ContractsDTO dto) {
        Optional<Contracts> existingContracts = contractsRepository.findByName(dto.getName());
        if (existingContracts.isPresent()) {
            return new ResponseEntity<>(new Message("El contrato ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del contrato no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del contrato excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            return new ResponseEntity<>(new Message("La descripcion del contrato excede los 255 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getDue_date() == null || dto.getDue_date().before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
            return new ResponseEntity<>(new Message("La fecha de vencimiento del contrato no puede ser nula o anterior a la fecha actual", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getClientsDTO() == null || dto.getClientsDTO().getId() == null || dto.getClientsDTO().getId() <= 0) {
            return new ResponseEntity<>(new Message("El cliente del contrato no puede ser nulo o no existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getCategoriesDTO() == null || dto.getCategoriesDTO().getId() == null || dto.getCategoriesDTO().getId() <= 0) {
            return new ResponseEntity<>(new Message("La categoría del contrato no puede ser nula o no existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        Clients clients = clientsRepository.findById(dto.getClientsDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            Categories categories = categoriesRepository.findById(dto.getCategoriesDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            Contracts contracts = new Contracts(dto.getName(), dto.getDescription(), dto.getDue_date(), true, clients, categories);
            contracts.setStatus(true);
            contracts = contractsRepository.saveAndFlush(contracts);
            if (contracts == null) {
                return new ResponseEntity<>(new Message("El contrato no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            logger.info("El registro ha sido realizado correctamente");
            return new ResponseEntity<>(new Message(contracts, "El contrato se registró correctamente", TypesResponse.SUCCESS), HttpStatus.CREATED);
        }

        //Actualizar Contratos

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ContractsDTO dto) {
        Optional<Contracts> contractsOptional = contractsRepository.findById(dto.getId());
        if (!contractsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del contrato no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del contrato excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            return new ResponseEntity<>(new Message("La descripcion del contrato excede los 255 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if (dto.getDue_date() == null || dto.getDue_date().before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
            return new ResponseEntity<>(new Message("La fecha de vencimiento del contrato no puede ser nula o anterior a la fecha actual", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if (dto.getClientsDTO() == null || dto.getClientsDTO().getId() == null || dto.getClientsDTO().getId() <= 0) {
            return new ResponseEntity<>(new Message("El cliente del contrato no puede ser nulo o no existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if (dto.getCategoriesDTO() == null || dto.getCategoriesDTO().getId() == null || dto.getCategoriesDTO().getId() <= 0) {
            return new ResponseEntity<>(new Message("La categoría del contrato no puede ser nula o no existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Contracts contracts = contractsOptional.get();
        contracts.setName(dto.getName());
        contracts.setDescription(dto.getDescription());
        contracts.setDue_date(dto.getDue_date());
        contracts.setStatus(dto.getStatus());
        Optional<Clients> clientsOptional = clientsRepository.findById(dto.getClientsDTO().getId());
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Clients clients = clientsOptional.get();

        Optional<Categories> categoriesOptional = categoriesRepository.findById(dto.getCategoriesDTO().getId());
        if (!categoriesOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Categoría no encontrada", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Categories categories = categoriesOptional.get();

        contracts.setClient_id(clients);
        contracts.setCategory_id(categories);
        contracts = contractsRepository.saveAndFlush(contracts);
        if (contracts == null) {
            return new ResponseEntity<>(new Message("El contrato no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Contrato actualizado correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contrato actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

        //Desactivar/Activar Contratos
        @Transactional(rollbackFor = {SQLException.class})
        public ResponseEntity<Message> changeStatus (ContractsDTO dto){
            Optional<Contracts> contractsOptional = contractsRepository.findById(dto.getId());
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            Contracts contracts = contractsOptional.get();
            contracts.setStatus(!contracts.isStatus());
            contracts = contractsRepository.saveAndFlush(contracts);
            if (contracts == null) {
                return new ResponseEntity<>(new Message("El status del contrato no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            logger.info("Contrato actualizado correctamente");
            return new ResponseEntity<>(new Message(contracts, "El status del contrato fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        }

        // Eliminar Contratos
        @Transactional(rollbackFor = {SQLException.class})
        public ResponseEntity<Message> delete (Long id){
            Optional<Contracts> contractsOptional = contractsRepository.findById(id);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            Contracts contracts = contractsOptional.get();
            if (contracts.isStatus()) {
                return new ResponseEntity<>(new Message("No se puede eliminar un contrato activo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            contractsRepository.delete(contracts);
            logger.info("Contrato eliminado correctamente");
            return new ResponseEntity<>(new Message("Contrato eliminado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
        }


        //Busqueda de Contrrato por nombre (PENDIENTE DE REVISION)
        @Transactional(readOnly = true)
        public ResponseEntity<Message> findByName (String name){
            if (name == null || name.isEmpty()) {
                return new ResponseEntity<>(new Message("El nombre no debe ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            Optional<Contracts> contractsOptional = contractsRepository.findByName(name);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
            }
            logger.info("Busqueda de contrato por nombre realizada correctamente");
            return new ResponseEntity<>(new Message(contractsOptional.get(), "Contrato encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
        }


        //Busqueda de Cliente por ID
        @Transactional(readOnly = true)
        public ResponseEntity<Message> findById (Long id){
            Optional<Contracts> contractsOptional = contractsRepository.findById(id);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            logger.info("Busqueda de contrato por ID realizada correctamente");
            return new ResponseEntity<>(new Message(contractsOptional.get(), "Contrato encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
        }

    //Busqueda de contratos activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue () {
        List<Object[]> contractsData = contractsRepository.findAllContractsByStatusWithBasicInfo(true);
        if (contractsData.isEmpty()) {
            return new ResponseEntity<>(new Message(contractsData, "No hay contratos activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        // Convertir Object[] a Map para evitar referencias circulares
        List<java.util.Map<String, Object>> contracts = contractsData.stream()
            .map(row -> {
                java.util.Map<String, Object> contract = new java.util.HashMap<>();
                contract.put("id", row[0]);
                contract.put("name", row[1]);
                contract.put("description", row[2]);
                contract.put("due_date", row[3]);
                contract.put("status", row[4]);
                contract.put("client_id", java.util.Map.of("id", row[5], "name", row[6]));
                contract.put("category_id", java.util.Map.of("id", row[7], "name", row[8]));
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Busqueda de contratos activos realizada correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contratos activos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

        //Scheduled para revisar cuantas contratos estan activas e inactivas
        @Scheduled(cron = "0 0 0 * * ?") // Ejecutar diariamente a medianoche
        public void reportContractStatus () {
            long activeCount = contractsRepository.countByStatusIsTrue();
            long inactiveCount = contractsRepository.countByStatusIsFalse();
            logger.info("Reporte de estado de contratos a las {}: Contratos activos: {}, Contratos inactivos: {}", LocalDateTime.now(), activeCount, inactiveCount);
        }

    }

