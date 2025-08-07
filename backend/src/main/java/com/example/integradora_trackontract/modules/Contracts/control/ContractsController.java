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

    @GetMapping("/all/status/true")
    public ResponseEntity<Message> getAllContractsByActiveStatus() {
        return contractsService.findAllByStatusIsTrue();
    }

}