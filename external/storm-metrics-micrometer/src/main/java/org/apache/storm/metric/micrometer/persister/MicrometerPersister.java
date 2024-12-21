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
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.apache.storm.metric.ICounter;
import org.apache.storm.metric.IHistogram;
import org.apache.storm.metric.IMeter;
import org.apache.storm.metric.IStormMetric;
import org.apache.storm.metric.ITimer;
import org.apache.storm.metric.micrometer.StormHistogram;
import org.apache.storm.metric.micrometer.StormMeter;
import org.apache.storm.metric.micrometer.StormTimer;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class MicrometerPersister implements StormMetricsPersister {

    private final MeterRegistry registry = new CompositeMeterRegistry();

    @Override
    public IMeter meter(String name) {
        Counter counter = registry.counter(name);
        return new StormMeter(counter);
    }

    @Override
    public IMeter meter(String name, String... tags) {
        return null;
    }

    @Override
    public ICounter counter(String name) {
        return null;
    }

    @Override
    public ICounter counter(String name, String... tags) {
        return null;
    }

    @Override
    public ITimer timer(String name) {
        Timer timer = registry.timer(name);
        return new StormTimer(timer);
    }

    @Override
    public ITimer timer(String name, String... tags) {
        return null;
    }

    @Override
    public IHistogram histogram(String name) {
        DistributionSummary summary = registry.summary(name);
        return new StormHistogram(registry.summary(name));
    }

    @Override
    public IHistogram histogram(String name, String... tags) {
        return new StormHistogram(registry.summary(name, tags));
    }

    @Override
    public <T extends Number> T gauge(String name, T number) {
        return registry.gauge(name, number);
    }

    @Override
    public void registerAll(Map<String, IStormMetric> metrics) {
        //TODO: implement regiterAll
    }

    @Override
    public MeterRegistry getRegistry() {
        return registry;
    }

}

