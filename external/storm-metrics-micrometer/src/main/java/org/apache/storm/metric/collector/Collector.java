package org.apache.storm.metric.collector;


import java.util.Map;

public interface Collector {
    void prepare(Map<String, Object> daemonConf);

    /**
     * Start the collector to begin collecting metrics.
     */
    void start();

    /**
     * Stop the collector and release resources.
     */
    void stop();
}
