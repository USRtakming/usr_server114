package com.usr_server.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 允許所有路徑
                .allowedOrigins("https://usr.takming.edu.tw","http://localhost:3001","http://localhost:81")  // 允許的來源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允許的方法
                .allowedHeaders("*")  // 允許的標頭
                .allowCredentials(true);  // 是否允許攜帶認證
    }
}
