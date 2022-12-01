package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.response.JwtAuthenticationResponse;
import com.metra.ezcardbesecurity.security.JwtAuthenticationRequest;
import com.metra.ezcardbesecurity.security.JwtTokenUtil;
import com.metra.ezcardbesecurity.service.UserEzService;
import com.metra.ezcardbesecurity.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserEzService userEzService;

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
                () -> userEzService.findAll()
        );
    }
}
