package com.example.integradora_trackontract.modules.User.control;

import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserProfileDTO;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public UserProfileDTO getProfile(String email) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isStatus(),
                user.getRol_id().getName()
        );
    }
}
