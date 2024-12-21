package org.apache.storm.metric.micrometer.reporter.config;

public class JmxConfig implements io.micrometer.jmx.JmxConfig {
    @Override
    public String get(String key) {
        return "";
    }
}
