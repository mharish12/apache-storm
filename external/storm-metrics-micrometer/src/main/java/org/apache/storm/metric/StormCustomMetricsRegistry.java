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

import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;
import org.apache.storm.metric.micrometer.reporter.Reporter;
import org.apache.storm.metric.micrometer.util.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class StormCustomMetricsRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(StormCustomMetricsRegistry.class);
    private final StormMetricsPersister stormMetricsPersister;
    private List<Reporter> reporters;
    private boolean reportersStarted = false;

    public StormCustomMetricsRegistry(StormMetricsPersister stormMetricsPersister, List<Reporter> reporters) {
        this.stormMetricsPersister = stormMetricsPersister;
        this.reporters = reporters;
    }

    public IMeter registerMeter(String name) {
        return stormMetricsPersister.meter(name);
    }

    public IMeter registerMeter(String name, String... tags) {
        return stormMetricsPersister.meter(name, tags);
    }

    public ICounter registerCounter(String name) {
        return stormMetricsPersister.counter(name);
    }

    public ICounter registerCounter(String name, String... tags) {
        return stormMetricsPersister.counter(name, tags);
    }

    public IMeter registerMeter(String name, IMeter meter) {
//        return stormMetricsPersister.meter(name, () -> meter);
        return null;
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

    public void registerAll(IMetricSet metrics) {
        stormMetricsPersister.registerAll(metrics.getMetrics());
    }

    public void removeAll(IMetricSet metrics) {
        //Could be replaced when metrics support remove all functions
        // https://github.com/dropwizard/metrics/pull/1280
        Map<String, IStormMetric> nameToMetric = metrics.getMetrics();
//        stormMetricsPersister.removeMatching((name, metric) -> nameToMetric.containsKey(name));
    }

    public IMeter getMeter(String meterName) {
//        return stormMetricsPersister.getMeters().get(meterName);
        return null;
    }

    public void startMetricsReporters(Map<String, Object> daemonConf) {
        reporters = MetricsUtils.getReporters(daemonConf);
        for (Reporter reporter : reporters) {
            reporter.prepare(stormMetricsPersister, daemonConf);
            reporter.start();
            LOG.info("Started statistics report plugin...");
        }
        reportersStarted = true;
    }

    public void stopMetricsReporters() {
        if (reportersStarted) {
            for (Reporter reporter : reporters) {
                reporter.stop();
            }
            reportersStarted = false;
        }
    }
}
