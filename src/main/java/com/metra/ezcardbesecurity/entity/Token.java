package com.metra.ezcardbesecurity.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "token")
@Data
public class Token {

    private String id;
    private String token;
    private String userId;
    private LocalDateTime expirationDate;
    private TokenType type;
}
