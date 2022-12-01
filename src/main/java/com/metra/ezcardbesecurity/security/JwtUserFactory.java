package com.metra.ezcardbesecurity.security;

import com.metra.ezcardbesecurity.entity.Authority;
import com.metra.ezcardbesecurity.entity.UserEz;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(UserEz user) {
        return new JwtUser(user.getUsername(), user.getPassword(), mapToGrantedAuthorities(user.getAuthorities()), user.isEnabled());
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Authority> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
                .collect(Collectors.toList());
    }

}
