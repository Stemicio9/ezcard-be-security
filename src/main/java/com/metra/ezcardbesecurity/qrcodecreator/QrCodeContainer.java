package com.metra.ezcardbesecurity.qrcodecreator;


import lombok.Data;

@Data
public class QrCodeContainer {

    private String text;
    private int width;
    private int height;
}