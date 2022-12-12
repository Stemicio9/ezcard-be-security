package com.metra.ezcardbesecurity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse{

    private byte[] file;
    private String name;
    private String type;
    private String size;

}
