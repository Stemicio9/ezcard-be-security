package com.metra.ezcardbesecurity.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyContainer {
    private String companyName;
    private String vatNumber;
    private String address;
    private String phoneNumber;
    private String email;
    private String codeSDI;
    private String pec;
    private List<SocialContainer> socials;

}
