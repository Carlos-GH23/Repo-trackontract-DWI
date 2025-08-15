package com.example.integradora_trackontract.modules.Contract_Approvals.control;

import com.example.integradora_trackontract.modules.Contract_Approvals.model.ContractRejection;
import com.example.integradora_trackontract.modules.Contract_Approvals.model.ContractRejectionDTO;
import com.example.integradora_trackontract.modules.Contract_Approvals.model.ContractRejectionRepository;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsRepository;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@Transactional
public class ContractRejectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractRejectionService.class);
    
    private final ContractRejectionRepository rejectionRepository;
    private final ContractsRepository contractsRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Autowired
    public ContractRejectionService(ContractRejectionRepository rejectionRepository,
                                   ContractsRepository contractsRepository,
                                   UserRepository userRepository,
                                   EmailService emailService) {
        this.rejectionRepository = rejectionRepository;
        this.contractsRepository = contractsRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    // Crear un nuevo rechazo de contrato
    @Transactional
    public ResponseEntity<Message> createRejection(Long contractId, Long abogadoId, String rejectionReason) {
        try {
            logger.info("Creando rechazo para contrato {} por abogado {} con motivo: {}", contractId, abogadoId, rejectionReason);
            
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
            
            // Verificar que no exista ya un rechazo para este contrato
            Optional<ContractRejection> existingRejection = rejectionRepository.findByContractId(contractId);
            if (existingRejection.isPresent()) {
                logger.warn("Ya existe un rechazo para el contrato {}", contractId);
                return new ResponseEntity<>(new Message(null, "Este contrato ya ha sido rechazado anteriormente", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            
            // Crear el rechazo
            ContractRejection rejection = new ContractRejection(contract, abogado, rejectionReason.trim());
            ContractRejection savedRejection = rejectionRepository.save(rejection);
            
            logger.info("Rechazo creado exitosamente con ID: {}", savedRejection.getId());
            
            // Enviar notificación por email al admin
            try {
                sendRejectionNotificationToAdmin(savedRejection);
            } catch (Exception emailError) {
                logger.error("Error enviando notificación por email al admin: {}", emailError.getMessage());
                // No fallamos la operación si el email falla
            }
            
            // Convertir a DTO para la respuesta
            ContractRejectionDTO rejectionDTO = new ContractRejectionDTO(savedRejection);
            
            logger.info("Rechazo de contrato {} procesado exitosamente", contractId);
            return new ResponseEntity<>(new Message(rejectionDTO, "Contrato rechazado exitosamente. Se ha notificado al administrador.", TypesResponse.SUCCESS), HttpStatus.CREATED);
            
        } catch (Exception e) {
            logger.error("Error creando rechazo para contrato {}: {}", contractId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Obtener todos los rechazos pendientes de revisión del admin
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getPendingRejections() {
        try {
            logger.info("Obteniendo rechazos pendientes de revisión del admin");
            
            List<ContractRejection> pendingRejections = rejectionRepository.findByAdminReviewedFalseOrderByRejectionDateDesc();
            
            if (pendingRejections.isEmpty()) {
                logger.info("No hay rechazos pendientes de revisión");
                return new ResponseEntity<>(new Message(List.of(), "No hay rechazos pendientes de revisión", TypesResponse.WARNING), HttpStatus.OK);
            }
            
            List<ContractRejectionDTO> rejectionDTOs = pendingRejections.stream()
                .map(ContractRejectionDTO::new)
                .collect(Collectors.toList());
            
            logger.info("Rechazos pendientes obtenidos: {}", rejectionDTOs.size());
            return new ResponseEntity<>(new Message(rejectionDTOs, "Rechazos pendientes obtenidos exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error obteniendo rechazos pendientes: {}", e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Obtener rechazo por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getRejectionById(Long rejectionId) {
        try {
            logger.info("Obteniendo rechazo con ID: {}", rejectionId);
            
            Optional<ContractRejection> rejectionOpt = rejectionRepository.findById(rejectionId);
            if (rejectionOpt.isEmpty()) {
                logger.warn("Rechazo {} no encontrado", rejectionId);
                return new ResponseEntity<>(new Message(null, "Rechazo no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            ContractRejectionDTO rejectionDTO = new ContractRejectionDTO(rejectionOpt.get());
            
            logger.info("Rechazo {} obtenido exitosamente", rejectionId);
            return new ResponseEntity<>(new Message(rejectionDTO, "Rechazo obtenido exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error obteniendo rechazo {}: {}", rejectionId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Revisar rechazo por parte del admin
    @Transactional
    public ResponseEntity<Message> reviewRejection(Long rejectionId, Long adminId, String adminComments, String finalDecision) {
        try {
            logger.info("Admin {} revisando rechazo {} con decisión: {}", adminId, rejectionId, finalDecision);
            
            // Verificar que el rechazo existe
            Optional<ContractRejection> rejectionOpt = rejectionRepository.findById(rejectionId);
            if (rejectionOpt.isEmpty()) {
                logger.warn("Rechazo {} no encontrado", rejectionId);
                return new ResponseEntity<>(new Message(null, "Rechazo no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            ContractRejection rejection = rejectionOpt.get();
            
            // Verificar que el admin existe
            Optional<User> adminOpt = userRepository.findById(adminId);
            if (adminOpt.isEmpty()) {
                logger.warn("Admin {} no encontrado", adminId);
                return new ResponseEntity<>(new Message(null, "Administrador no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
            }
            
            User admin = adminOpt.get();
            
            // Verificar que el usuario es admin
            if (!"ADMIN".equals(admin.getRol_id().getName())) {
                logger.warn("Usuario {} no tiene permisos de administrador", adminId);
                return new ResponseEntity<>(new Message(null, "No tienes permisos para realizar esta acción", TypesResponse.ERROR), HttpStatus.FORBIDDEN);
            }
            
            // Validar la decisión final
            if (finalDecision == null || finalDecision.trim().isEmpty()) {
                logger.warn("Decisión final no proporcionada para rechazo {}", rejectionId);
                return new ResponseEntity<>(new Message(null, "Debe proporcionar una decisión final", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            
            if (!List.of("APPROVED", "REJECTED", "NEEDS_REVISION").contains(finalDecision.toUpperCase())) {
                logger.warn("Decisión final inválida: {}", finalDecision);
                return new ResponseEntity<>(new Message(null, "Decisión final inválida. Debe ser: APPROVED, REJECTED o NEEDS_REVISION", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }
            
            // Actualizar el rechazo
            rejection.setAdminReviewed(true);
            rejection.setAdminComments(adminComments != null ? adminComments.trim() : null);
            rejection.setAdminReviewDate(LocalDateTime.now());
            rejection.setReviewedByAdmin(admin);
            rejection.setFinalDecision(finalDecision.toUpperCase());
            
            ContractRejection savedRejection = rejectionRepository.save(rejection);
            
            logger.info("Rechazo {} revisado exitosamente por admin {}", rejectionId, adminId);
            
            // Enviar notificación por email al abogado
            try {
                sendAdminReviewNotificationToAbogado(savedRejection);
            } catch (Exception emailError) {
                logger.error("Error enviando notificación por email al abogado: {}", emailError.getMessage());
                // No fallamos la operación si el email falla
            }
            
            // Convertir a DTO para la respuesta
            ContractRejectionDTO rejectionDTO = new ContractRejectionDTO(savedRejection);
            
            return new ResponseEntity<>(new Message(rejectionDTO, "Rechazo revisado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error revisando rechazo {}: {}", rejectionId, e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Contar rechazos pendientes
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getPendingRejectionsCount() {
        try {
            long count = rejectionRepository.countByAdminReviewedFalse();
            logger.info("Rechazos pendientes de revisión: {}", count);
            
            return new ResponseEntity<>(new Message(Map.of("pendingCount", count), "Conteo de rechazos pendientes obtenido", TypesResponse.SUCCESS), HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error contando rechazos pendientes: {}", e.getMessage());
            return new ResponseEntity<>(new Message(null, "Error interno del servidor", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Enviar notificación por email al admin sobre un rechazo
    private void sendRejectionNotificationToAdmin(ContractRejection rejection) {
        try {
            // Buscar usuarios admin
            List<User> admins = userRepository.findAll().stream()
                .filter(user -> "ADMIN".equals(user.getRol_id().getName()))
                .collect(Collectors.toList());
            
            if (admins.isEmpty()) {
                logger.warn("No se encontraron usuarios administradores para enviar notificación");
                return;
            }
            
            String subject = "🚨 Contrato Rechazado Requiere Revisión - " + rejection.getContract().getName();
            
            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto;">
                <div style="background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); padding: 20px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 24px;">🚨 Contrato Rechazado</h1>
                    <p style="margin: 10px 0 0 0; font-size: 16px;">Requiere revisión administrativa</p>
                </div>
                
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #2c3e50; margin-bottom: 20px;">Notificación de Rechazo</h2>
                    
                    <div style="background-color: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin: 20px 0;">
                        <h3 style="color: #e74c3c; margin-top: 0;">📋 Detalles del Contrato Rechazado</h3>
                        
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Nombre del Contrato:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Cliente:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Categoría:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Abogado que Rechazó:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Fecha de Rechazo:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <div style="background-color: #fff3cd; padding: 20px; border-radius: 8px; border-left: 4px solid #ffc107; margin: 20px 0;">
                        <h4 style="color: #856404; margin-top: 0;">📝 Motivo del Rechazo</h4>
                        <p style="color: #856404; margin: 0;">%s</p>
                    </div>
                    
                    <div style="background-color: #d1ecf1; padding: 20px; border-radius: 8px; border-left: 4px solid #17a2b8; margin: 20px 0;">
                        <h4 style="color: #0c5460; margin-top: 0;">⚡ Acción Requerida</h4>
                        <p style="color: #0c5460; margin: 0;">
                            Este contrato rechazado requiere su revisión y decisión final. 
                            Puede aprobar, rechazar definitivamente o solicitar revisiones.
                        </p>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:3000/admin/contratos/rechazos" style="background-color: #e74c3c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">
                            🔍 Revisar Rechazo
                        </a>
                    </div>
                    
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                    
                    <p style="font-size: 14px; color: #666; text-align: center;">
                        Sistema de Gestión de Contratos - TrackOnTract<br>
                        📧 soporte@trackontract.com | 📞 +52 55 1234 5678
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                rejection.getContract().getName(),
                rejection.getContract().getClient_id().getName(),
                rejection.getContract().getCategory_id().getName(),
                rejection.getRejectedByAbogado().getName() + " " + rejection.getRejectedByAbogado().getLastName(),
                rejection.getRejectionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                rejection.getRejectionReason()
            );
            
            // Enviar email a todos los admins
            for (User admin : admins) {
                emailService.sendEmail(admin.getEmail(), subject, htmlContent);
                logger.info("Notificación de rechazo enviada al admin: {} ({})", admin.getName(), admin.getEmail());
            }
            
        } catch (Exception e) {
            logger.error("Error enviando notificación de rechazo al admin: {}", e.getMessage());
        }
    }
    
    // Enviar notificación por email al abogado sobre la revisión del admin
    private void sendAdminReviewNotificationToAbogado(ContractRejection rejection) {
        try {
            String subject = "📋 Revisión de Rechazo - " + rejection.getContract().getName();
            
            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto;">
                <div style="background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); padding: 20px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 24px;">📋 Revisión Completada</h1>
                    <p style="margin: 10px 0 0 0; font-size: 16px;">Su rechazo de contrato ha sido revisado</p>
                </div>
                
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2 style="color: #2c3e50; margin-bottom: 20px;">Hola %s,</h2>
                    
                    <p style="font-size: 16px; margin-bottom: 20px;">
                        Su rechazo del contrato <strong>%s</strong> ha sido revisado por el administrador.
                    </p>
                    
                    <div style="background-color: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin: 20px 0;">
                        <h3 style="color: #3498db; margin-top: 0;">📋 Resultado de la Revisión</h3>
                        
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Contrato:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Cliente:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Decisión Final:</td>
                                <td style="padding: 8px 0; color: #333; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Fecha de Revisión:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    %s
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:3000/abogado/contract" style="background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">
                            🔍 Ver Contratos
                        </a>
                    </div>
                    
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                    
                    <p style="font-size: 14px; color: #666; text-align: center;">
                        Sistema de Gestión de Contratos - TrackOnTract<br>
                        📧 soporte@trackontract.com | 📞 +52 55 1234 5678
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                rejection.getRejectedByAbogado().getName(),
                rejection.getContract().getName(),
                rejection.getContract().getName(),
                rejection.getContract().getClient_id().getName(),
                rejection.getFinalDecision(),
                rejection.getAdminReviewDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                rejection.getAdminComments() != null ? 
                    String.format("""
                    <div style="background-color: #e8f5e8; padding: 20px; border-radius: 8px; border-left: 4px solid #27ae60; margin: 20px 0;">
                        <h4 style="color: #27ae60; margin-top: 0;">💬 Comentarios del Administrador</h4>
                        <p style="color: #27ae60; margin: 0;">%s</p>
                    </div>
                    """, rejection.getAdminComments()) : ""
            );
            
            emailService.sendEmail(rejection.getRejectedByAbogado().getEmail(), subject, htmlContent);
            logger.info("Notificación de revisión enviada al abogado: {} ({})", 
                rejection.getRejectedByAbogado().getName(), rejection.getRejectedByAbogado().getEmail());
            
        } catch (Exception e) {
            logger.error("Error enviando notificación de revisión al abogado: {}", e.getMessage());
        }
    }
}
