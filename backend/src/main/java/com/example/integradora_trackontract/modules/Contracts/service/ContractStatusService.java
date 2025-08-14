package com.example.integradora_trackontract.modules.Contracts.service;

import com.example.integradora_trackontract.modules.Contracts.model.*;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio especializado para la gestión de estados de contratos
 */
@Service
@Transactional
public class ContractStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractStatusService.class);
    
    private final ContractsRepository contractsRepository;
    private final ContractStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public ContractStatusService(ContractsRepository contractsRepository,
                               ContractStatusHistoryRepository statusHistoryRepository,
                               UserRepository userRepository) {
        this.contractsRepository = contractsRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Cambia el estado de un contrato
     */
    public ResponseEntity<Message> changeContractStatus(ContractStatusChangeDTO statusChangeDTO) {
        try {
            // Validar que el contrato existe
            Optional<Contracts> contractOpt = contractsRepository.findById(statusChangeDTO.getContractId());
            if (contractOpt.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(null, "Contrato no encontrado", TypesResponse.ERROR),
                    HttpStatus.NOT_FOUND
                );
            }

            Contracts contract = contractOpt.get();
            ContractStatus currentStatus = contract.getContractStatus();
            ContractStatus newStatus = statusChangeDTO.getNewStatus();

            // Validar que la transición de estado es válida
            if (!currentStatus.canTransitionTo(newStatus)) {
                return new ResponseEntity<>(
                    new Message(null, 
                        "Transición de estado no válida: " + currentStatus.getDisplayName() + " → " + newStatus.getDisplayName(), 
                        TypesResponse.ERROR),
                    HttpStatus.BAD_REQUEST
                );
            }

            // Validar que el usuario existe
            Optional<User> userOpt = userRepository.findById(statusChangeDTO.getChangedByUserId());
            if (userOpt.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(null, "Usuario no encontrado", TypesResponse.ERROR),
                    HttpStatus.NOT_FOUND
                );
            }

            User user = userOpt.get();

            // Crear registro del historial antes del cambio
            ContractStatusHistory historyEntry = new ContractStatusHistory(
                contract, currentStatus, newStatus, 
                statusChangeDTO.getChangeReason(), user
            );

            // Agregar información adicional del contexto
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                historyEntry.setIpAddress(getClientIpAddress(request));
                historyEntry.setUserAgent(request.getHeader("User-Agent"));
            }
            
            if (statusChangeDTO.getAdditionalNotes() != null) {
                historyEntry.setAdditionalNotes(statusChangeDTO.getAdditionalNotes());
            }

            // Actualizar el estado del contrato
            contract.setContractStatus(newStatus);
            contract.setUpdated_at(LocalDateTime.now());

            // Si el nuevo estado es APPROVED, actualizar approvedAt
            if (newStatus == ContractStatus.APPROVED) {
                contract.setApprovedAt(LocalDateTime.now());
            }

            // Guardar el contrato actualizado
            contractsRepository.save(contract);

            // Guardar el historial
            statusHistoryRepository.save(historyEntry);

            logger.info("Estado del contrato {} cambiado de {} a {} por el usuario {}", 
                contract.getId(), currentStatus.getDisplayName(), newStatus.getDisplayName(), user.getId());

            return new ResponseEntity<>(
                new Message(contract, 
                    "Estado del contrato cambiado exitosamente de " + currentStatus.getDisplayName() + 
                    " a " + newStatus.getDisplayName(), 
                    TypesResponse.SUCCESS),
                HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error al cambiar el estado del contrato: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                new Message(null, "Error interno del servidor", TypesResponse.ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtiene el historial de cambios de estado de un contrato
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getContractStatusHistory(Long contractId) {
        try {
            // Validar que el contrato existe
            if (!contractsRepository.existsById(contractId)) {
                return new ResponseEntity<>(
                    new Message(null, "Contrato no encontrado", TypesResponse.ERROR),
                    HttpStatus.NOT_FOUND
                );
            }

            List<ContractStatusHistory> history = statusHistoryRepository
                .findByContractIdOrderByChangedAtDesc(contractId);

            if (history.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(history, "No hay historial de cambios de estado para este contrato", TypesResponse.WARNING),
                    HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                new Message(history, "Historial de cambios de estado obtenido exitosamente", TypesResponse.SUCCESS),
                HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error al obtener el historial de cambios de estado: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                new Message(null, "Error interno del servidor", TypesResponse.ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtiene los estados válidos a los que se puede transicionar desde el estado actual
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getValidStatusTransitions(Long contractId) {
        try {
            Optional<Contracts> contractOpt = contractsRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(null, "Contrato no encontrado", TypesResponse.ERROR),
                    HttpStatus.NOT_FOUND
                );
            }

            Contracts contract = contractOpt.get();
            ContractStatus currentStatus = contract.getContractStatus();
            ContractStatus[] validTransitions = currentStatus.getValidTransitions();

            return new ResponseEntity<>(
                new Message(validTransitions, 
                    "Transiciones válidas obtenidas para el estado: " + currentStatus.getDisplayName(), 
                    TypesResponse.SUCCESS),
                HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error al obtener las transiciones válidas: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                new Message(null, "Error interno del servidor", TypesResponse.ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtiene contratos por estado específico
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getContractsByStatus(ContractStatus status) {
        try {
            List<Contracts> contracts = contractsRepository.findByContractStatus(status);
            
            if (contracts.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(contracts, 
                        "No hay contratos con el estado: " + status.getDisplayName(), 
                        TypesResponse.WARNING),
                    HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                new Message(contracts, 
                    "Contratos obtenidos exitosamente para el estado: " + status.getDisplayName(), 
                    TypesResponse.SUCCESS),
                HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error al obtener contratos por estado: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                new Message(null, "Error interno del servidor", TypesResponse.ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtiene el estado actual de un contrato
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getCurrentContractStatus(Long contractId) {
        try {
            Optional<Contracts> contractOpt = contractsRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                return new ResponseEntity<>(
                    new Message(null, "Contrato no encontrado", TypesResponse.ERROR),
                    HttpStatus.NOT_FOUND
                );
            }

            Contracts contract = contractOpt.get();
            return new ResponseEntity<>(
                new Message(contract.getContractStatus(), 
                    "Estado actual del contrato obtenido exitosamente", 
                    TypesResponse.SUCCESS),
                HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error al obtener el estado actual del contrato: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                new Message(null, "Error interno del servidor", TypesResponse.ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtiene la solicitud HTTP actual
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            logger.warn("No se pudo obtener la solicitud HTTP actual: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la dirección IP del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
