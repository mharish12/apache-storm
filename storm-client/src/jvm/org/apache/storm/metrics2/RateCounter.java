/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.metrics2;

import org.apache.storm.metric.ICounter;
import org.apache.storm.metric.IGauge;
import org.apache.storm.metric.StormCustomMetricsRegistry;

/**
 * A Counter metric that also implements a Gauge to report the average rate of events per second over 1 minute.  This class
 * was added as a compromise to using a Meter, which has a much larger performance impact.
 */
public class RateCounter implements IGauge<Double> {
    private final ICounter counter;
    private double currentRate = 0;
    private int time = 0;
    private final double[] values;
    private final int timeSpanInSeconds;

    RateCounter(StormCustomMetricsRegistry metricRegistry, String metricName, String topologyId,
                String componentId, int taskId, int workerPort, String streamId) {
        if (streamId != null) {
            counter = metricRegistry.registerCounter(metricName, "topologyId", topologyId, "componentId", componentId,
                    "taskId", String.valueOf(taskId), "workerPort", String.valueOf(workerPort), "streamId", streamId);
            metricRegistry.gauge(metricName + ".m1_rate", this, "topologyId", topologyId, "componentId", componentId,
                    "taskId", String.valueOf(taskId), "workerPort", String.valueOf(workerPort), "streamId", streamId);
        } else {
            counter = metricRegistry.registerCounter(metricName, "componentId", componentId, "taskId", String.valueOf(taskId));
            metricRegistry.gauge(metricName + ".m1_rate", this, "componentId", componentId, "taskId", String.valueOf(taskId));
        }

        this.timeSpanInSeconds = Math.max(60 - (60 % metricRegistry.getRateCounterUpdateIntervalSeconds()),
                metricRegistry.getRateCounterUpdateIntervalSeconds());
        this.values = new double[this.timeSpanInSeconds / metricRegistry.getRateCounterUpdateIntervalSeconds() + 1];

    }

    RateCounter(StormCustomMetricsRegistry metricRegistry, String metricName, String topologyId,
                String componentId, int taskId, int workerPort) {
        this(metricRegistry, metricName, topologyId, componentId, taskId, workerPort, null);
    }

    /**
     * Reports the the average rate of events per second over 1 minute for the metric.
     * @return the rate
     */
    @Override
    public Double getValue() {
        return currentRate;
    }

    public void inc(long n) {
        counter.inc(n);
    }

    /**
     * Updates the rate in a background thread by a StormMetricRegistry at a fixed frequency.
     */
    void update() {
        time = (time + 1) % values.length;
        values[time] = counter.getCount();
        currentRate =  ((values[time] - values[(time + 1) % values.length]) / timeSpanInSeconds);
    }

    ICounter getCounter() {
        return counter;
    }
}
