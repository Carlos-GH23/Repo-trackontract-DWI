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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
        
        Contracts contracts = new Contracts(dto.getName(), dto.getDescription(), dto.getDue_date(), true, clients, categories);
        contracts.setStatus(true);
        contracts.setAbogado_id(abogado);
        
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

    }

