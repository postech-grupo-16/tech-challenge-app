package com.fiap.tech_challenge_backend.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class ScenarioContext {

    private MvcResult lastResult;
    private int lastStatus;

    public MvcResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(MvcResult lastResult) {
        this.lastResult = lastResult;
        if (lastResult != null) {
            this.lastStatus = lastResult.getResponse().getStatus();
        }
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(int lastStatus) {
        this.lastStatus = lastStatus;
    }

    public void reset() {
        this.lastResult = null;
        this.lastStatus = 0;
    }
}
