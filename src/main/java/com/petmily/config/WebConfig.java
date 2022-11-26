package com.petmily.config;

import com.petmily.config.formatter.AccountNumberFormatter;
import com.petmily.config.formatter.BoardTypeFormatter;
import com.petmily.config.formatter.EmailFormatter;
import com.petmily.config.formatter.PhoneNumberFormatter;
import com.petmily.config.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MessageSource ms;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new BoardTypeFormatter());
        registry.addFormatter(new PhoneNumberFormatter(ms));
        registry.addFormatter(new EmailFormatter());
        registry.addFormatter(new AccountNumberFormatter(ms));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**/auth/**");
    }
}
