package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.entity.profile.*;
import com.metra.ezcardbesecurity.service.FtpService;
import com.metra.ezcardbesecurity.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/protected/profile")
public class ProfileController {


    private final ProfileService profileService;
    private final FtpService ftpService;

    public ProfileController(ProfileService profileService, FtpService ftpService) {
        this.profileService = profileService;
        this.ftpService = ftpService;
    }

    @PostMapping("/update/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody ProfileContainer profile) {
        return ResponseEntity.ok(profileService.updateProfile(profile, authentication.getName()));
    }

    @PostMapping("/update/social")
    public ResponseEntity<?> updateSocial(Authentication authentication, @RequestBody List<SocialContainer> socialContainerList) {
        return ResponseEntity.ok(profileService.updateSocial(socialContainerList, authentication.getName()));
    }

    @PostMapping("/update/contacts")
    public ResponseEntity<?> updateContacts(Authentication authentication, @RequestBody List<ContactContainer> contactContainerList) {
        return ResponseEntity.ok(profileService.updateContacts(contactContainerList, authentication.getName()));
    }

    @PostMapping("/update/company")
    public ResponseEntity<?> updateCompanies(Authentication authentication, @RequestBody List<CompanyContainer> companyContainers) {
        return ResponseEntity.ok(profileService.updateCompanies(companyContainers, authentication.getName()));
    }

    @PostMapping(value = "/update/gallery", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateGallery(Authentication authentication, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return ResponseEntity.ok(profileService.updateMedia(files, authentication.getName(), "gallery"));
    }

    @PostMapping(value = "/update/partner", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updatePartner(Authentication authentication, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return ResponseEntity.ok(profileService.updateMedia(files, authentication.getName(), "partner"));
    }

    @PostMapping(value = "/update/presentation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updatePresentation(Authentication authentication, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return ResponseEntity.ok(profileService.updateMedia(files, authentication.getName(), "presentation"));
    }

    @GetMapping("/get/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(authentication.getName()));
    }

    @GetMapping("/get/social")
    public ResponseEntity<?> getSocial(Authentication authentication) {
        return ResponseEntity.ok(profileService.getSocial(authentication.getName()));
    }

    @GetMapping("/get/contacts")
    public ResponseEntity<?> getContacts(Authentication authentication) {
        return ResponseEntity.ok(profileService.getContacts(authentication.getName()));
    }

    @GetMapping("/get/company")
    public ResponseEntity<?> getCompanies(Authentication authentication) {
        return ResponseEntity.ok(profileService.getCompanies(authentication.getName()));
    }

    @GetMapping(value = "/get/gallery")
    public ResponseEntity<?> getGallery(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMedia(authentication.getName(), "gallery"));
    }

    @GetMapping(value = "/get/partner")
    public ResponseEntity<?> getPartner(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMedia(authentication.getName(), "partner"));
    }

    @GetMapping(value = "/get/presentation")
    public ResponseEntity<?> getPresentation(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMedia(authentication.getName(), "presentation"));
    }

    @PostMapping(value = "/get/file")
    public ResponseEntity<?> serveFile(@RequestBody MediaContainer mediaContainer) {
        return ResponseEntity.ok(ftpService.serveFile(mediaContainer.getFileLink()));
    }


}
