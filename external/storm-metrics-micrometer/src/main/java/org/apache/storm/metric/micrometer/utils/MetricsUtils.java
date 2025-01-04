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

package org.apache.storm.metric.micrometer.utils;


import org.apache.storm.metric.collector.MetricsCollector;
import org.apache.storm.metric.config.MetricsConfig;
import org.apache.storm.metric.reporter.JmxReporter;
import org.apache.storm.metric.reporter.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MetricsUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsUtils.class);

    public static List<Reporter> getReporters(Map<String, Object> daemonConf) {
        List<String> classes = (List<String>) daemonConf.get(MetricsConfig.STORM_METRICS_REPORTERS);
        List<Reporter> reporterList = new ArrayList<>();

        if (classes != null) {
            for (String clazz : classes) {
                try {
                    reporterList.add(getPreparableReporter(clazz));
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException | IllegalAccessException e) {
                    LOG.error("Error while preparing reporter: {}", clazz, e);
                }
            }
        }
        if (reporterList.isEmpty()) {
            reporterList.add(new JmxReporter());
        }
        return reporterList;
    }

    private static Reporter getPreparableReporter(String clazz) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Reporter reporter = null;
        LOG.info("Using statistics reporter plugin: {}", clazz);
        if (clazz != null) {
            Class<Reporter> reporterClass = (Class<Reporter>) Class.forName(clazz);
            reporter = reporterClass.getDeclaredConstructor().newInstance();
        }
        return reporter;
    }

    private static MetricsCollector getPreparableCollector(String clazz) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        MetricsCollector metricsCollector = null;
        LOG.info("Using Collector {}", clazz);
        if (clazz != null) {
            Class<MetricsCollector> reporterClass = (Class<MetricsCollector>) Class.forName(clazz);
            metricsCollector = reporterClass.getDeclaredConstructor().newInstance();
        }
        return metricsCollector;
    }


    public static List<MetricsCollector> getCollectors(Map<String, Object> daemonConf) {
        List<String> classes = (List<String>) daemonConf.get(MetricsConfig.STORM_METRICS_COLLECTORS);
        List<MetricsCollector> metricsCollectors = new ArrayList<>();

        if (classes != null) {
            for (String clazz : classes) {
                try {
                    metricsCollectors.add(getPreparableCollector(clazz));
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException | IllegalAccessException e) {
                    LOG.error("Error while preparing collector: {}", clazz, e);
                }
            }
        }
        return metricsCollectors;
    }
}
