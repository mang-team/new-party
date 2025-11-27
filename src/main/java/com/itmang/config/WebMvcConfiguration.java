package com.itmang.config;



import com.itmang.intercepor.JwtTokenAdminInterceptor;
import com.itmang.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;


    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        //log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/user/**")

                .addPathPatterns("/admin/**")//新增管理接口
                .addPathPatterns("/activity/**")
                .addPathPatterns("/action/**")
                .addPathPatterns("/study/**")
                .addPathPatterns("/party/**")
                //放行swagger3文档路径
                .excludePathPatterns("/swagger**/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/doc.html")
                //放行登录注册路径
                .excludePathPatterns("/user/user/login")

                .excludePathPatterns("/admin/user/login")//新增放行管理端登录接口

                .excludePathPatterns("/user/user/register")
                .excludePathPatterns("/user/user/logout")//新增登出接口
                .excludePathPatterns("/**/OPTIONS");

    }

//    /**
//     * 通过knife4j生成接口文档
//     * @return
//     */
//    @Bean
//    public Docket docketAdmin() {
//        ApiInfo apiInfo = new ApiInfoBuilder()
//                .title("学生管理系统接口文档")
//                .version("2.0")
//                .description("学生管理系统接口文档")
//                .build();
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.itmang.controller"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
//


    /**
     * 设置静态资源映射
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始配置静态资源映射...");
        //映射swagger3文档的静态资源
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     *  设置消息转换器(扩展Spring MVC的消息转换器)
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //log.info("开始扩展消息转换器...");
        MappingJackson2HttpMessageConverter converter= new MappingJackson2HttpMessageConverter();
        //注意这里导包一定要导对，包名为：org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
        converter.setObjectMapper(new JacksonObjectMapper());
        //添加
        converters.add(0,converter);//将自定义的消息扩展器移到首位
    }
}









