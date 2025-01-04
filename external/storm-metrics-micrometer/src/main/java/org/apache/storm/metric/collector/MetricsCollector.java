package org.apache.storm.metric.collector;

import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;

import java.util.Map;

public interface MetricsCollector extends Collector {
    /**
     * Prepare the collector with necessary configurations and resources.
     *
     * @param daemonConf the configuration for the collector
     */
    void prepare(StormMetricsPersister stormMetricsPersister, Map<String, Object> daemonConf);


}
