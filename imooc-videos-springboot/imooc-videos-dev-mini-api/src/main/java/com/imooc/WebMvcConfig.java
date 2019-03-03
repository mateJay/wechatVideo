package com.imooc;

import com.imooc.interceptor.MyInceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: mate_J
 * @Date: 2018/12/14 9:32
 * @Version 1.0
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:F:/workspace/upload/");
        super.addResourceHandlers(registry);
    }

    /**
     * 注册bean
     * @return
     */
    @Bean
    public MyInceptor myInceptor(){
        return new MyInceptor();
    }
    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient(){
        return new ZKCuratorClient();
    }

    /**
     *ctrl + o 快捷键 重写父类的方法
     * 将写的拦截器注册到配置中
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInceptor()).addPathPatterns("/user/**")
                .addPathPatterns("/video/upload","/video/uploadCover","video/saveComment","/video/userLike","/video/userUnLike")
                                             .addPathPatterns("/bgm/**").excludePathPatterns("/user/queryPublisher");
        super.addInterceptors(registry);
    }
}
