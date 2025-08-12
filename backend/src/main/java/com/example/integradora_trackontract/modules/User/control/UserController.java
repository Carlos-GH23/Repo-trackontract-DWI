package com.example.integradora_trackontract.modules.User.control;

import com.example.integradora_trackontract.modules.Contracts.control.ContractsService;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsDTO; /*Me marca error en esta importacion*/
import com.example.integradora_trackontract.modules.User.model.ChangePasswordRequest;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserDTO;
import com.example.integradora_trackontract.modules.User.model.UserProfileDTO;
import com.example.integradora_trackontract.utils.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveUsers(@Validated(UserDTO.Register.class) @RequestBody UserDTO dto) {
        return userService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateUsers(@Validated(UserDTO.Modify.class) @RequestBody UserDTO dto) {
        return userService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(UserDTO.ChangeStatus.class) @RequestBody UserDTO dto) {
        return userService.changeStatus(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteById(@PathVariable Long id) {
        return userService.delete(id);
    }

    @GetMapping("/all/status/false")
    public List<User> getAllUsersByInactiveStatus() {
        return userService.findAllByStatusIsFalse(true);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Message> getUsersById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/all/status/true")
    public ResponseEntity<Message> getAllUsersByActiveStatus() {
        return userService.findAllByStatusIsTrue();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> me(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserProfileDTO dto = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }

    //Cambio de contraseña
    @PutMapping("/me/password")
    public ResponseEntity<Message> changeMyPassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails ud,
            @Validated @RequestBody ChangePasswordRequest body) {
        return userService.changeMyPassword(ud.getUsername(), body);
    }
}
