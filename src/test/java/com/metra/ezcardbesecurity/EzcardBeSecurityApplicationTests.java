package com.metra.ezcardbesecurity;

import com.metra.ezcardbesecurity.entity.Authority;
import com.metra.ezcardbesecurity.entity.AuthorityName;
import com.metra.ezcardbesecurity.respository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EzcardBeSecurityApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    AuthorityRepository authorityRepository;


    @Test
    public void createAuthorities() {
        Authority authority = new Authority();
        authority.setName(AuthorityName.ROLE_USER);
        authorityRepository.save(authority);

        Authority authority1 = new Authority();
        authority1.setName(AuthorityName.ROLE_ADMIN);
        authorityRepository.save(authority1);
    }

}
