package com.fiap.tech_challenge_backend.cucumber.hooks;

import com.fiap.tech_challenge_backend.cucumber.config.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    private final TestContext testContext;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void before() {
        logger.info("Iniciando cenário de teste");
        testContext.clear();
    }

    @After
    public void after() {
        logger.info("Finalizando cenário de teste");
        testContext.clear();
    }
}
