package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.Authority;
import com.metra.ezcardbesecurity.entity.AuthorityName;
import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import com.metra.ezcardbesecurity.security.JwtAuthenticationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserEzService {

    @Value("${ezprofile.url}")
    private String ezProfileUrl;

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

    private UserEz create(JwtAuthenticationRequest request) {
        log.info("Registering user {}", request.getUsername());
        UserEz userEz = new UserEz();
        userEz.setUsername(request.getUsername());
        userEz.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityName.ROLE_USER));
        userEz.setAuthorities(authorities);
        userEz.setEnabled(true);
        return userEzRepository.save(userEz);
    }


    public UserEz updateUser(UserEz updateEzRequest) {
        Optional<UserEz> opt = userEzRepository.findById(updateEzRequest.getId());
        if (opt.isPresent()) {
            UserEz userEz = opt.get();
            userEz.setUsername(updateEzRequest.getUsername());
            userEz.setEmail(updateEzRequest.getEmail());
            userEz.setAuthorities(updateEzRequest.getAuthorities());
            return userEzRepository.save(userEz);
        }else{
            log.error("User not found");
            return null;
        }
    }

    public UserEz createUser(UserEz updateEzRequest) {
        UserEz userEz = new UserEz();
        userEz.setUsername(updateEzRequest.getUsername());
        userEz.setPassword(passwordEncoder.encode(updateEzRequest.getUsername())); //TODO password as username
        userEz.setEmail(updateEzRequest.getEmail());
        userEz.setAuthorities(updateEzRequest.getAuthorities());
        userEz.setEnabled(true);
        profileService.insertProfile(updateEzRequest.getUsername());
        return userEzRepository.save(userEz);
    }

    public String generateLinkForQrCode(String username) {
        String id = profileService.getIdProfileFromUsername(username);
        return ezProfileUrl + "#/profile?id=" + id;
        //return "http://localhost:8080/public/register?username=" + username;
    }
}
