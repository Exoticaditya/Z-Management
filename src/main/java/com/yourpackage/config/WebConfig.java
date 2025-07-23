package com.yourpackage.config; // <-- Change this to your actual package

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS configuration for all /api/** endpoints
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:8080",   // If you serve frontend from backend
                    "http://localhost:5500",   // If you use a separate dev server (adjust as needed)
                    "http://127.0.0.1:5500"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // (Optional) Serve static resources from /static, /public, /resources, /META-INF/resources
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Uncomment and adjust if you want to serve static files from a custom directory
        // registry.addResourceHandler("/static/**")
        //         .addResourceLocations("classpath:/static/");
    }

    // (Optional) If you use a SPA (React/Angular/Vue) and want to support client-side routing
    // Uncomment this if you want all non-API requests to go to index.html
    /*
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css)$}")
                .setViewName("forward:/index.html");
    }
    */

    // (Optional) If you want to customize JSON serialization (e.g., date/time format)
    // Uncomment and add dependency for Jackson2ObjectMapperBuilderCustomizer if needed
    /*
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
    */
} 