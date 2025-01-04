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


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.apache.storm.metric.*;
import org.apache.storm.metric.micrometer.RateCounter;
import org.apache.storm.metric.micrometer.StormHistogram;
import org.apache.storm.metric.micrometer.StormMeter;
import org.apache.storm.metric.micrometer.StormTimerMetric;

import java.util.Map;
import java.util.function.ToDoubleFunction;

@SuppressWarnings("SpellCheckingInspection")
public class PrometheusPersister implements StormMetricsPersister {

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    public PrometheusPersister() {
        prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Metrics.addRegistry(prometheusMeterRegistry);
    }

    @Override
    public IMeter meter(String name) {
        Counter counter = prometheusMeterRegistry.counter(name);
        return new StormMeter(counter);
    }

    @Override
    public IMeter meter(String name, String... tags) {
        return new StormMeter(prometheusMeterRegistry.counter(name, tags));
    }

    @Override
    public ICounter counter(String name) {
        Counter counter = prometheusMeterRegistry.counter(name);
        return new StormMeter(counter);
    }

    @Override
    public ICounter counter(String name, String... tags) {
        return new StormMeter(prometheusMeterRegistry.counter(name, tags));
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
        return new StormTimerMetric(prometheusMeterRegistry.timer(name));
    }

    @Override
    public ITimer timer(String name, String... tags) {
        return new StormTimerMetric(prometheusMeterRegistry.timer(name, tags));
    }

    @Override
    public IHistogram histogram(String name) {
        return new StormHistogram(prometheusMeterRegistry.summary(name));
    }

    @Override
    public IHistogram histogram(String name, String... tags) {
        return new StormHistogram(prometheusMeterRegistry.summary(name, tags));
    }

    @Override
    public <T extends Number> T gauge(String name, T number) {
        return prometheusMeterRegistry.gauge(name, number);
    }

    @Override
    public <T extends Number> T gauge(String name, T number, IGauge<T> gauge, String... tags) {
        return prometheusMeterRegistry.gauge(name, Tags.of(tags), number, value -> gauge.getValue().doubleValue());
    }

    @Override
    public void registerAll(Map<String, IStormMetric> metrics) {
        //TODO: update the register all logic.
    }

    @Override
    public MeterRegistry getRegistry() {
        return prometheusMeterRegistry;
    }

    @Override
    public String getMetricsAsText() {
        return prometheusMeterRegistry.scrape();
    }
}

