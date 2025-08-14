package com.example.integradora_trackontract.modules.Contracts.control;

import com.example.integradora_trackontract.modules.Clients.control.ClientsService;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsDTO;
import com.example.integradora_trackontract.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.core.io.ByteArrayResource;

@RestController
@RequestMapping("/contracts")
public class ContractsController {

    private final ContractsService contractsService;

    @Autowired
    public ContractsController(ContractsService contractsService) {
        this.contractsService = contractsService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllContracts() {
        return contractsService.findAll();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveContracts(@Validated(ContractsDTO.Register.class) @RequestBody ContractsDTO dto) {
        return contractsService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateContracts(@Validated(ContractsDTO.Modify.class) @RequestBody ContractsDTO dto) {
        return contractsService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(ContractsDTO.ChangeStatus.class) @RequestBody ContractsDTO dto) {
        return contractsService.changeStatus(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteById(@PathVariable Long id) {
        return contractsService.delete(id);
    }

    @GetMapping("/all/status/false")
    public List<Contracts> getAllClientsByInactiveStatus() {
        return contractsService.findAllByStatusIsFalse(true);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Message> getContractsByName(@PathVariable String name) {
        return contractsService.findByName(name);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Message> getContractsById(@PathVariable Long id) {
        return contractsService.findById(id);
    }

    @GetMapping("/abogado/{abogadoId}/id/{id}")
    public ResponseEntity<Message> getContractsByIdWithAbogadoValidation(@PathVariable Long abogadoId, @PathVariable Long id) {
        return contractsService.findByIdWithAbogadoValidation(id, abogadoId);
    }

    @GetMapping("/all/status/true")
    public ResponseEntity<Message> getAllContractsByActiveStatus() {
        return contractsService.findAllByStatusIsTrue();
    }

    @GetMapping("/by-abogado/{abogadoId}")
    public ResponseEntity<Message> getContractsByAbogado(@PathVariable Long abogadoId) {
        return contractsService.findAllByAbogado(abogadoId);
    }

    @GetMapping("/empresas-by-abogado/{abogadoId}")
    public ResponseEntity<Message> getEmpresasByAbogado(@PathVariable Long abogadoId) {
        return contractsService.getEmpresasByAbogado(abogadoId);
    }

    @PostMapping("/{contractId}/accept")
    public ResponseEntity<Message> acceptContract(@PathVariable Long contractId, @RequestParam Long abogadoId) {
        return contractsService.acceptContract(contractId, abogadoId);
    }

    @PostMapping("/{contractId}/reject")
    public ResponseEntity<Message> rejectContract(@PathVariable Long contractId, @RequestParam Long abogadoId, @RequestBody String rejectionReason) {
        return contractsService.rejectContract(contractId, abogadoId, rejectionReason);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Message> getContractsByClient(@PathVariable Long clientId) {
        return contractsService.findAllByClient(clientId);
    }

    @GetMapping("/by-user-email")
    public ResponseEntity<Message> getContractsByUserEmail(@RequestParam String email) {
        return contractsService.findAllByUserEmail(email);
    }

    @GetMapping("/{contractId}/pdf")
    public ResponseEntity<ByteArrayResource> generateContractPDF(@PathVariable Long contractId) {
        return contractsService.generateContractPDF(contractId);
    }

}