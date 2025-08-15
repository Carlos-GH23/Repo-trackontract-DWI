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
import com.example.integradora_trackontract.config.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import java.io.ByteArrayOutputStream;


@Transactional
@Service
public class ContractsService {
    private static final Logger logger = LoggerFactory.getLogger(ContractsService.class);
    private final ContractsRepository contractsRepository;
    private final ClientsRepository clientsRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    @Autowired
    public ContractsService(ContractsRepository contractsRepository, ClientsRepository clientsRepository, CategoriesRepository categoriesRepository, UserRepository userRepository, EmailService emailService) {
        this.contractsRepository = contractsRepository;
        this.clientsRepository = clientsRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }



    //Busqueda de contratos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Contracts> contracts = contractsRepository.findAllWithClientAndCategory();
        logger.info("La búsqueda ha sido realizada correctamente");
        
        if (contracts.isEmpty()) {
            return new ResponseEntity<>(new Message(contracts, "No hay contratos registrados", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        logger.info("Listado de contratos obtenido correctamente");
        return new ResponseEntity<>(new Message(contracts, "Listado de contratos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar contratos por abogado específico
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByAbogado(Long abogadoId) {
        List<Contracts> contracts = contractsRepository.findAllByAbogado(abogadoId);
        logger.info("Buscando contratos para el abogado ID: {}", abogadoId);
        
        if (contracts.isEmpty()) {
            return new ResponseEntity<>(new Message(contracts, "No hay contratos asignados a este abogado", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        logger.info("Contratos del abogado obtenidos correctamente");
        return new ResponseEntity<>(new Message(contracts, "Contratos del abogado encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Obtener empresas asignadas a un abogado (clientes únicos)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getEmpresasByAbogado(Long abogadoId) {
        List<Contracts> contracts = contractsRepository.findAllByAbogado(abogadoId);
        logger.info("Buscando empresas asignadas al abogado: {}", abogadoId);
        
        if (contracts.isEmpty()) {
            return new ResponseEntity<>(new Message(contracts, "No hay empresas asignadas a este abogado", TypesResponse.WARNING), HttpStatus.OK);
        }
        
        // Obtener clientes únicos de los contratos del abogado
        List<java.util.Map<String, Object>> empresas = contracts.stream()
            .map(contract -> {
                java.util.Map<String, Object> empresa = new java.util.HashMap<>();
                empresa.put("clientId", contract.getClient_id().getId());
                empresa.put("clientName", contract.getClient_id().getName());
                empresa.put("categoryId", contract.getCategory_id().getId());
                empresa.put("categoryName", contract.getCategory_id().getName());
                empresa.put("contractId", contract.getId());
                empresa.put("contractName", contract.getName());
                empresa.put("contractDescription", contract.getDescription());
                empresa.put("status", contract.isStatus());
                empresa.put("dueDate", contract.getDue_date());
                return empresa;
            })
            .distinct() // Eliminar duplicados por cliente
            .collect(java.util.stream.Collectors.toList());
        
        logger.info("Empresas del abogado obtenidas correctamente");
        return new ResponseEntity<>(new Message(empresas, "Empresas del abogado obtenidas", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar contratos por cliente específico
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByClient(Long clientId) {
        List<Contracts> contracts = contractsRepository.findAllByClient(clientId);
        logger.info("Buscando contratos para el cliente ID: {}", clientId);
        logger.info("Contratos encontrados en BD: {}", contracts.size());
        
        if (contracts.isEmpty()) {
            logger.info("No hay contratos para el cliente ID: {}", clientId);
            return new ResponseEntity<>(new Message(contracts, "No hay contratos para este cliente", TypesResponse.WARNING), HttpStatus.OK);
        }
        
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
        List<Contracts> contracts = contractsRepository.findAllByClient(clientId);
        logger.info("Contratos encontrados en BD para cliente ID {}: {}", clientId, contracts.size());
        
        if (contracts.isEmpty()) {
            logger.info("No hay contratos para el cliente con email: {}", userEmail);
            return new ResponseEntity<>(new Message(contracts, "No hay contratos para este cliente", TypesResponse.WARNING), HttpStatus.OK);
        }
        
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

        // Validar regla de negocio: Un cliente solo puede tener un contrato activo
        int activeContractsCount = contractsRepository.countActiveContractsByClient(dto.getClientsDTO().getId());
        
        if (activeContractsCount > 0) {
            return new ResponseEntity<>(new Message("El cliente ya tiene un contrato activo. Un cliente solo puede tener un contrato asignado a la vez.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
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
        
        // Crear contrato en estado PENDIENTE (false) hasta que el abogado lo apruebe
        Contracts contracts = new Contracts(dto.getName(), dto.getDescription(), dto.getDue_date(), false, clients, categories);
        contracts.setStatus(false); // ❌ PENDIENTE: Requiere aprobación del abogado
        contracts.setAbogado_id(abogado);
        
        logger.info("Contrato creado en memoria - Cliente ID: {}, Abogado ID: {}", 
            contracts.getClient_id().getId(), contracts.getAbogado_id().getId());
        
        contracts = contractsRepository.saveAndFlush(contracts);
        
        logger.info("Contrato guardado en BD - ID: {}, Cliente ID: {}, Abogado ID: {}, Status: {}", 
            contracts.getId(), contracts.getClient_id().getId(), contracts.getAbogado_id().getId(), contracts.isStatus());
        
        if (contracts == null) {
            return new ResponseEntity<>(new Message("El contrato no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        
        logger.info("El registro ha sido realizado correctamente - Contrato en estado PENDIENTE");
        logger.info("IMPORTANTE: El contrato requiere aprobación del abogado antes de activarse");
        
        return new ResponseEntity<>(new Message(contracts, "El contrato se registró correctamente en estado PENDIENTE. Requiere aprobación del abogado.", TypesResponse.SUCCESS), HttpStatus.CREATED);
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

        // Validar regla de negocio: Un cliente solo puede tener un contrato activo
        // Solo validar si se está cambiando el cliente o si el contrato se está activando
        if (!clients.getId().equals(contracts.getClient_id().getId()) || dto.getStatus()) {
            int activeContractsCount = contractsRepository.countActiveContractsByClient(clients.getId());
            
            // Si se está activando este contrato, restar 1 del conteo
            if (dto.getStatus() && contracts.getId().equals(dto.getId())) {
                activeContractsCount = Math.max(0, activeContractsCount - 1);
            }
            
            if (activeContractsCount > 0) {
                return new ResponseEntity<>(new Message("El cliente ya tiene un contrato activo. Un cliente solo puede tener un contrato asignado a la vez.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
        }

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
            
            // Si se va a activar el contrato, validar que el cliente no tenga otro activo
            if (!contracts.isStatus()) { // Si está inactivo y se va a activar
                int activeContractsCount = contractsRepository.countActiveContractsByClient(contracts.getClient_id().getId());
                
                if (activeContractsCount > 0) {
                    return new ResponseEntity<>(new Message("No se puede activar este contrato. El cliente ya tiene un contrato activo.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
                }
            }
            
            contracts.setStatus(!contracts.isStatus());
            contracts = contractsRepository.saveAndFlush(contracts);
            if (contracts == null) {
                return new ResponseEntity<>(new Message("El status del contrato no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            logger.info("Contrato actualizado correctamente");
                    return new ResponseEntity<>(new Message(contracts, "El status del contrato fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Método para verificar si un cliente puede tener un nuevo contrato
    @Transactional(readOnly = true)
    public ResponseEntity<Message> canClientHaveNewContract(Long clientId) {
        if (clientId == null || clientId <= 0) {
            return new ResponseEntity<>(new Message("ID de cliente inválido", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        
        int activeContractsCount = contractsRepository.countActiveContractsByClient(clientId);
        boolean canHaveContract = activeContractsCount == 0;
        
        String message = canHaveContract ? 
            "El cliente puede tener un nuevo contrato" : 
            "El cliente ya tiene un contrato activo. No puede tener otro contrato asignado.";
        
        return new ResponseEntity<>(new Message(
            Map.of(
                "clientId", clientId,
                "canHaveContract", canHaveContract,
                "activeContractsCount", activeContractsCount,
                "message", message
            ), 
            message, 
            canHaveContract ? TypesResponse.SUCCESS : TypesResponse.WARNING
        ), HttpStatus.OK);
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


        //Busqueda de Contrato por nombre
        @Transactional(readOnly = true)
        public ResponseEntity<Message> findByName (String name){
            if (name == null || name.isEmpty()) {
                return new ResponseEntity<>(new Message("El nombre no debe ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            Optional<Contracts> contractsOptional = contractsRepository.findByNameWithRelations(name);
            if (!contractsOptional.isPresent()) {
                return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
            }
            logger.info("Busqueda de contrato por nombre realizada correctamente");
            return new ResponseEntity<>(new Message(contractsOptional.get(), "Contrato encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
        }


    //Busqueda de Contrato por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById (Long id){
        Optional<Contracts> contractsOptional = contractsRepository.findByIdWithRelations(id);
        if (!contractsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de contrato por ID realizada correctamente");
        return new ResponseEntity<>(new Message(contractsOptional.get(), "Contrato encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }



    //Busqueda de contratos activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue () {
        try {
            logger.info("Iniciando búsqueda de contratos activos");
            List<Contracts> contracts = contractsRepository.findAllByStatusIsTrue();
            logger.info("Contratos activos encontrados: {}", contracts.size());
            
            if (contracts.isEmpty()) {
                logger.info("No se encontraron contratos activos");
                return new ResponseEntity<>(new Message(contracts, "No hay contratos activos", TypesResponse.WARNING), HttpStatus.OK);
            }
            
            logger.info("Búsqueda de contratos activos completada exitosamente");
            return new ResponseEntity<>(new Message(contracts, "Contratos activos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error en findAllByStatusIsTrue", e);
            return new ResponseEntity<>(new Message("Error interno del servidor al buscar contratos activos", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Busqueda de contratos inactivos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsFalse () {
        try {
            logger.info("Iniciando búsqueda de contratos inactivos");
            List<Contracts> contracts = contractsRepository.findAllByStatusIsFalse();
            logger.info("Contratos inactivos encontrados: {}", contracts.size());
            
            if (contracts.isEmpty()) {
                logger.info("No se encontraron contratos inactivos");
                return new ResponseEntity<>(new Message(contracts, "No hay contratos inactivos", TypesResponse.WARNING), HttpStatus.OK);
            }
            
            logger.info("Búsqueda de contratos inactivos completada exitosamente");
            return new ResponseEntity<>(new Message(contracts, "Contratos inactivos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error en findAllByStatusIsFalse", e);
            return new ResponseEntity<>(new Message("Error interno del servidor al buscar contratos inactivos", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            
            contractInfo.addCell(new PdfPCell(new Paragraph("Estado:")));
            contractInfo.addCell(new PdfPCell(new Paragraph(contract.isStatus() ? "ACTIVO" : "INACTIVO")));
            
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

    // Método para aceptar un contrato por parte del abogado
    @Transactional
    public ResponseEntity<Message> acceptContract(Long contractId, Long abogadoId) {
        try {
            logger.info("Intentando aceptar contrato {} por abogado {}", contractId, abogadoId);
            
            // Verificar que el contrato existe
            Optional<Contracts> contractOpt = contractsRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                logger.warn("Contrato {} no encontrado", contractId);
                return new ResponseEntity<>(new Message(null, "Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            Contracts contract = contractOpt.get();
            
            // Verificar que el abogado existe
            Optional<User> abogadoOpt = userRepository.findById(abogadoId);
            if (abogadoOpt.isEmpty()) {
                logger.warn("Abogado {} no encontrado", abogadoId);
                return new ResponseEntity<>(new Message(null, "Abogado no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            User abogado = abogadoOpt.get();
            
            // Verificar que el abogado está asignado al contrato
            if (contract.getAbogado_id() == null || !contract.getAbogado_id().getId().equals(abogadoId)) {
                logger.warn("Abogado {} no está asignado al contrato {}", abogadoId, contractId);
                return new ResponseEntity<>(new Message(null, "No tienes permisos para aceptar este contrato", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
            }
            
            // Verificar que el contrato no haya sido ya aceptado
            if (contract.isStatus()) {
                logger.warn("Contrato {} ya ha sido aceptado anteriormente", contractId);
                return new ResponseEntity<>(new Message(null, "Este contrato ya ha sido aceptado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            
            // Log del estado actual del contrato
            logger.info("Estado actual del contrato {}: status={}, abogado_id={}", 
                contractId, contract.isStatus(), 
                contract.getAbogado_id() != null ? contract.getAbogado_id().getId() : "null");
            
            // Cambiar el estado del contrato a aceptado
            boolean oldStatus = contract.isStatus();
            contract.setStatus(true); // Activar el contrato
            contract.setUpdated_at(LocalDateTime.now());
            
            logger.info("Cambiando estado del contrato {} de {} a {}", contractId, oldStatus, contract.isStatus());
            
            // Guardar el contrato
            Contracts savedContract = contractsRepository.save(contract);
            logger.info("Contrato guardado con ID: {}, nuevo status: {}", savedContract.getId(), savedContract.isStatus());
            
            // Enviar email al cliente con los detalles del contrato
            try {
                String clientEmail = contract.getClient_id().getEmail();
                String clientName = contract.getClient_id().getRepresentative_name() + " " + contract.getClient_id().getRepresentative_surnames();
                String abogadoName = abogado.getName() + " " + abogado.getLastName();
                
                String subject = "✅ Contrato Aceptado - " + contract.getName();
                
                String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; text-align: center; color: white;">
                        <h1 style="margin: 0; font-size: 24px;">🎉 ¡Contrato Aceptado!</h1>
                        <p style="margin: 10px 0 0 0; font-size: 16px;">Su contrato ha sido revisado y aceptado</p>
                    </div>
                    
                    <div style="padding: 30px; background-color: #f9f9f9;">
                        <h2 style="color: #2c3e50; margin-bottom: 20px;">Hola %s,</h2>
                        
                        <p style="font-size: 16px; margin-bottom: 20px;">
                            Nos complace informarle que su contrato ha sido <strong>aceptado y aprobado</strong> por nuestro equipo legal.
                        </p>
                        
                        <div style="background-color: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin: 20px 0;">
                            <h3 style="color: #27ae60; margin-top: 0;">📋 Detalles del Contrato</h3>
                            
                            <table style="width: 100%%; border-collapse: collapse;">
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Nombre del Contrato:</td>
                                    <td style="padding: 8px 0; color: #333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Descripción:</td>
                                    <td style="padding: 8px 0; color: #333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Fecha de Vencimiento:</td>
                                    <td style="padding: 8px 0; color: #333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Categoría:</td>
                                    <td style="padding: 8px 0; color: #333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Abogado Asignado:</td>
                                    <td style="padding: 8px 0; color: #333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; font-weight: bold; color: #555;">Estado:</td>
                                    <td style="padding: 8px 0; color: #27ae60; font-weight: bold;">✅ ACEPTADO</td>
                                </tr>
                            </table>
                        </div>
                        
                        <div style="background-color: #e8f5e8; padding: 20px; border-radius: 8px; border-left: 4px solid #27ae60; margin: 20px 0;">
                            <h4 style="color: #27ae60; margin-top: 0;">🎯 Próximos Pasos</h4>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Su contrato está ahora <strong>activo y en proceso</strong></li>
                                <li>Nuestro equipo legal comenzará a trabajar en su caso</li>
                                <li>Recibirá actualizaciones periódicas sobre el progreso</li>
                                <li>Puede acceder a su contrato desde su panel de cliente</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="http://localhost:3000/login" style="background-color: #27ae60; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">
                                🔐 Acceder a Mi Panel
                            </a>
                        </div>
                        
                        <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                        
                        <p style="font-size: 14px; color: #666; text-align: center;">
                            Si tiene alguna pregunta, no dude en contactarnos.<br>
                            <strong>Equipo Legal - TrackOnTract</strong><br>
                            📧 soporte@trackontract.com | 📞 +52 55 1234 5678
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(
                    clientName,
                    contract.getName(),
                    contract.getDescription() != null ? contract.getDescription() : "Sin descripción",
                    contract.getDue_date() != null ? contract.getDue_date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "No especificada",
                    contract.getCategory_id() != null ? contract.getCategory_id().getName() : "No especificada",
                    abogadoName
                );
                
                emailService.sendEmail(clientEmail, subject, htmlContent);
                logger.info("Email enviado exitosamente al cliente {} ({})", clientName, clientEmail);
                
            } catch (Exception emailError) {
                logger.error("Error enviando email al cliente: {}", emailError.getMessage());
                // No fallamos la operación si el email falla, solo lo registramos
            }
            
            // Verificar que se guardó correctamente
            Optional<Contracts> verifyContract = contractsRepository.findById(contractId);
            if (verifyContract.isPresent()) {
                logger.info("Verificación: contrato {} tiene status: {}", contractId, verifyContract.get().isStatus());
            } else {
                logger.warn("No se pudo verificar el contrato después de guardar");
            }
            
            logger.info("Contrato {} aceptado exitosamente por abogado {}", contractId, abogadoId);
            return new ResponseEntity<>(new Message(savedContract, "Contrato aceptado exitosamente. Se ha enviado una notificación por email al cliente.", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error aceptando contrato {}: {}", contractId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para rechazar un contrato por parte del abogado
    @Transactional
    public ResponseEntity<Message> rejectContract(Long contractId, Long abogadoId, String rejectionReason) {
        try {
            logger.info("Intentando rechazar contrato {} por abogado {} con motivo: {}", contractId, abogadoId, rejectionReason);
            
            // Verificar que el contrato existe
            Optional<Contracts> contractOpt = contractsRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                logger.warn("Contrato {} no encontrado", contractId);
                return new ResponseEntity<>(new Message(null, "Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            Contracts contract = contractOpt.get();
            
            // Verificar que el abogado existe
            Optional<User> abogadoOpt = userRepository.findById(abogadoId);
            if (abogadoOpt.isEmpty()) {
                logger.warn("Abogado {} no encontrado", abogadoId);
                return new ResponseEntity<>(new Message(null, "Abogado no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            User abogado = abogadoOpt.get();
            
            // Verificar que el abogado está asignado al contrato
            if (contract.getAbogado_id() == null || !contract.getAbogado_id().getId().equals(abogadoId)) {
                logger.warn("Abogado {} no está asignado al contrato {}", abogadoId, contractId);
                return new ResponseEntity<>(new Message(null, "No tienes permisos para rechazar este contrato", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
            }
            
            // Validar que se proporcione un motivo de rechazo
            if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
                logger.warn("Motivo de rechazo no proporcionado para contrato {}", contractId);
                return new ResponseEntity<>(new Message(null, "Debe proporcionar un motivo para rechazar el contrato", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            
            // Log del estado actual del contrato
            logger.info("Estado actual del contrato {}: status={}, abogado_id={}", 
                contractId, contract.isStatus(), 
                contract.getAbogado_id() != null ? contract.getAbogado_id().getId() : "null");
            
            // Por ahora, simplemente desactivamos el contrato
            // En el futuro se puede implementar la lógica completa de aprobaciones
            boolean oldStatus = contract.isStatus();
            contract.setStatus(false); // Desactivar el contrato
            contract.setUpdated_at(LocalDateTime.now());
            
            logger.info("Cambiando estado del contrato {} de {} a {}", contractId, oldStatus, contract.isStatus());
            
            // Guardar el contrato
            Contracts savedContract = contractsRepository.save(contract);
            logger.info("Contrato rechazado guardado con ID: {}, nuevo status: {}", savedContract.getId(), savedContract.isStatus());
            
            // Verificar que se guardó correctamente
            Optional<Contracts> verifyContract = contractsRepository.findById(contractId);
            if (verifyContract.isPresent()) {
                logger.info("Verificación: contrato {} tiene status: {}", contractId, verifyContract.get().isStatus());
            } else {
                logger.warn("No se pudo verificar el contrato después de guardar");
            }
            
            logger.info("Contrato {} rechazado exitosamente por abogado {} con motivo: {}", contractId, abogadoId, rejectionReason);
            return new ResponseEntity<>(new Message(savedContract, "Contrato rechazado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error rechazando contrato {}: {}", contractId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para debuggear un contrato
    @Transactional(readOnly = true)
    public ResponseEntity<Message> debugContract(Long contractId) {
        try {
            logger.info("Debuggeando contrato {}", contractId);
            
            Optional<Contracts> contractOpt = contractsRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                return new ResponseEntity<>(new Message(null, "Contrato no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            Contracts contract = contractOpt.get();
            
            Map<String, Object> debugInfo = Map.of(
                "id", contract.getId(),
                "name", contract.getName(),
                "status", contract.isStatus(),
                "created_at", contract.getCreated_at(),
                "updated_at", contract.getUpdated_at(),
                "abogado_id", contract.getAbogado_id() != null ? contract.getAbogado_id().getId() : "null",
                "client_id", contract.getClient_id() != null ? contract.getClient_id().getId() : "null",
                "category_id", contract.getCategory_id() != null ? contract.getCategory_id().getId() : "null"
            );
            
            logger.info("Información de debug del contrato {}: {}", contractId, debugInfo);
            return new ResponseEntity<>(new Message(debugInfo, "Información de debug del contrato", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error debuggeando contrato {}: {}", contractId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para debuggear contratos de un abogado
    @Transactional(readOnly = true)
    public ResponseEntity<Message> debugAbogadoContracts(Long abogadoId) {
        try {
            logger.info("Debuggeando contratos del abogado {}", abogadoId);
            
            List<Contracts> contracts = contractsRepository.findAllByAbogado(abogadoId);
            logger.info("Contratos encontrados para abogado {}: {}", abogadoId, contracts.size());
            
            List<Map<String, Object>> debugInfo = contracts.stream()
                .map(contract -> {
                    Map<String, Object> contractInfo = new java.util.HashMap<>();
                    contractInfo.put("id", contract.getId());
                    contractInfo.put("name", contract.getName());
                    contractInfo.put("status", contract.isStatus());
                    contractInfo.put("created_at", contract.getCreated_at());
                    contractInfo.put("updated_at", contract.getUpdated_at());
                    contractInfo.put("abogado_id", contract.getAbogado_id() != null ? contract.getAbogado_id().getId() : "null");
                    contractInfo.put("client_id", contract.getClient_id() != null ? contract.getClient_id().getId() : "null");
                    contractInfo.put("category_id", contract.getCategory_id() != null ? contract.getCategory_id().getId() : "null");
                    return contractInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            logger.info("Información de debug de contratos del abogado {}: {}", abogadoId, debugInfo);
            return new ResponseEntity<>(new Message(debugInfo, "Información de debug de contratos del abogado", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error debuggeando contratos del abogado {}: {}", abogadoId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

