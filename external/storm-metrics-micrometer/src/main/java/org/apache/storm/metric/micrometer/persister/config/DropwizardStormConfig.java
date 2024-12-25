package org.apache.storm.metric.micrometer.persister.config;

import io.micrometer.core.instrument.dropwizard.DropwizardConfig;

public class DropwizardStormConfig implements DropwizardConfig {
    @Override
    public String prefix() {
        return "storm";
    }

    @Override
    public String get(String key) {
        return "";
    }
}
