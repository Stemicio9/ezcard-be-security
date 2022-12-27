package com.metra.ezcardbesecurity.controller;

import com.metra.ezcardbesecurity.qrcodecreator.QRCodeGenerator;
import com.metra.ezcardbesecurity.qrcodecreator.QrCodeContainer;
import com.metra.ezcardbesecurity.service.UserEzService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class QrCodeController {

    private final UserEzService userEzService;

    public QrCodeController(UserEzService userEzService) {
        this.userEzService = userEzService;
    }

    @GetMapping(value = "protected/qrcode/{username}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getQrCode(@PathVariable String username) {
        try{
            String text = userEzService.generateLinkForQrCode(username);
            return QRCodeGenerator.getQRCodeImage(text, 512, 512);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[1];
        }

    }

    @PostMapping(value = "public/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getQrCode(@RequestBody QrCodeContainer container) {
        try{
            return QRCodeGenerator.getQRCodeImage(container.getText(), container.getWidth(), container.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[1];
        }
    }

}