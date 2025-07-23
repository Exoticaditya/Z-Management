package com.zplus.adminpanel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve admin panel static files
        registry.addResourceHandler("/admin/**")
                .addResourceLocations(
                    "classpath:/static/admin/",
                    "classpath:/static/"
                )
                .setCachePeriod(0);

        // Serve login page static files
        registry.addResourceHandler("/index/**")
                .addResourceLocations("classpath:/static/index/")
                .setCachePeriod(0);

        // Serve common static resources (CSS, JS, images)
        registry.addResourceHandler(
                "/css/**",
                "/js/**",
                "/assets/**",
                "/images/**"
            )
            .addResourceLocations(
                "classpath:/static/css/",
                "classpath:/static/js/",
                "classpath:/static/assets/",
                "classpath:/static/images/"
            )
            .setCachePeriod(0);

        // Serve root static files
        registry.addResourceHandler("/**")
                .addResourceLocations(
                    "classpath:/static/",
                    "classpath:/public/",
                    "classpath:/resources/"
                )
                .setCachePeriod(0);
    }
}