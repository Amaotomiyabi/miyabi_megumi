package com.megumi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ResourceBundle;

@Configuration
public class CustomMvcConfig implements WebMvcConfigurer {

    public static final String PHOTO_REQUEST_PATH;
    public static final String PHOTO_MAPPING_PATH = "/photo/map/";

    static {
        var path2 = ResourceBundle.getBundle("server").getString("photo.request.path");
        if (path2.endsWith("/")) {
            PHOTO_REQUEST_PATH = path2.substring(0, path2.length() - 1) + PHOTO_MAPPING_PATH;
        } else {
            PHOTO_REQUEST_PATH = path2 + PHOTO_MAPPING_PATH;
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var separator = File.separator;
        registry.addResourceHandler(PHOTO_MAPPING_PATH + "**").addResourceLocations("file:" + CommonConfig.PHOTO_SAVE_PATH + separator);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

}
