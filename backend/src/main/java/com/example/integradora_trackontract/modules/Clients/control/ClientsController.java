package com.example.integradora_trackontract.modules.Clients.control;

import com.example.integradora_trackontract.modules.Categories.control.CategoriesService;
import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesDTO;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientsController {
    private final ClientsService clientsService;
    @Autowired
    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllClients() {
        return clientsService.findAll();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveClients(@Validated(ClientsDTO.Register.class) @RequestBody ClientsDTO dto) {
        return clientsService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateClients(@Validated(ClientsDTO.Modify.class) @RequestBody ClientsDTO dto) {
        return clientsService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(ClientsDTO.ChangeStatus.class) @RequestBody ClientsDTO dto) {
        return clientsService.changeStatus(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteById(@PathVariable Long id) {
        return clientsService.delete(id);
    }

    @GetMapping("/all/status/false")
    public List<Clients> getAllClientsByInactiveStatus() {
        return clientsService.findAllByStatusIsFalse(true);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Message> getClientsByName(@PathVariable String name) {
        return clientsService.findByName(name);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Message> getClientsById(@PathVariable Long id) {
        return clientsService.findById(id);
    }

    @GetMapping("/all/status/true")
    public ResponseEntity<Message> getAllClientsByActiveStatus() {
        return clientsService.findAllByStatusIsTrue();
    }

    @GetMapping("/me")
    public ResponseEntity<Message> getMyProfile(@RequestParam String email) {
        System.out.println("Endpoint /clients/me llamado con email: " + email);
        return clientsService.getProfileByEmail(email);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<Message> updateMyProfile(@RequestParam String email, @RequestBody ClientsDTO dto) {
        return clientsService.updateProfile(email, dto);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Message> updateMyPassword(@RequestParam String email, @RequestBody java.util.Map<String, String> body) {
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");
        return clientsService.updatePassword(email, newPassword, confirmPassword);
    }

}
