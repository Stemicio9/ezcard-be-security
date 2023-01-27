package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.*;
import com.metra.ezcardbesecurity.request.ForgotPasswordRequest;
import com.metra.ezcardbesecurity.request.ResetPasswordRequest;
import com.metra.ezcardbesecurity.respository.TokenRepository;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import com.metra.ezcardbesecurity.security.JwtAuthenticationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserEzService {

    @Value("${ezprofile.url}")
    private String ezProfileUrl;

    private final UserEzRepository userEzRepository;
    private final TokenRepository tokenRepository;
    private final ProfileService profileService;

    private final long tokenExpirationTime = 5; //in hours
    final PasswordEncoder passwordEncoder;

    public UserEzService(UserEzRepository userEzRepository, TokenRepository tokenRepository, ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.userEzRepository = userEzRepository;
        this.tokenRepository = tokenRepository;
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
        userEz.setGifted(updateEzRequest.isGifted() ? updateEzRequest.isGifted() : false);
        userEz.setEnabled(true);
        profileService.insertProfile(updateEzRequest.getUsername());
        return userEzRepository.save(userEz);
    }

    public String generateLinkForQrCode(String username) {
        String id = profileService.getIdProfileFromUsername(username);
        return ezProfileUrl + "/profile?id=" + id;
    }

    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Optional<UserEz> opt = userEzRepository.findByEmail(email);
        if (opt.isPresent()) {
            UserEz userEz = opt.get();
            Token token = generateToken(userEz.getId(), TokenType.RESET_PASSWORD);
            sendEmailForResetPassword(userEz, token);
            return "Password changed";
        } else {
            log.error("User not found for email {}", email);
            return "User not found";
        }

    }

    public boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        Optional<Token> opt = tokenRepository.findByToken(token);
        if (opt.isPresent()) {
            Token tokenObj = opt.get();
            if (tokenObj.getType().equals(TokenType.RESET_PASSWORD)) {
                if (tokenObj.getExpirationDate().isAfter(LocalDateTime.now())) {
                    Optional<UserEz> optUser = userEzRepository.findById(tokenObj.getUserId());
                    if (optUser.isPresent()) {
                        UserEz userEz = optUser.get();
                        userEz.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
                        userEzRepository.save(userEz);
                        tokenRepository.delete(tokenObj);
                        return true;
                    } else {
                        log.error("User not found for token {}", token);
                        return false;
                    }
                } else {
                    log.error("Token expired for token {}", token);
                    return false;
                }
            } else {
                log.error("Token not valid for token {}", token);
                return false;
            }
        } else {
            log.error("Token not found for token {}", token);
            return false;
        }
    }

    private void sendEmailForResetPassword(UserEz userEz, Token token) {
        String link = generateLinkForResetPassword(token.getToken());
        //TODO send email

    }

    private String generateLinkForResetPassword(String token) {
        return ezProfileUrl + "/reset-password?token=" + token;
    }

    private Token generateToken(String userId, TokenType resetPassword){
        Token token = new Token();
        token.setUserId(userId);
        token.setType(resetPassword);
        token.setToken(UUID.randomUUID().toString());
        //set expiration date 5 hours from now
        token.setExpirationDate(LocalDateTime.now().plusHours(tokenExpirationTime));
        return tokenRepository.save(token);
    }


}
