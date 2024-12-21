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

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.storm.metric.ICounter;
import org.apache.storm.metric.IHistogram;
import org.apache.storm.metric.IMeter;
import org.apache.storm.metric.IStormMetric;
import org.apache.storm.metric.ITimer;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public interface StormMetricsPersister {


    IMeter meter(String name);

    IMeter meter(String name, String... tags);

    ICounter counter(String name);

    ICounter counter(String name, String... tags);

    ITimer timer(String name);

    ITimer timer(String name, String... tags);

    IHistogram histogram(String name);

    IHistogram histogram(String name, String... tags);

    <T extends Number> T gauge(String name, T number);

    void registerAll(Map<String, IStormMetric> metrics);

    MeterRegistry getRegistry();

}
