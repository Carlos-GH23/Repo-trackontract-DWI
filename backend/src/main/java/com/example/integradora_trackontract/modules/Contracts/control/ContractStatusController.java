package com.example.integradora_trackontract.modules.Contracts.control;

import com.example.integradora_trackontract.modules.Contracts.model.ContractStatus;
import com.example.integradora_trackontract.modules.Contracts.model.ContractStatusChangeDTO;
import com.example.integradora_trackontract.modules.Contracts.service.ContractStatusService;
import com.example.integradora_trackontract.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la gestión de estados de contratos
 */
@RestController
@RequestMapping("/contract-status")
public class ContractStatusController {

    private final ContractStatusService contractStatusService;

    @Autowired
    public ContractStatusController(ContractStatusService contractStatusService) {
        this.contractStatusService = contractStatusService;
    }

    /**
     * Cambia el estado de un contrato
     */
    @PostMapping("/change")
    public ResponseEntity<Message> changeContractStatus(@Validated @RequestBody ContractStatusChangeDTO statusChangeDTO) {
        return contractStatusService.changeContractStatus(statusChangeDTO);
    }

    /**
     * Obtiene el historial de cambios de estado de un contrato
     */
    @GetMapping("/history/{contractId}")
    public ResponseEntity<Message> getContractStatusHistory(@PathVariable Long contractId) {
        return contractStatusService.getContractStatusHistory(contractId);
    }

    /**
     * Obtiene los estados válidos a los que se puede transicionar desde el estado actual
     */
    @GetMapping("/transitions/{contractId}")
    public ResponseEntity<Message> getValidStatusTransitions(@PathVariable Long contractId) {
        return contractStatusService.getValidStatusTransitions(contractId);
    }

    /**
     * Obtiene contratos por estado específico
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Message> getContractsByStatus(@PathVariable ContractStatus status) {
        return contractStatusService.getContractsByStatus(status);
    }

    /**
     * Obtiene el estado actual de un contrato
     */
    @GetMapping("/current/{contractId}")
    public ResponseEntity<Message> getCurrentContractStatus(@PathVariable Long contractId) {
        return contractStatusService.getCurrentContractStatus(contractId);
    }

    /**
     * Obtiene todos los estados disponibles
     */
    @GetMapping("/all-statuses")
    public ResponseEntity<Message> getAllContractStatuses() {
        return ResponseEntity.ok(new Message(
            ContractStatus.values(),
            "Todos los estados de contratos obtenidos exitosamente",
            com.example.integradora_trackontract.utils.TypesResponse.SUCCESS
        ));
    }

    /**
     * Obtiene información detallada de un estado específico
     */
    @GetMapping("/status-info/{status}")
    public ResponseEntity<Message> getStatusInfo(@PathVariable ContractStatus status) {
        var statusInfo = new java.util.HashMap<String, Object>();
        statusInfo.put("status", status);
        statusInfo.put("displayName", status.getDisplayName());
        statusInfo.put("description", status.getDescription());
        statusInfo.put("isFinal", status.isFinal());
        statusInfo.put("allowsEditing", status.allowsEditing());
        statusInfo.put("requiresApproval", status.requiresApproval());
        statusInfo.put("validTransitions", status.getValidTransitions());

        return ResponseEntity.ok(new Message(
            statusInfo,
            "Información del estado obtenida exitosamente",
            com.example.integradora_trackontract.utils.TypesResponse.SUCCESS
        ));
    }
}
