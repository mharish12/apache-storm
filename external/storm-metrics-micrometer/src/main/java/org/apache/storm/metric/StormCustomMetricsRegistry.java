/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.storm.metric;

import com.codahale.metrics.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import org.apache.storm.metric.collector.MetricsCollector;
import org.apache.storm.metric.micrometer.RateCounter;
import org.apache.storm.metric.micrometer.persister.PrometheusPersister;
import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;
import org.apache.storm.metric.reporter.Reporter;
import org.apache.storm.metric.micrometer.utils.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class StormCustomMetricsRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(StormCustomMetricsRegistry.class);

    private static final int RATE_COUNTER_UPDATE_INTERVAL_SECONDS = 2;

    private final StormMetricsPersister stormMetricsPersister;
    private List<MetricsCollector> metricsCollectors;
    private List<Reporter> reporters;
    private boolean reportersStarted = false;
    private boolean collectorsStarted = false;

    public StormCustomMetricsRegistry() {
        this(new PrometheusPersister(), Collections.emptyList(), Collections.emptyList());
    }

    public StormCustomMetricsRegistry(StormMetricsPersister stormMetricsPersister, List<Reporter> reporters, List<MetricsCollector> metricsCollectors) {
        this.stormMetricsPersister = stormMetricsPersister;
        this.reporters = reporters;
        this.metricsCollectors = metricsCollectors;
        new JvmGcMetrics().bindTo(stormMetricsPersister.getRegistry());
        new JvmThreadMetrics().bindTo(stormMetricsPersister.getRegistry());
        new JvmMemoryMetrics().bindTo(stormMetricsPersister.getRegistry());
    }

    public IMeter registerMeter(String name) {
        return stormMetricsPersister.meter(name);
    }

    public IMeter registerMeter(String name, String... tags) {
        return stormMetricsPersister.meter(name, tags);
    }

    public IMeter registerMeter(String name, IMeter meter) {
//        return stormMetricsPersister.meter(name, () -> meter);
        return null;
    }

    public ICounter registerCounter(String name) {
        return stormMetricsPersister.counter(name);
    }

    public ICounter registerCounter(String name, String... tags) {
        return stormMetricsPersister.counter(name, tags);
    }

    public RateCounter rateCounter(String name) {
        return stormMetricsPersister.rateCounter(name);
    }

    public RateCounter rateCounter(String name, String... tags) {
        return stormMetricsPersister.rateCounter(name, tags);
    }

    public ITimer registerTimer(String name) {
        return stormMetricsPersister.timer(name);
    }

    public ITimer registerTimer(String name, String... tags) {
        return stormMetricsPersister.timer(name, tags);
    }

    public IHistogram registerHistogram(String name) {
        return stormMetricsPersister.histogram(name);
    }

    public IHistogram registerHistogram(String name, String... tags) {
        return stormMetricsPersister.histogram(name, tags);
    }

    public <T extends Number> T registerGauge(final String name, T number) {
        return stormMetricsPersister.gauge(name, number);
    }

    public <T extends Number> T registerGauge(final String name, IGauge<T> gauge) {
//        return stormMetricsPersister.gauge(name, number);
        //TODO: Implementation left.
        return null;
    }

    public <T extends Number> IGauge<T> gauge(String name, IGauge<T> gauge, String... tags) {
        stormMetricsPersister.gauge(name, gauge.getValue(), gauge, tags);
        return gauge;
    }

    public <T extends Number> Gauge<T> gauge(String name, Gauge<T> gauge, String componentId, Integer taskId) {
        return gauge;
    }

    public void registerAll(IMetricSet metrics) {
        stormMetricsPersister.registerAll(metrics.getMetrics());
    }

    public void removeAll(IMetricSet metrics) {
        Map<String, IStormMetric> nameToMetric = metrics.getMetrics();
//        stormMetricsPersister.removeMatching((name, metric) -> nameToMetric.containsKey(name));
    }

    public IMeter getMeter(String meterName) {
//        return stormMetricsPersister.getMeters().get(meterName);
        return null;
    }

    private void startMetricsReporters(Map<String, Object> daemonConf) {
        reporters = MetricsUtils.getReporters(daemonConf);
        for (Reporter reporter : reporters) {
            reporter.prepare(stormMetricsPersister, daemonConf);
            reporter.start();
            LOG.info("Started statistics report plugin...");
        }
        reportersStarted = true;
    }

    private void startMetricsCollectors(Map<String, Object> daemonConf) {
        metricsCollectors = MetricsUtils.getCollectors(daemonConf);
        for (MetricsCollector metricsCollector : metricsCollectors) {
            metricsCollector.prepare(stormMetricsPersister, daemonConf);
            metricsCollector.start();
            LOG.info("Started collectors...");
        }
        collectorsStarted = true;
    }

    public void startMetricsComponents(Map<String, Object> daemonConf) {
        startMetricsReporters(daemonConf);
        startMetricsCollectors(daemonConf);
    }

    private void stopMetricsReporters() {
        if (reportersStarted) {
            for (Reporter reporter : reporters) {
                reporter.stop();
            }
            reportersStarted = false;
        }
    }

    private void stopMetricsCollectors() {
        if (collectorsStarted) {
            for (MetricsCollector metricsCollector : metricsCollectors) {
                metricsCollector.stop();
            }
            collectorsStarted = false;
        }
    }

    public void stopMetricsComponents() {
        stopMetricsReporters();
        stopMetricsCollectors();
    }

    public void deregister(Set<IStormMetric> metrics) {

    }

    public int getRateCounterUpdateIntervalSeconds() {
        return RATE_COUNTER_UPDATE_INTERVAL_SECONDS;
    }

    public Map<String, IGauge> getTaskGauges(int taskId) {
        return getTaskGauges(String.valueOf(taskId));
    }

    public Map<String, IGauge> getTaskGauges(String taskId) {
        return null;
    }

    public Map<String, ICounter> getTaskCounters(int taskId) {
        return getTaskCounters(String.valueOf(taskId));
    }

    public Map<String, ICounter> getTaskCounters(String taskId) {
        return null;
    }

    public Map<String, IHistogram> getTaskHistograms(int taskId) {
        return getTaskHistograms(String.valueOf(taskId));
    }

    public Map<String, IHistogram> getTaskHistograms(String taskId) {
        return null;
    }
    public Map<String, IMeter> getTaskMeters(int taskId) {
        return getTaskMeters(String.valueOf(taskId));
    }

    public Map<String, IMeter> getTaskMeters(String taskId) {
        return null;
    }
    public Map<String, ITimer> getTaskTimers(int taskId) {
        return getTaskTimers(String.valueOf(taskId));
    }

    public Map<String, ITimer> getTaskTimers(String taskId) {
        return null;
    }

    public void stop() {
        for (Reporter sr : reporters) {
            sr.stop();
        }
    }

    protected StormMetricsPersister getStormMetricsPersister() {
        return stormMetricsPersister;
    }

    public String getMetricsAsText() {
        return stormMetricsPersister.getMetricsAsText();
    }

}
