package org.apache.storm.metric.micrometer.persister.config;

import io.micrometer.prometheusmetrics.PrometheusConfig;

public class PrometheusStormConfig implements PrometheusConfig {
    @Override
    public String prefix() {
        return "storm";
    }

    @Override
    public String get(String key) {
        return "";
    }
}
