package org.apache.storm.metric.server;

import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;

import java.util.Map;
import java.util.Objects;

public class MetricsServerCollectorImpl extends AbstractMetricsServerCollector {
    private StormMetricsPersister stormMetricsPersister;

    @Override
    public void prepare(StormMetricsPersister stormMetricsPersister, Map<String, Object> daemonConf) {
        super.prepare(stormMetricsPersister, daemonConf);
        this.stormMetricsPersister = stormMetricsPersister;
    }

    @Override
    protected String getMetricsAsText() {
        if (Objects.nonNull(stormMetricsPersister)) {
            return stormMetricsPersister.getMetricsAsText();
        }
        return "";
    }
}
