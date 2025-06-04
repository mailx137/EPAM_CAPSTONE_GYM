package com.gym.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.gym.config.dev.DevDataSource;
import com.gym.config.test.TestDataSource;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Properties props = new Properties();

        try (var input = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                String activeProfile = props.getProperty("spring.profiles.active");
                if (activeProfile != null) {
                    servletContext.setInitParameter("spring.profiles.active", activeProfile);
                }
            }
        } catch (IOException e) {
            throw new ServletException("Failed to load application.properties", e);
        }

        super.onStartup(servletContext);
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { WebSecurityConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfig.class, DevDataSource.class, TestDataSource.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
                new DelegatingFilterProxy("springSecurityFilterChain"),
                new HiddenHttpMethodFilter()
        };
    }
}