package com.gym.service;

import java.sql.SQLException;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.gym.config.test.TestAppConfig;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional(rollbackFor = SQLException.class)
public abstract class AbstractServiceTest {    
}
