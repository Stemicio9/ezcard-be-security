package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.Authority;
import com.metra.ezcardbesecurity.entity.AuthorityName;
import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import com.metra.ezcardbesecurity.security.JwtAuthenticationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserEzService {

    private final UserEzRepository userEzRepository;
    private final ProfileService profileService;
    final
    PasswordEncoder passwordEncoder;

    public UserEzService(UserEzRepository userEzRepository, ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.userEzRepository = userEzRepository;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEz> findAll() {
        return userEzRepository.findAll();
    }

    public void register(JwtAuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        create(authenticationRequest);
        log.info("User {} registered", username);
        profileService.insertProfile(username);
        log.info("Profile for user {} created", username);
    }

    private Object create(JwtAuthenticationRequest request) {
        log.info("Registering user {}", request.getUsername());
        UserEz userEz = new UserEz();
        userEz.setUsername(request.getUsername());
        userEz.setPassword(passwordEncoder.encode(request.getPassword()));
        userEz.setAuthorities(new HashSet<Authority>() {{add(new Authority(AuthorityName.ROLE_USER));}});
        userEz.setEnabled(true);
        return userEzRepository.save(userEz);
    }


}
