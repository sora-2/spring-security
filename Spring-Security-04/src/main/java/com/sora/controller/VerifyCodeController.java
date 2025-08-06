package com.sora.controller;

import com.google.code.kaptcha.Producer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class VerifyCodeController {

    private Producer producer;

    public VerifyCodeController(Producer producer) {
        this.producer = producer;
    }

    @GetMapping("vc.jpg")
    public String getVerifyCode(HttpSession httpSession) throws IOException {

        String text = producer.createText();
        //redis
        httpSession.setAttribute("kaptcha ", text);
        BufferedImage image = producer.createImage(text);
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", fastByteArrayOutputStream);

        return Base64.encodeBase64String(fastByteArrayOutputStream.toByteArray());
    }
}
