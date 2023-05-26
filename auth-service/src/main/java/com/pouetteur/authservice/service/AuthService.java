package com.pouetteur.authservice.service;

import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsManager {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(UserDetails user) {
        this.userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        this.userRepository.save((User) user);
    }

    @Override
    public void deleteUser(String username) {
        this.userRepository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        User user = (User) this.loadUserByUsername(username);
        user.setPassword(newPassword);
        this.userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return this.userRepository.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
