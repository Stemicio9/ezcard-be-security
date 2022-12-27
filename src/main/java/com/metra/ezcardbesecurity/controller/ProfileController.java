package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.entity.profile.*;
import com.metra.ezcardbesecurity.service.FtpService;
import com.metra.ezcardbesecurity.service.ProfileService;
import com.metra.ezcardbesecurity.utils.ProfilePaths;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProfileController {

    private final ProfileService profileService;
    private final FtpService ftpService;

    public ProfileController(ProfileService profileService, FtpService ftpService) {
        this.profileService = profileService;
        this.ftpService = ftpService;
    }

    @PostMapping(ProfilePaths.UPDATE_PROFILE_CONTAINER)
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody ProfileContainer profile) {
        return ResponseEntity.ok(profileService.updateProfile(profile, authentication.getName()));
    }

    @PostMapping(ProfilePaths.UPDATE_SOCIAL)
    public ResponseEntity<?> updateSocial(Authentication authentication, @RequestBody List<SocialContainer> socialContainerList) {
        return ResponseEntity.ok(profileService.updateSocial(socialContainerList, authentication.getName()));
    }

    @PostMapping(ProfilePaths.UPDATE_CONTACTS)
    public ResponseEntity<?> updateContacts(Authentication authentication, @RequestBody List<ContactContainer> contactContainerList) {
        return ResponseEntity.ok(profileService.updateContacts(contactContainerList, authentication.getName()));
    }

    @PostMapping(ProfilePaths.UPDATE_COMPANIES)
    public ResponseEntity<?> updateCompanies(Authentication authentication, @RequestBody List<CompanyContainer> companyContainers) {
        return ResponseEntity.ok(profileService.updateCompanies(companyContainers, authentication.getName()));
    }

    @PostMapping(value = ProfilePaths.UPDATE_MEDIA, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateGallery(Authentication authentication,
                                           @RequestParam(value = "files", required = false) MultipartFile[] files,
                                           @RequestParam(value = "type") String type) {
        return ResponseEntity.ok(profileService.updateMedia(files, authentication.getName(), type));
    }

    @GetMapping(ProfilePaths.GET_PROFILE_CONTAINER)
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(authentication.getName()));
    }

    @GetMapping(ProfilePaths.GET_SOCIAL)
    public ResponseEntity<?> getSocial(Authentication authentication) {
        return ResponseEntity.ok(profileService.getSocial(authentication.getName()));
    }

    @GetMapping(ProfilePaths.GET_CONTACTS)
    public ResponseEntity<?> getContacts(Authentication authentication) {
        return ResponseEntity.ok(profileService.getContacts(authentication.getName()));
    }

    @GetMapping(ProfilePaths.GET_COMPANIES)
    public ResponseEntity<?> getCompanies(Authentication authentication) {
        return ResponseEntity.ok(profileService.getCompanies(authentication.getName()));
    }

    @GetMapping(ProfilePaths.GET_MEDIA)
    public ResponseEntity<?> getMedia(Authentication authentication, @RequestParam(value = "type") String type) {
        return ResponseEntity.ok(profileService.getMedia(authentication.getName(), type));
    }

    @PostMapping(ProfilePaths.SERVE_FILE)
    public ResponseEntity<?> serveFile(@RequestBody MediaContainer mediaContainer) {
        return ResponseEntity.ok(ftpService.serveFile(mediaContainer.getLink()));
    }

    @GetMapping(ProfilePaths.GET_PROFILE_SHOWN)
    public ResponseEntity<?> getProfileShown(@PathVariable("id") String id) {
        return ResponseEntity.ok(profileService.getProfileShown(id));
    }

    @GetMapping(ProfilePaths.CHANGE_USER_STATUS+"/{id}")
    public ResponseEntity<?> changeUserStatus(@PathVariable("id") String id) {
        return ResponseEntity.ok(profileService.changeUserStatus(id));
    }


}
