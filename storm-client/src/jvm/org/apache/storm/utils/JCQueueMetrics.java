/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.apache.storm.utils;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.storm.metric.IGauge;
import org.apache.storm.metric.internal.RateTracker;
import org.apache.storm.metric.StormCustomMetricsRegistry;
import org.apache.storm.shade.org.jctools.queues.MpscArrayQueue;
import org.apache.storm.shade.org.jctools.queues.MpscUnboundedArrayQueue;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class JCQueueMetrics implements Closeable {
    private final RateTracker arrivalsTracker = new RateTracker(10000, 10);
    private final RateTracker insertFailuresTracker = new RateTracker(10000, 10);
    private final AtomicLong droppedMessages = new AtomicLong(0);

    public JCQueueMetrics(String metricNamePrefix, String topologyId, String componentId, int taskId, int port,
                          StormCustomMetricsRegistry metricRegistry, MpscArrayQueue<Object> receiveQ,
                          MpscUnboundedArrayQueue<Object> overflowQ) {

        IGauge<Integer> cap = new IGauge<Integer>() {
            @Override
            public Integer getValue() {
                return receiveQ.capacity();
            }
        };

        IGauge<Float> pctFull = new IGauge<Float>() {
            @Override
            public Float getValue() {
                return (1.0F * receiveQ.size() / receiveQ.capacity());
            }
        };

        IGauge<Integer> pop = new IGauge<Integer>() {
            @Override
            public Integer getValue() {
                return receiveQ.size();
            }
        };

        IGauge<Double> arrivalRate = new IGauge<Double>() {
            @Override
            public Double getValue() {
                return arrivalsTracker.reportRate();
            }
        };

        IGauge<Double> sojourn = new IGauge<Double>() {
            @Override
            public Double getValue() {
                // Assume the recvQueue is stable, in which the arrival rate is equal to the consumption rate.
                // If this assumption does not hold, the calculation of sojourn time should also consider
                // departure rate according to Queuing Theory.
                return receiveQ.size() / Math.max(arrivalsTracker.reportRate(), 0.00001) * 1000.0;
            }
        };

        IGauge<Double> insertFailures = new IGauge<Double>() {
            @Override
            public Double getValue() {
                return insertFailuresTracker.reportRate();
            }
        };

        IGauge<Long> dropped = new IGauge<Long>() {
            @Override
            public Long getValue() {
                return droppedMessages.get();
            }
        };

        IGauge<Integer> overflow = new IGauge<Integer>() {
            @Override
            public Integer getValue() {
                return overflowQ.size();
            }
        };

        metricRegistry.gauge(metricNamePrefix + "-capacity", cap,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-pct_full", pctFull,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-population", pop,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-arrival_rate_secs", arrivalRate,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-sojourn_time_ms", sojourn,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-insert_failures", insertFailures,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-dropped_messages", dropped,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
        metricRegistry.gauge(metricNamePrefix + "-overflow", overflow,"topologyId", topologyId, "componentId", componentId, "taskId", String.valueOf(taskId), "port", String.valueOf(port));
    }

    public void notifyArrivals(long counts) {
        arrivalsTracker.notify(counts);
    }

    public void notifyInsertFailure() {
        insertFailuresTracker.notify(1);
    }

    public void notifyDroppedMsg() {
        droppedMessages.incrementAndGet();
    }

    @Override
    public void close() {
        arrivalsTracker.close();
        insertFailuresTracker.close();
    }
}
