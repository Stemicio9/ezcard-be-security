package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import com.metra.ezcardbesecurity.security.JwtUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserEzRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEz> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(userOptional.get());
        }
    }
}
