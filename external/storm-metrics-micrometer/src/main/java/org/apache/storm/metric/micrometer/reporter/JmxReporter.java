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

package org.apache.storm.metric.micrometer.reporter;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.apache.storm.metric.micrometer.persister.DropwizardPersister;
import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JmxReporter extends AbstractReporter {

    private static final Logger LOG = LoggerFactory.getLogger(JmxReporter.class);
    private JmxMeterRegistry jmxMeterRegistry;

    @Override
    public void prepare(StormMetricsPersister stormMetricsPersister, Map<String, Object> daemonConf) {
        LOG.info("Preparing...");
        if (stormMetricsPersister instanceof DropwizardPersister) {
            DropwizardMeterRegistry dropwizardMeterRegistry = (DropwizardMeterRegistry) stormMetricsPersister.getRegistry();
            jmxMeterRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT,
                    dropwizardMeterRegistry.getDropwizardRegistry());
        } else {
            jmxMeterRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
        }
    }

    @Override
    public void start() {
        if (jmxMeterRegistry != null) {
            LOG.debug("Starting...");
            jmxMeterRegistry.start();
        } else {
            throw new IllegalStateException("Attempt to start without preparing " + getClass().getSimpleName());
        }
    }

    @Override
    public void stop() {
        if (jmxMeterRegistry != null) {
            LOG.debug("Stopping...");
            jmxMeterRegistry.stop();
        } else {
            throw new IllegalStateException("Attempt to stop without preparing " + getClass().getSimpleName());
        }
    }
}
