package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.profile.*;
import com.metra.ezcardbesecurity.respository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final FtpService ftpService;

    public ProfileService(ProfileRepository profileRepository, FtpService ftpService) {
        this.profileRepository = profileRepository;
        this.ftpService = ftpService;
    }

    public Profile findByUsername(String username) {
        return profileRepository.findByUsername(username).orElse(null);
    }

    public Profile insertProfile(String username) {
        //check if profile exists by username
        if (profileRepository.findByUsername(username).isPresent()) {
            log.error("Profile for user {} already exists", username);
            return null;
        } else {
            Profile profile = new Profile();
            profile.setUsername(username);
            log.info("Creating profile for user {}", profile.getUsername());
            return profileRepository.insert(profile);
        }
    }

    public Profile updateProfile(ProfileContainer profileContainer, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", username);
            return null;
        } else {
            profile.setProfile(profileContainer);
            log.info("Profile for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateSocial(List<SocialContainer> socialContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", username);
            return null;
        } else {
            profile.setSocials(socialContainerList);
            log.info("Social for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateContacts(List<ContactContainer> contactContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", username);
            return null;
        } else {
            profile.setContacts(contactContainerList);
            log.info("Contacts for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateCompanies(List<CompanyContainer> companyContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", username);
            return null;
        } else {
            profile.setCompanies(companyContainerList);
            log.info("Companies for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public ProfileContainer getProfile(String name) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {
            log.info("Profile for user {} retrieved", name);
            return profile.getProfile();
        }
    }

    public List<SocialContainer> getSocial(String name) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {
            log.info("Social for user {} retrieved", name);
            return profile.getSocials();
        }
    }

    public List<ContactContainer> getContacts(String name) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {
            log.info("Contacts for user {} retrieved", name);
            return profile.getContacts();
        }
    }

    public List<CompanyContainer> getCompanies(String name) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {
            log.info("Companies for user {} retrieved", name);
            return profile.getCompanies();
        }
    }


    public List<MediaContainer> updateMedia(MultipartFile[] files, String name, String type) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {

            try {
                List<MediaContainer> mediaContainerResponse = new ArrayList<>();
                List<String> links = ftpService.uploadFiles(files, profile.getId(), type);

                for (int i = 0; i < files.length; i++) {
                    MediaContainer mediaContainer = new MediaContainer();
                    mediaContainer.setFileName(files[i].getOriginalFilename());
                    mediaContainer.setFileType(files[i].getContentType());
                    mediaContainer.setFileLink(links.get(i));
                }

                profile.setGallery(mediaContainerResponse);
                log.info("Gallery for user {} updated", name);
                profileRepository.save(profile);
                return profile.getGallery();
            } catch (Exception e) {
                log.error("Error updating gallery for user {}", name);
                return Collections.emptyList();
            }
        }
    }

    public List<MediaContainer> getMedia(String name, String type) {
        Profile profile = profileRepository.findByUsername(name).orElse(null);
        if (profile == null) {
            log.error("Profile for user {} does not exist", name);
            return null;
        } else {
            switch (type) {
                case "gallery":
                    log.info("Gallery for user {} retrieved", name);
                    return profile.getGallery();
                case "presentation":
                    log.info("Presentation for user {} retrieved", name);
                    return profile.getPresentation();
                case "partner":
                    log.info("Partner for user {} retrieved", name);
                    return profile.getPartner();
                default:
                    log.error("Invalid media type {}", type);
                    return null;
            }

        }
    }
}
