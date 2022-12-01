package com.metra.ezcardbesecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "authority")
public class Authority implements Serializable {

    @Id
    private String id;
    private AuthorityName name;


    public Authority(AuthorityName name) {
        this.name = name;
    }

}
