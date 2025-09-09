package com.jwt.jwt.service;


import com.jwt.jwt.entity.LoginAttempt;
import com.jwt.jwt.enumeration.Role;
import com.jwt.jwt.exception.ReachedLimitAttemptsException;
import com.jwt.jwt.repository.LoginAttemptRepository;
import com.jwt.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.jwt.jwt.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final LoginAttemptRepository loginAttemptRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User with this email/ username : "+usernameOrEmail+" not found!"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    public int getLoginAttempts(String username){
        return loginAttemptRepository.findByUsername(username)
                .map(LoginAttempt::getAttempts)
                .orElse(0);
    }

    public void incrementAttempts(String username){
        if(username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username mustn't be null or empty!");
        }
        LoginAttempt loginAttempt = loginAttemptRepository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .attempts(0)
                        .lastAttempt(LocalDateTime.now())
                        .build());
        int updatedAttempts = loginAttempt.getAttempts() + 1;

        loginAttempt.setAttempts(updatedAttempts);
        loginAttempt.setLastAttempt(LocalDateTime.now());

        if(updatedAttempts == 5){
            loginAttempt.setLockedUntil(LocalDateTime.now().plusMinutes(5));
            loginAttemptRepository.save(loginAttempt);
            throw new ReachedLimitAttemptsException(username);
        }
        if(updatedAttempts < 5)
            loginAttemptRepository.save(loginAttempt);
    }

    public void resetAttempts(String username){
        loginAttemptRepository.findByUsername(username)
                .ifPresent(attempt -> {
                    attempt.setAttempts(0);
                    attempt.setLockedUntil(null);
                    loginAttemptRepository.save(attempt);
                });
    }

    public void validateLoginAttempt(String username){
        LoginAttempt loginAttempt = loginAttemptRepository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .attempts(0)
                        .lockedUntil(null)
                        .lastAttempt(LocalDateTime.now())
                        .build());
        if(loginAttempt.getLockedUntil() != null && loginAttempt.getLockedUntil().isAfter(LocalDateTime.now())){
            throw new ReachedLimitAttemptsException(username);
        }else if(loginAttempt.getLockedUntil() != null && loginAttempt.getLockedUntil().isBefore(LocalDateTime.now())){

            loginAttemptRepository.delete(loginAttempt);
        }
    }

    public void clearExpiredLockedUntil(){
        List<LoginAttempt> expired = loginAttemptRepository.findAll()
                .stream()
                .filter(attempt -> attempt.getLockedUntil() != null && attempt.getLockedUntil().isBefore(LocalDateTime.now()))
                .toList();
        loginAttemptRepository.deleteAll(expired);
    }


}
