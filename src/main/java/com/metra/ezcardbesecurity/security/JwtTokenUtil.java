package com.metra.ezcardbesecurity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.exception.AuthenticationFailedException;
import com.metra.ezcardbesecurity.exception.TokenNotRefreshedException;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "iat";
    static final String CLAIM_KEY_AUTHORITIES = "roles";
    static final String CLAIM_KEY_IS_ENABLED = "isEnabled";
    static final String CLAIM_KEY_ID = "id";
    static final String CLAIM_BLOCKED_AT = "blockedAt";
    static final String CLAIM_NAME = "name";

    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserEzRepository userRepository;

    @Value("${jwt.expiration}")
    private Long expiration;

    public Authentication getAuthentication(JwtAuthenticationRequest authenticationRequest) throws AuthenticationFailedException {
        try {
            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        } catch (AuthenticationException authenticationException) {
            throw new AuthenticationFailedException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }

    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    @SuppressWarnings("unchecked")
    public JwtUser getUserDetails(String token) {

        if (token == null) {
            return null;
        }
        try {
            final Claims claims = getClaimsFromToken(token);
            List<SimpleGrantedAuthority> authorities = null;
            if (claims.get(CLAIM_KEY_AUTHORITIES) != null) {
                authorities = ((List<String>) claims.get(CLAIM_KEY_AUTHORITIES)).stream()
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            }

            return new JwtUser(claims.getSubject(), "", authorities, (boolean) claims.get(CLAIM_KEY_IS_ENABLED));
        } catch (Exception e) {
            return null;
        }

    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expire;
        try {
            final Claims claims = getClaimsFromToken(token);
            expire = claims.getExpiration();
        } catch (Exception e) {
            expire = null;
        }
        return expire;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = getClaimsFromToken(token);
            audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    public Claims getClaimsFromToken(String token) throws TokenNotRefreshedException {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            //TODO: rendere più esplicativo l'errore
            throw new TokenNotRefreshedException("Error refreshing token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return claims;
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expire = getExpirationDateFromToken(token);
        return expire.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        List<String> auth = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(CLAIM_KEY_AUTHORITIES, auth);
        claims.put(CLAIM_KEY_IS_ENABLED, userDetails.isEnabled());
        Optional<UserEz> user = userRepository.findByUsername(userDetails.getUsername());
        user.ifPresent(userEz -> claims.put(CLAIM_KEY_ID, userEz.getId()));
        return generateToken(claims);
    }

    @SuppressWarnings("unused")
    String generateToken(Map<String, Object> claims) {
        ObjectMapper mapper = new ObjectMapper();

        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    @SuppressWarnings("unused")
    public Boolean canTokenBeRefreshed(String token) {
        final Date created = getCreatedDateFromToken(token);
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) throws TokenNotRefreshedException {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            //TODO: rendere più esplicativo l'errore
            throw new TokenNotRefreshedException("Error refreshing token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

}
