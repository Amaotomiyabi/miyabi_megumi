package com.megumi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ResourceBundle;

@Configuration
public class CustomMvcConfig implements WebMvcConfigurer {

    public static final String IMG_REQUEST_PATH;
    public static final String SMALL_IMG_REQUEST_PATH;
    public static final String IMG_MAPPING_PATH = "/img/map/";
    public static final String SMALL_IMG_MAPPING_PATH = "/small/map/";
    public static final String PHOTO_REQUEST_PATH;
    public static final String PHOTO_MAPPING_PATH = "/photo/map/";

    static {
        var path2 = ResourceBundle.getBundle("server").getString("photo.request.path");
        if (path2.endsWith("/")) {
            PHOTO_REQUEST_PATH = path2.substring(0, path2.length() - 1) + PHOTO_MAPPING_PATH;
        } else {
            PHOTO_REQUEST_PATH = path2 + PHOTO_MAPPING_PATH;
        }
        var path = ResourceBundle.getBundle("server").getString("file.request.path");
        if (path.endsWith("/")) {
            IMG_REQUEST_PATH = path.substring(0, path.length() - 1) + IMG_MAPPING_PATH;
        } else {
            IMG_REQUEST_PATH = path + IMG_MAPPING_PATH;
        }
        var path1 = ResourceBundle.getBundle("server").getString("smallFile.request.path");
        if (path1.endsWith("/")) {
            SMALL_IMG_REQUEST_PATH = path1.substring(0, path.length() - 1) + SMALL_IMG_MAPPING_PATH;
        } else {
            SMALL_IMG_REQUEST_PATH = path1 + SMALL_IMG_MAPPING_PATH;
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var separator = File.separator;
        registry.addResourceHandler(SMALL_IMG_MAPPING_PATH + "**").addResourceLocations("file:" + CommonConfig.IMG_SMALL_SAVE_PATH + separator);
        registry.addResourceHandler(IMG_MAPPING_PATH + "**").addResourceLocations("file:" + CommonConfig.IMG_SAVE_PATH + separator);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

}
