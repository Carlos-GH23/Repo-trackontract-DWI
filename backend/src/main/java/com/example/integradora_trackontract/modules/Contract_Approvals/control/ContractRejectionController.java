package com.example.integradora_trackontract.modules.Contract_Approvals.control;

import com.example.integradora_trackontract.modules.Contract_Approvals.control.ContractRejectionService;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contract-rejections")
@CrossOrigin(origins = "http://localhost:5173")
public class ContractRejectionController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractRejectionController.class);
    
    private final ContractRejectionService rejectionService;
    
    @Autowired
    public ContractRejectionController(ContractRejectionService rejectionService) {
        this.rejectionService = rejectionService;
    }
    
    // Crear un nuevo rechazo de contrato (para abogados)
    @PostMapping("/reject")
    public ResponseEntity<Message> rejectContract(@RequestBody Map<String, Object> request) {
        try {
            Long contractId = Long.valueOf(request.get("contractId").toString());
            Long abogadoId = Long.valueOf(request.get("abogadoId").toString());
            String rejectionReason = request.get("rejectionReason").toString();
            
            logger.info("Abogado {} rechazando contrato {} con motivo: {}", abogadoId, contractId, rejectionReason);
            
            return rejectionService.createRejection(contractId, abogadoId, rejectionReason);
            
        } catch (Exception e) {
            logger.error("Error en rechazo de contrato: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Message("Datos de rechazo inválidos", TypesResponse.ERROR));
        }
    }
    
    // Obtener todos los rechazos pendientes de revisión (para admins)
    @GetMapping("/pending")
    public ResponseEntity<Message> getPendingRejections() {
        logger.info("Obteniendo rechazos pendientes de revisión");
        return rejectionService.getPendingRejections();
    }
    
    // Obtener rechazo por ID
    @GetMapping("/{rejectionId}")
    public ResponseEntity<Message> getRejectionById(@PathVariable Long rejectionId) {
        logger.info("Obteniendo rechazo con ID: {}", rejectionId);
        return rejectionService.getRejectionById(rejectionId);
    }
    
    // Revisar rechazo por parte del admin
    @PostMapping("/{rejectionId}/review")
    public ResponseEntity<Message> reviewRejection(@PathVariable Long rejectionId, 
                                                  @RequestBody Map<String, Object> request) {
        try {
            Long adminId = Long.valueOf(request.get("adminId").toString());
            String adminComments = request.get("adminComments") != null ? request.get("adminComments").toString() : null;
            String finalDecision = request.get("finalDecision").toString();
            
            logger.info("Admin {} revisando rechazo {} con decisión: {}", adminId, rejectionId, finalDecision);
            
            return rejectionService.reviewRejection(rejectionId, adminId, adminComments, finalDecision);
            
        } catch (Exception e) {
            logger.error("Error en revisión de rechazo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Message("Datos de revisión inválidos", TypesResponse.ERROR));
        }
    }
    
    // Contar rechazos pendientes
    @GetMapping("/pending/count")
    public ResponseEntity<Message> getPendingRejectionsCount() {
        logger.info("Obteniendo conteo de rechazos pendientes");
        return rejectionService.getPendingRejectionsCount();
    }
}
