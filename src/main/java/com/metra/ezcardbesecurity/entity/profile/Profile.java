package com.metra.ezcardbesecurity.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Profile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String idLink;

    private MediaContainer profileImage;
    private MediaContainer coverImage;
    private ProfileContainer profileContainer;
    private List<SocialContainer> socials;
    private List<ContactContainer> contacts;
    private List<CompanyContainer> companies;
    private List<MediaContainer> gallery;
    private List<MediaContainer> presentation;
    private List<MediaContainer> partner;


}
