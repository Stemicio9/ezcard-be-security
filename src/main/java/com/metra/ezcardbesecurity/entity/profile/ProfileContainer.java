package com.metra.ezcardbesecurity.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileContainer {

    private String firstName;
    private String lastName;
    private String role;
    private String description;
}
