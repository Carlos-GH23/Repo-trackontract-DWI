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
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;

// Imports para PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;


@Transactional
@Service
public class ContractsService {
    private static final Logger logger = LoggerFactory.getLogger(ContractsService.class);
    private final ContractsRepository contractsRepository;
    private final ClientsRepository clientsRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;


    @Autowired
    public ContractsService(ContractsRepository contractsRepository, ClientsRepository clientsRepository, CategoriesRepository categoriesRepository, UserRepository userRepository) {
        this.contractsRepository = contractsRepository;
        this.clientsRepository = clientsRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
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
                contract.put("approvalStatus", row[5]);
                contract.put("approvedAt", row[6]);
                contract.put("rejectionReason", row[7]);
                contract.put("client_id", java.util.Map.of("id", row[8], "name", row[9]));
                contract.put("category_id", java.util.Map.of("id", row[10], "name", row[11]));
                contract.put("abogado_id", java.util.Map.of("id", row[12], "name", row[13], "lastName", row[14]));
                
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Listado de contratos obtenido correctamente");
        return new ResponseEntity<>(new Message(contracts, "Listado de contratos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar contratos por abogado específico
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByAbogado(Long abogadoId) {
        List<Object[]> contractsData = contractsRepository.findAllContractsByAbogado(abogadoId);
        logger.info("Buscando contratos para el abogado ID: {}", abogadoId);
        
        if (contractsData.isEmpty()) {
            return new ResponseEntity<>(new Message(contractsData, "No hay contratos asignados a este abogado", TypesResponse.WARNING), HttpStatus.OK);
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
                contract.put("approvalStatus", row[5]);
                contract.put("approvedAt", row[6]);
                contract.put("rejectionReason", row[7]);
                contract.put("client_id", java.util.Map.of("id", row[8], "name", row[9]));
                contract.put("category_id", java.util.Map.of("id", row[10], "name", row[11]));
                contract.put("abogado_id", java.util.Map.of("id", row[12], "name", row[13], "lastName", row[14]));
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Contratos del abogado obtenidos correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contratos del abogado encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar contratos por cliente específico
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByClient(Long clientId) {
        List<Object[]> contractsData = contractsRepository.findAllContractsByClient(clientId);
        logger.info("Buscando contratos para el cliente ID: {}", clientId);
        logger.info("Contratos encontrados en BD: {}", contractsData.size());
        
        if (contractsData.isEmpty()) {
            logger.info("No hay contratos para el cliente ID: {}", clientId);
            return new ResponseEntity<>(new Message(contractsData, "No hay contratos para este cliente", TypesResponse.WARNING), HttpStatus.OK);
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
                contract.put("approvalStatus", row[5]);
                contract.put("approvedAt", row[6]);
                contract.put("rejectionReason", row[7]);
                contract.put("client_id", java.util.Map.of("id", row[8], "name", row[9]));
                contract.put("category_id", java.util.Map.of("id", row[10], "name", row[11]));
                contract.put("abogado_id", java.util.Map.of("id", row[12], "name", row[13], "lastName", row[14]));
                
                logger.info("Contrato procesado - ID: {}, Name: {}, ApprovalStatus: {}", 
                    row[0], row[1], row[5]);
                
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Contratos del cliente procesados: {}", contracts.size());
        logger.info("Contratos del cliente obtenidos correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contratos del cliente encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar contratos por email del usuario (nuevo método)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByUserEmail(String userEmail) {
        logger.info("Buscando contratos para el usuario con email: {}", userEmail);
        
        // Primero buscar el cliente por email
        Optional<Clients> client = clientsRepository.findByEmail(userEmail);
        if (!client.isPresent()) {
            logger.info("No se encontró cliente con email: {}", userEmail);
            return new ResponseEntity<>(new Message(List.of(), "No se encontró cliente con este email", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        Long clientId = client.get().getId();
        logger.info("Cliente encontrado - ID: {}, Nombre: {}, Email: {}", clientId, client.get().getName(), userEmail);
        
        // Ahora buscar contratos por el ID del cliente
        List<Object[]> contractsData = contractsRepository.findAllContractsByClient(clientId);
        logger.info("Contratos encontrados en BD para cliente ID {}: {}", clientId, contractsData.size());
        
        if (contractsData.isEmpty()) {
            logger.info("No hay contratos para el cliente con email: {}", userEmail);
            return new ResponseEntity<>(new Message(contractsData, "No hay contratos para este cliente", TypesResponse.WARNING), HttpStatus.OK);
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
                contract.put("approvalStatus", row[5]);
                contract.put("approvedAt", row[6]);
                contract.put("rejectionReason", row[7]);
                contract.put("client_id", java.util.Map.of("id", row[8], "name", row[9]));
                contract.put("category_id", java.util.Map.of("id", row[10], "name", row[11]));
                contract.put("abogado_id", java.util.Map.of("id", row[12], "name", row[13], "lastName", row[14]));
                
                return contract;
            })
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Contratos del usuario procesados: {}", contracts.size());
        logger.info("Contratos del usuario obtenidos correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contratos del usuario encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
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
        if (dto.getAbogadoDTO() == null || dto.getAbogadoDTO().getId() == null || dto.getAbogadoDTO().getId() <= 0) {
            return new ResponseEntity<>(new Message("El abogado del contrato no puede ser nulo o no existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        Clients clients = clientsRepository.findById(dto.getClientsDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Categories categories = categoriesRepository.findById(dto.getCategoriesDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        User abogado = userRepository.findById(dto.getAbogadoDTO().getId())
                    .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));
        
        logger.info("Cliente encontrado - ID: {}, Nombre: {}", clients.getId(), clients.getName());
        logger.info("Categoría encontrada - ID: {}, Nombre: {}", categories.getId(), categories.getName());
        logger.info("Abogado encontrado - ID: {}, Nombre: {}", abogado.getId(), abogado.getName());
        
        Contracts contracts = new Contracts(dto.getName(), dto.getDescription(), dto.getDue_date(), true, clients, categories);
        contracts.setStatus(true);
        contracts.setAbogado_id(abogado);
        contracts.setApprovalStatus(Contracts.ApprovalStatus.PENDIENTE);
        
        logger.info("Contrato creado en memoria - Cliente ID: {}, Abogado ID: {}", 
            contracts.getClient_id().getId(), contracts.getAbogado_id().getId());
        
        contracts = contractsRepository.saveAndFlush(contracts);
        
        logger.info("Contrato guardado en BD - ID: {}, Cliente ID: {}, Abogado ID: {}", 
            contracts.getId(), contracts.getClient_id().getId(), contracts.getAbogado_id().getId());
        
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
        
        // Mantener el approvalStatus existente si no se proporciona uno nuevo
        if (dto.getApprovalStatus() != null) {
            contracts.setApprovalStatus(Contracts.ApprovalStatus.valueOf(dto.getApprovalStatus()));
        }
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

        Optional<User> abogadoOptional = userRepository.findById(dto.getAbogadoDTO().getId());
        if (!abogadoOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Abogado no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        User abogado = abogadoOptional.get();

        contracts.setClient_id(clients);
        contracts.setCategory_id(categories);
        contracts.setAbogado_id(abogado);
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

    // Buscar contrato por ID con validación de abogado
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByIdWithAbogadoValidation(Long id, Long abogadoId) {
        Optional<Contracts> contractsOptional = contractsRepository.findById(id);
        if (!contractsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        
        Contracts contract = contractsOptional.get();
        
        // Validar que el abogado autenticado sea el asignado al contrato
        if (contract.getAbogado_id() == null || !contract.getAbogado_id().getId().equals(abogadoId)) {
            return new ResponseEntity<>(new Message("No tienes permisos para acceder a este contrato", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
        }
        
        logger.info("Contrato encontrado y validado para el abogado ID: {}", abogadoId);
        return new ResponseEntity<>(new Message(contract, "Contrato encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
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
                contract.put("abogado_id", java.util.Map.of("id", row[9], "name", row[10], "lastName", row[11]));
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

        // Aceptar contrato
        @Transactional(rollbackFor = {SQLException.class})
        public ResponseEntity<Message> acceptContract(Long contractId, Long abogadoId) {
            Optional<Contracts> contractsOptional = contractsRepository.findById(contractId);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            Contracts contract = contractsOptional.get();
            
            // Validar que el abogado autenticado sea el asignado al contrato
            if (contract.getAbogado_id() == null || !contract.getAbogado_id().getId().equals(abogadoId)) {
                return new ResponseEntity<>(new Message("No tienes permisos para aceptar este contrato", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
            }
            
            // Validar que el contrato esté pendiente
            if (contract.getApprovalStatus() != null && contract.getApprovalStatus() != Contracts.ApprovalStatus.PENDIENTE) {
                return new ResponseEntity<>(new Message("El contrato ya no está pendiente de aprobación", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            
            // Si no tiene approvalStatus, establecerlo como PENDIENTE primero
            if (contract.getApprovalStatus() == null) {
                contract.setApprovalStatus(Contracts.ApprovalStatus.PENDIENTE);
            }
            
            // Cambiar estado a ACEPTADO
            contract.setApprovalStatus(Contracts.ApprovalStatus.ACEPTADO);
            contract.setApprovedAt(LocalDateTime.now());
            
            contract = contractsRepository.saveAndFlush(contract);
            if (contract == null) {
                return new ResponseEntity<>(new Message("El contrato no se pudo aceptar", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            
            logger.info("Contrato {} aceptado por el abogado {}", contractId, abogadoId);
            return new ResponseEntity<>(new Message(contract, "Contrato aceptado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        }

        // Rechazar contrato
        @Transactional(rollbackFor = {SQLException.class})
        public ResponseEntity<Message> rejectContract(Long contractId, Long abogadoId, String rejectionReason) {
            Optional<Contracts> contractsOptional = contractsRepository.findById(contractId);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            Contracts contract = contractsOptional.get();
            
            // Validar que el abogado autenticado sea el asignado al contrato
            if (contract.getAbogado_id() == null || !contract.getAbogado_id().getId().equals(abogadoId)) {
                return new ResponseEntity<>(new Message("No tienes permisos para rechazar este contrato", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
            }
            
            // Validar que el contrato esté pendiente
            if (contract.getApprovalStatus() != null && contract.getApprovalStatus() != Contracts.ApprovalStatus.PENDIENTE) {
                return new ResponseEntity<>(new Message("El contrato ya no está pendiente de aprobación", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            
            // Si no tiene approvalStatus, establecerlo como PENDIENTE primero
            if (contract.getApprovalStatus() == null) {
                contract.setApprovalStatus(Contracts.ApprovalStatus.PENDIENTE);
            }
            
            // Validar que se proporcione un motivo de rechazo
            if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
                return new ResponseEntity<>(new Message("Debe proporcionar un motivo para rechazar el contrato", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            
            // Cambiar estado a RECHAZADO
            contract.setApprovalStatus(Contracts.ApprovalStatus.RECHAZADO);
            contract.setRejectionReason(rejectionReason.trim());
            
            contract = contractsRepository.saveAndFlush(contract);
            if (contract == null) {
                return new ResponseEntity<>(new Message("El contrato no se pudo rechazar", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            
            logger.info("Contrato {} rechazado por el abogado {} con motivo: {}", contractId, abogadoId, rejectionReason);
            return new ResponseEntity<>(new Message(contract, "Contrato rechazado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        }

    // Generar PDF del contrato
    public ResponseEntity<ByteArrayResource> generateContractPDF(Long contractId) {
        try {
            // Buscar el contrato
            Optional<Contracts> contractOptional = contractsRepository.findById(contractId);
            if (!contractOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Contracts contract = contractOptional.get();
            
            // Crear el PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Título del documento
            Paragraph title = new Paragraph("CONTRATO DE SERVICIOS");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Información del contrato
            document.add(new Paragraph(" ")); // Espacio
            
            // Tabla de información del contrato
            PdfPTable contractInfo = new PdfPTable(2);
            contractInfo.setWidthPercentage(100);
            
            contractInfo.addCell(new PdfPCell(new Paragraph("Nombre del Contrato:")));
            contractInfo.addCell(new PdfPCell(new Paragraph(contract.getName())));
            
            contractInfo.addCell(new PdfPCell(new Paragraph("Descripción:")));
            contractInfo.addCell(new PdfPCell(new Paragraph(contract.getDescription() != null ? contract.getDescription() : "Sin descripción")));
            
            contractInfo.addCell(new PdfPCell(new Paragraph("Fecha de Vencimiento:")));
            contractInfo.addCell(new PdfPCell(new Paragraph(contract.getDue_date() != null ? 
                contract.getDue_date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                "No especificada")));
            
            contractInfo.addCell(new PdfPCell(new Paragraph("Estado de Aprobación:")));
            contractInfo.addCell(new PdfPCell(new Paragraph(contract.getApprovalStatus() != null ? 
                contract.getApprovalStatus().toString() : "PENDIENTE")));
            
            if (contract.getApprovalStatus() == Contracts.ApprovalStatus.ACEPTADO && contract.getApprovedAt() != null) {
                contractInfo.addCell(new PdfPCell(new Paragraph("Fecha de Aprobación:")));
                contractInfo.addCell(new PdfPCell(new Paragraph(contract.getApprovedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))));
            }
            
            document.add(contractInfo);
            
            // Información del cliente
            document.add(new Paragraph(" "));
            Paragraph clientTitle = new Paragraph("INFORMACIÓN DEL CLIENTE");
            clientTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(clientTitle);
            
            PdfPTable clientInfo = new PdfPTable(2);
            clientInfo.setWidthPercentage(100);
            
            if (contract.getClient_id() != null) {
                clientInfo.addCell(new PdfPCell(new Paragraph("Nombre del Cliente:")));
                clientInfo.addCell(new PdfPCell(new Paragraph(contract.getClient_id().getName())));
                
                clientInfo.addCell(new PdfPCell(new Paragraph("Email:")));
                clientInfo.addCell(new PdfPCell(new Paragraph(contract.getClient_id().getEmail())));
                
                if (contract.getClient_id().getPhone() != null) {
                    clientInfo.addCell(new PdfPCell(new Paragraph("Teléfono:")));
                    clientInfo.addCell(new PdfPCell(new Paragraph(contract.getClient_id().getPhone())));
                }
            }
            
            document.add(clientInfo);
            
            // Información del abogado
            document.add(new Paragraph(" "));
            Paragraph lawyerTitle = new Paragraph("ABOGADO ASIGNADO");
            lawyerTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(lawyerTitle);
            
            PdfPTable lawyerInfo = new PdfPTable(2);
            lawyerInfo.setWidthPercentage(100);
            
            if (contract.getAbogado_id() != null) {
                lawyerInfo.addCell(new PdfPCell(new Paragraph("Nombre:")));
                lawyerInfo.addCell(new PdfPCell(new Paragraph(contract.getAbogado_id().getName() + " " + contract.getAbogado_id().getLastName())));
                
                lawyerInfo.addCell(new PdfPCell(new Paragraph("Email:")));
                lawyerInfo.addCell(new PdfPCell(new Paragraph(contract.getAbogado_id().getEmail())));
            }
            
            document.add(lawyerInfo);
            
            // Información de la categoría
            document.add(new Paragraph(" "));
            Paragraph categoryTitle = new Paragraph("CATEGORÍA DEL SERVICIO");
            categoryTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(categoryTitle);
            
            PdfPTable categoryInfo = new PdfPTable(2);
            categoryInfo.setWidthPercentage(100);
            
            if (contract.getCategory_id() != null) {
                categoryInfo.addCell(new PdfPCell(new Paragraph("Categoría:")));
                categoryInfo.addCell(new PdfPCell(new Paragraph(contract.getCategory_id().getName())));
                
                if (contract.getCategory_id().getDescription() != null) {
                    categoryInfo.addCell(new PdfPCell(new Paragraph("Descripción:")));
                    categoryInfo.addCell(new PdfPCell(new Paragraph(contract.getCategory_id().getDescription())));
                }
            }
            
            document.add(categoryInfo);
            
            // Pie de página
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Documento generado el: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            // Preparar la respuesta
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrato_" + contractId + ".pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Error generando PDF del contrato {}: {}", contractId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    }

