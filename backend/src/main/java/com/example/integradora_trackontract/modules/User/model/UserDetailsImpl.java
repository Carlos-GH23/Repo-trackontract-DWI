package com.example.integradora_trackontract.modules.User.model;
import com.example.integradora_trackontract.modules.User.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRol_id() != null
                ? user.getRol_id().getAuthorities()
                : null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 🔹 Aquí devolvemos el email para que auth.getName() lo recupere
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isStatus(); }

    public User getUser() {
        return this.user;
    }
}
