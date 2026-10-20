package com.fiap.tech_challenge_backend.cucumber.config;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestContext {
    private MvcResult lastResult;
    private final Map<String, Object> scenario = new HashMap<>();
    private int lastHttpStatus;
    private String lastResponse;

    public MvcResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(MvcResult lastResult) {
        this.lastResult = lastResult;
    }

    public int getLastHttpStatus() {
        return lastHttpStatus;
    }

    public void setLastHttpStatus(int lastHttpStatus) {
        this.lastHttpStatus = lastHttpStatus;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(String lastResponse) {
        this.lastResponse = lastResponse;
    }

    public Map<String, Object> getScenario() {
        return scenario;
    }

    public void put(String key, Object value) {
        scenario.put(key, value);
    }

    public Object get(String key) {
        return scenario.get(key);
    }

    public void clear() {
        scenario.clear();
        lastResult = null;
        lastResponse = null;
        lastHttpStatus = 0;
    }
}
