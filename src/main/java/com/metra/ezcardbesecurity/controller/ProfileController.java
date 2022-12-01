package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.entity.profile.CompanyContainer;
import com.metra.ezcardbesecurity.entity.profile.ContactContainer;
import com.metra.ezcardbesecurity.entity.profile.ProfileContainer;
import com.metra.ezcardbesecurity.entity.profile.SocialContainer;
import com.metra.ezcardbesecurity.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/protected/profile")
public class ProfileController {


    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    //TODO presentation, gallery, partner

    @PostMapping("/update/profile")
    public ResponseEntity updateProfile(Authentication authentication, @RequestBody ProfileContainer profile) {
        return ResponseEntity.ok(profileService.updateProfile(profile, authentication.getName()));
    }

    @PostMapping("/update/social")
    public ResponseEntity updateSocial(Authentication authentication, @RequestBody List<SocialContainer> socialContainerList) {
        return ResponseEntity.ok(profileService.updateSocial(socialContainerList, authentication.getName()));
    }

    @PostMapping("/update/contacts")
    public ResponseEntity updateContacts(Authentication authentication, @RequestBody List<ContactContainer> contactContainerList) {
        return ResponseEntity.ok(profileService.updateContacts(contactContainerList, authentication.getName()));
    }
    @PostMapping("/update/company")
    public ResponseEntity updateCompanies(Authentication authentication, @RequestBody List<CompanyContainer> companyContainers) {
        return ResponseEntity.ok(profileService.updateCompanies(companyContainers, authentication.getName()));
    }



    @PostMapping("/get/profile")
    public ResponseEntity getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(authentication.getName()));
    }

    @PostMapping("/get/social")
    public ResponseEntity getSocial(Authentication authentication) {
        return ResponseEntity.ok(profileService.getSocial(authentication.getName()));
    }

    @PostMapping("/get/contacts")
    public ResponseEntity getContacts(Authentication authentication) {
        return ResponseEntity.ok(profileService.getContacts(authentication.getName()));
    }
    @PostMapping("/get/company")
    public ResponseEntity getCompanies(Authentication authentication) {
        return ResponseEntity.ok(profileService.getCompanies(authentication.getName()));
    }


}
