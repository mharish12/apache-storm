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

package org.apache.storm.metric.micrometer.util;

import org.apache.storm.DaemonConfig;
import org.apache.storm.metric.micrometer.reporter.JmxReporter;
import org.apache.storm.metric.micrometer.reporter.Reporter;
import org.apache.storm.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricsUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsUtils.class);

    public static List<Reporter> getReporters(Map<String, Object> daemonConf) {
        List<String> classes = (List<String>) daemonConf.get(DaemonConfig.STORM_DAEMON_METRICS_REPORTER_PLUGINS);
        List<Reporter> reporterList = new ArrayList<>();

        if (classes != null) {
            for (String clazz : classes) {
                reporterList.add(getPreparableReporter(clazz));
            }
        }
        if (reporterList.isEmpty()) {
            reporterList.add(new JmxReporter());
        }
        return reporterList;
    }

    private static Reporter getPreparableReporter(String clazz) {
        Reporter reporter = null;
        LOG.info("Using statistics reporter plugin: {}", clazz);
        if (clazz != null) {
            reporter = ReflectionUtils.newInstance(clazz);
        }
        return reporter;
    }
}
