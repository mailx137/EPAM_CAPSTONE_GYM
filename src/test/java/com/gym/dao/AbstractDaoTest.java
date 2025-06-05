package com.gym.dao;

import com.gym.config.test.TestAppConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class AbstractDaoTest {
}
