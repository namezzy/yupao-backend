package top.withlevi.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域的域名
                .allowedOrigins("http://121.4.39.137","http://localhost:8001","http://localhost:8000","https://usercenter.withlevi.top")
                // 是否允许证书 不在默认开启
                .allowCredentials(true)
                .allowedHeaders("*")
                // 设置允许方法
                .allowedMethods("*")
                // 允许跨域的时间
                .maxAge(3600);
    }
}
