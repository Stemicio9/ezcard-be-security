package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.response.JwtAuthenticationResponse;
import com.metra.ezcardbesecurity.security.JwtAuthenticationRequest;
import com.metra.ezcardbesecurity.security.JwtTokenUtil;
import com.metra.ezcardbesecurity.service.UserEzService;
import com.metra.ezcardbesecurity.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserEzService userEzService;

    public AuthenticationController(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, UserEzService userEzService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userEzService = userEzService;
    }

    @PostMapping(value = "public/login")
    public ResponseEntity<ResponseWrapper> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                                     HttpServletResponse response) {
        return ResponseWrapper.format(
                "logged in",
                () -> {
                    // Effettuo l autenticazione
                    final Authentication authentication = jwtTokenUtil.getAuthentication(authenticationRequest);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Genero Token
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                    final String token = jwtTokenUtil.generateToken(userDetails);

                    response.setHeader(tokenHeader, token);
                    return new JwtAuthenticationResponse(userDetails.getUsername(), userDetails.isEnabled());
                }
        );
    }

    @GetMapping(value = "protected/refresh-token")
    public ResponseEntity<ResponseWrapper> refreshAndGetAuthenticationToken(HttpServletRequest request,
                                                                            HttpServletResponse response) {
        return ResponseWrapper.format(
                "token refreshed",
                () -> {
                    String refreshedToken = jwtTokenUtil.refreshToken(request.getHeader(tokenHeader));
                    response.setHeader(tokenHeader, refreshedToken);
                    return true;
                }
        );
    }

    @PostMapping(value = "public/register")
    public ResponseEntity<ResponseWrapper> register(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        return ResponseWrapper.format(
                "registered",
                () -> {
                    userEzService.register(authenticationRequest);
                    return true;
                }
        );
    }

    @GetMapping(value = "protected/list")
    public ResponseEntity<ResponseWrapper> listUsers() {
        return ResponseWrapper.format(
                "list users",
                userEzService::findAll
        );
    }


    @PostMapping(value = "protected/update-admin")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserEz updateEzRequest){
        return ResponseWrapper.format(
                "update user info",
                ()-> userEzService.updateUser(updateEzRequest)
        );
    }




}
