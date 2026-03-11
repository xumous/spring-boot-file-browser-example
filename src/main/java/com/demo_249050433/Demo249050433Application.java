package com.demo_249050433;

import com.demo_249050433.controller.FileController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot应用程序主类，负责启动文件共享服务。
 * 包含应用入口、下载目录配置及跨域资源共享(CORS)配置。
 */
@SpringBootApplication
public class Demo249050433Application {
    /**
     * 应用程序入口方法，启动文件共享服务流程。
     * 1. 获取用户指定的下载目录
     * 2. 配置文件控制器的下载路径
     * 3. 输出启动信息及访问地址
     * 4. 启动Spring Boot应用上下文
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        // 获取用户输入的下载目录
        String downloadDir = getUserInput();
        // 设置下载目录
        FileController.setDownloadDir(downloadDir);
        System.out.println("文件共享服务启动...");
        System.out.println("下载目录设置为: " + FileController.DOWNLOAD_DIR);
        System.out.println("访问地址: http://localhost:8080/files/index");
        SpringApplication.run(Demo249050433Application.class, args);
    }

    /**
     * 获取用户输入的目录路径
     */
    private static String getUserInput() {
        return "F:/common_share";//先不获取，免得启动程序还要输入
    }

    /**
     * 创建CORS配置Bean，解决跨域资源访问问题。
     * 允许所有来源、HTTP方法和请求头，支持文件共享服务的跨域调用需求。
     *
     * @return 配置好的WebMvcConfigurer实例
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 配置所有路径的CORS规则，允许跨域请求
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}