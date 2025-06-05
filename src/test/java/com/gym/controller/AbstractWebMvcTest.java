package com.gym.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gym.config.WebConfig;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class })
public abstract class AbstractWebMvcTest {
    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
}