package com.metra.ezcardbesecurity.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactContainer {
    private String name;
    private String value;
    private String callToAction;
}
