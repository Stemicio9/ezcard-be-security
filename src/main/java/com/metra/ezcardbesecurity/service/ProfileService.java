package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.entity.UserEz;
import com.metra.ezcardbesecurity.entity.profile.*;
import com.metra.ezcardbesecurity.respository.ProfileRepository;
import com.metra.ezcardbesecurity.respository.UserEzRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProfileService {
    private final UserEzRepository userEzRepository;

    private final ProfileRepository profileRepository;
    private final FtpService ftpService;

    public ProfileService(ProfileRepository profileRepository, FtpService ftpService,
                          UserEzRepository userEzRepository) {
        this.profileRepository = profileRepository;
        this.ftpService = ftpService;
        this.userEzRepository = userEzRepository;
    }

    public Profile insertProfile(String username) {
        //check if profile exists by username
        if (profileRepository.findByUsername(username).isPresent()) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            Profile profile = new Profile();
            profile = initializeProfile(profile, username);

            log.info("Creating profile for user {}", profile.getUsername());
            return profileRepository.insert(profile);
        }
    }

    public Profile updateProfile(ProfileContainer profileContainer, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            profile.setProfileContainer(profileContainer);
            log.info("Profile for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateSocial(List<SocialContainer> socialContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            List<SocialContainer> socialContainers = socialContainerList.stream().filter(socialContainer -> !(StringUtils.isBlank(socialContainer.getValue()))).collect(Collectors.toList());
            profile.setSocials(socialContainers);
            log.info("Social for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateContacts(List<ContactContainer> contactContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            List<ContactContainer> contactContainers = contactContainerList.stream().filter(contactContainer -> !(StringUtils.isBlank(contactContainer.getValue()))).collect(Collectors.toList());
            profile.setContacts(contactContainers);
            log.info("Contacts for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public Profile updateCompanies(List<CompanyContainer> companyContainerList, String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            List<CompanyContainer> companyContainers = companyContainerList.stream().peek(
                    companyContainer -> companyContainer.setSocials(companyContainer.getSocials().stream().filter(socialContainer -> !(StringUtils.isBlank(socialContainer.getValue()))).collect(Collectors.toList()))
            ).collect(Collectors.toList());
            profile.setCompanies(companyContainers);
            log.info("Companies for user {} updated", username);
            return profileRepository.save(profile);
        }
    }

    public ProfileContainer getProfile(String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return null;
        } else {
            log.info("Profile for user {} retrieved", username);
            return profile.getProfileContainer() != null ? profile.getProfileContainer() : new ProfileContainer();
        }
    }

    public List<SocialContainer> getSocial(String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return Collections.emptyList();
        } else {
            log.info("Social for user {} retrieved", username);
            return profile.getSocials();
        }
    }

    public List<ContactContainer> getContacts(String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return Collections.emptyList();
        } else {
            log.info("Contacts for user {} retrieved", username);
            return profile.getContacts();
        }
    }

    public List<CompanyContainer> getCompanies(String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return Collections.emptyList();
        } else {
            log.info("Companies for user {} retrieved", username);
            return profile.getCompanies();
        }
    }

    public List<MediaContainer> updateMedia(MultipartFile[] files, String username, String type) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return Collections.emptyList();
        } else {
            List<MediaContainer> mediaContainerResponse = new ArrayList<>();
            if (files != null && files.length > 0) {
                try {
                    List<String> links = ftpService.uploadFiles(files, profile.getId(), type);
                    for (int i = 0; i < files.length; i++) {
                        MediaContainer mediaContainer = new MediaContainer();
                        mediaContainer.setName(files[i].getOriginalFilename());
                        mediaContainer.setType(files[i].getContentType());
                        mediaContainer.setSize(String.valueOf(files[i].getSize()));
                        mediaContainer.setLink(links.get(i));
                        mediaContainerResponse.add(mediaContainer);
                    }
                } catch (Exception e) {
                    log.error("Error updating gallery for user {}", username);
                    profile = setMedia(username, type, profile, mediaContainerResponse);
                    profileRepository.save(profile);
                    return Collections.emptyList();
                }
            }
            profile = setMedia(username, type, profile, mediaContainerResponse);
            log.info("Updating Media for user {} updated", username);
            profileRepository.save(profile);
            return profile.getGallery();

        }
    }

    public List<MediaContainer> getMedia(String username, String type) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error(profileNotFoundErrorMessage(username));
            return Collections.emptyList();
        } else {
            switch (type) {
                case "gallery":
                    log.info("Gallery for user {} retrieved", username);
                    return profile.getGallery();
                case "presentation":
                    log.info("Presentation for user {} retrieved", username);
                    return profile.getPresentation();
                case "partner":
                    log.info("Partner for user {} retrieved", username);
                    return profile.getPartner();
                case "profile":
                    log.info("Profile image for user {} retrieved", username);
                    return profile.getProfileImage() != null ? Collections.singletonList(profile.getProfileImage()) : Collections.emptyList();
                case "cover":
                    log.info("Cover image for user {} retrieved", username);
                    return profile.getCoverImage() != null ? Collections.singletonList(profile.getCoverImage()) : Collections.emptyList();
                default:
                    log.error("Invalid media type {}", type);
                    return null;
            }

        }
    }

    public Profile getProfileShown(String id) {
        //TODO: abbiamo usato l'id al posto di username per la prima ristampa di ezcard
        Profile profile = profileRepository.findByIdLink(id).orElse(null);
        if (profile == null) {
            log.error("Profile for user with id {} does not exist", id);
            return null;
        } else {
            log.info("Profile for user with id {} retrieved", id);
            return profile;
        }
    }


    private String profileNotFoundErrorMessage(String username) {
        return "Profile for user " + username + " does not exist";
    }

    private Profile initializeProfile(Profile profile, String username) {
        profile.setUsername(username);
        profile.setProfileContainer(new ProfileContainer());
        profile.setSocials(new ArrayList<>());
        profile.setContacts(new ArrayList<>());
        profile.setCompanies(new ArrayList<>());
        profile.setGallery(new ArrayList<>());
        profile.setPartner(new ArrayList<>());
        profile.setPresentation(new ArrayList<>());
        profile.setIdLink(generateUniqueID());
        return profile;
    }

    private String generateUniqueID() {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(10000));
        while (profileRepository.findByIdLink(id).isPresent()) {
            id = String.format("%04d", random.nextInt(10000));
        }
        return id;
    }

    private Profile setMedia(String username, String type, Profile profile, List<MediaContainer> mediaContainerResponse) {
        switch (type) {
            case "gallery":
                profile.setGallery(mediaContainerResponse);
                break;
            case "presentation":
                profile.setPresentation(mediaContainerResponse);
                break;
            case "partner":
                profile.setPartner(mediaContainerResponse);
                break;
            case "profile":
                profile.setProfileImage(mediaContainerResponse.get(0));
                break;
            case "cover":
                profile.setCoverImage(mediaContainerResponse.get(0));
                break;
            default:
                log.error("Error updating media for user {}", username);
        }
        return profile;
    }


    public UserEz changeUserStatus(String id) {
        UserEz userEz = userEzRepository.findById(id).orElse(null);
        if (userEz == null) {
            log.error("Profile for user with id {} does not exist", id);
            return null;
        } else {
            userEz.setEnabled(!userEz.isEnabled());
            userEzRepository.save(userEz);
            log.info("Profile for user with id {} updated", id);
            return userEz;
        }
    }

    public String getIdProfileFromUsername(String username) {
        Profile profile = profileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            log.error("Profile for user with username {} does not exist", username);
            return null;
        } else {
            log.info("Profile for user with username {} retrieved", username);
            return profile.getIdLink();
        }
    }
}
