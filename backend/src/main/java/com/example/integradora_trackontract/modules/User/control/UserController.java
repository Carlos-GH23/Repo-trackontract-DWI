package com.example.integradora_trackontract.modules.User.control;

import com.example.integradora_trackontract.modules.User.model.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> me(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserProfileDTO dto = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }
}
