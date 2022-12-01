package com.metra.ezcardbesecurity.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaContainer {

    private String fileName;
    private String fileType;
    private String fileLink;
}
