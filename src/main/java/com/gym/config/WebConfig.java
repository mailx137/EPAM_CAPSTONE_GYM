package com.gym.config;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.gym")
public class WebConfig implements WebMvcConfigurer {
    private static final int COOKIE_MAX_AGE = 365 * 10;
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            new Locale("en"),
            new Locale("ru"));

    /*
     * Configures the LocaleResolver to use a CookieLocaleResolver.
     * This resolver will store the user's locale in a cookie
     * with a maximum age of COOKIE_MAX_AGE days.
     * The default locale is set to English.
     * This allows the application to remember the user's locale preference
     * across sessions and requests.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieMaxAge(Duration.ofDays(COOKIE_MAX_AGE));
        return resolver;
    }

    /**
     * Configures the LocaleChangeInterceptor to allow changing the locale via a
     * request parameter.
     * This interceptor will check for a parameter named "lang" and change the
     * locale accordingly.
     * It only allows changing to supported locales defined in SUPPORTED_LOCALES.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws ServletException {
                String newLocale = request.getParameter(getParamName());
                if (newLocale != null && SUPPORTED_LOCALES.stream().anyMatch(l -> l.getLanguage().equals(newLocale))) {
                    return super.preHandle(request, response, handler);
                }
                return true;
            }
        };
    }

    /**
     * Configures the message source for internationalization.
     * This bean will load messages from the messages.properties file located in the
     * classpath.
     * It supports UTF-8 encoding and does not fall back to system locale.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages"); // messages.properties
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    /**
     * Configures the LocalValidatorFactoryBean to use the message source for
     * validation messages.
     * This bean will be used for validating request bodies and form data.
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource()); // подключаем messageSource
        return bean;
    }

    /**
     * Provides a custom validator bean that uses the LocalValidatorFactoryBean.
     * This validator will be used for validating request bodies and form data.
     */
    @Override
    public Validator getValidator() {
        return validator();
    }

    /**
     * Configures the Thymeleaf template engine with the template resolver and
     * dialects.
     * This engine will be used to process Thymeleaf templates in the application.
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

    /**
     * Configures the template resolver for Thymeleaf templates.
     * This resolver will look for templates in the classpath under /templates/ with
     * .html suffix.
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * Configures the Thymeleaf view resolver to use the template engine and set the
     * character encoding.
     * This resolver will handle rendering views using Thymeleaf templates.
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }
}
