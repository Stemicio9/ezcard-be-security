package com.metra.ezcardbesecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "userez")
public class UserEz implements Serializable {

    private String id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Set<Authority> authorities;

}
