package com.itmang.config;//package com.itmang.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//// 关键
//@EnableOpenApi
//public class SwaggerConfig {
//
//    @Bean
//    public Docket createRestAPI() {
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.itmang.controller"))//映射的包路径
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("学生管理系统接口文档")
//                .description("学生管理系统接口文档")
////                .contact(new Contact("xxUser", "#", "xxUser@qq.com"))
//                .version("1.0")
//                .build();
//    }
//}