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

package org.apache.storm.metric.micrometer.persister;


import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import org.apache.storm.metric.ICounter;
import org.apache.storm.metric.IGauge;
import org.apache.storm.metric.IHistogram;
import org.apache.storm.metric.IMeter;
import org.apache.storm.metric.IStormMetric;
import org.apache.storm.metric.ITimer;
import org.apache.storm.metric.micrometer.RateCounter;
import org.apache.storm.metric.micrometer.StormHistogram;
import org.apache.storm.metric.micrometer.StormMeter;
import org.apache.storm.metric.micrometer.StormTimerMetric;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class DropwizardPersister implements StormMetricsPersister {

    private final DropwizardMeterRegistry dropwizardMeterRegistry;

    public DropwizardPersister(DropwizardConfig config, MetricRegistry registry, HierarchicalNameMapper nameMapper, Clock clock, double guageDefault) {
        this.dropwizardMeterRegistry = new DropwizardMeterRegistry(config, registry, nameMapper, clock) {
            @Override
            protected Double nullGaugeValue() {
                return guageDefault;
            }
        };
        Metrics.addRegistry(dropwizardMeterRegistry);
    }

    @Override
    public IMeter meter(String name) {
        return new StormMeter(dropwizardMeterRegistry.counter(name));
    }

    @Override
    public IMeter meter(String name, String... tags) {
        return new StormMeter(dropwizardMeterRegistry.counter(name, tags));
    }

    @Override
    public ICounter counter(String name) {
        return new StormMeter(dropwizardMeterRegistry.counter(name));
    }

    @Override
    public ICounter counter(String name, String... tags) {
        return new StormMeter(dropwizardMeterRegistry.counter(name, tags));
    }

    @Override
    public RateCounter rateCounter(String name) {
        return null;
    }

    @Override
    public RateCounter rateCounter(String name, String... tags) {
        return null;
    }

    @Override
    public ITimer timer(String name) {
        return new StormTimerMetric(dropwizardMeterRegistry.timer(name));
    }

    @Override
    public ITimer timer(String name, String... tags) {
        return new StormTimerMetric(dropwizardMeterRegistry.timer(name, tags));
    }

    @Override
    public IHistogram histogram(String name) {
        return new StormHistogram(dropwizardMeterRegistry.summary(name));
    }

    @Override
    public IHistogram histogram(String name, String... tags) {
        return new StormHistogram(dropwizardMeterRegistry.summary(name, tags));
    }

    @Override
    public <T extends Number> T gauge(String name, T number) {
        return dropwizardMeterRegistry.gauge(name, number);
    }

    @Override
    public <T extends Number> T gauge(String name, T number, IGauge<T> gauge, String... tags) {
        return dropwizardMeterRegistry.gauge(name, Tags.of(tags), number, value -> gauge.getValue().doubleValue());
    }

    @Override
    public void registerAll(Map<String, IStormMetric> metrics) {
        //TODO: implement registerAll API.
    }

    @Override
    public MeterRegistry getRegistry() {
        return dropwizardMeterRegistry;
    }

    @Override
    public String getMetricsAsText() {
        MetricRegistry metricRegistry = dropwizardMeterRegistry.getDropwizardRegistry();
        StringBuilder metricsText = new StringBuilder();

        // Iterate over the metrics and format them
        metricRegistry.getMetrics().forEach((name, metric) -> {
            metricsText.append(name).append(" = ");
            if (metric instanceof com.codahale.metrics.Counter) {
                metricsText.append(((com.codahale.metrics.Counter) metric).getCount());
            } else if (metric instanceof com.codahale.metrics.Gauge) {
                metricsText.append(((com.codahale.metrics.Gauge<?>) metric).getValue());
            } else if (metric instanceof com.codahale.metrics.Histogram) {
                metricsText.append(((com.codahale.metrics.Histogram) metric).getSnapshot().getMean());
            } else if (metric instanceof com.codahale.metrics.Meter) {
                metricsText.append(((com.codahale.metrics.Meter) metric).getCount());
            } else if (metric instanceof com.codahale.metrics.Timer) {
                metricsText.append(((com.codahale.metrics.Timer) metric).getSnapshot().getMean());
            } else {
                metricsText.append("Unsupported metric type: ").append(metric.getClass().getName());
            }
            metricsText.append("\n");
        });
        return metricsText.toString();
    }


}
